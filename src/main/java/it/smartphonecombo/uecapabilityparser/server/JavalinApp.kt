package it.smartphonecombo.uecapabilityparser.server

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.config.SizeUnit
import io.javalin.http.ContentType
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.HttpStatus
import io.javalin.http.servlet.throwContentTooLargeIfContentTooLarge
import io.javalin.http.staticfiles.Location
import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.attachFile
import it.smartphonecombo.uecapabilityparser.extension.badRequest
import it.smartphonecombo.uecapabilityparser.extension.bodyAsClassEfficient
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.internalError
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.notFound
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.NullInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class JavalinApp {
    private val jsonMapper =
        object : JsonMapper {
            override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
                @Suppress("UNCHECKED_CAST")
                val deserializer = serializer(targetType) as KSerializer<T>
                return Json.custom().decodeFromString(deserializer, json)
            }

            override fun <T : Any> fromJsonStream(json: InputStream, targetType: Type): T {
                @Suppress("UNCHECKED_CAST")
                val deserializer = serializer(targetType) as KSerializer<T>
                return Json.custom().decodeFromStream(deserializer, json)
            }

            override fun toJsonString(obj: Any, type: Type): String {
                val serializer = serializer(obj.javaClass)
                return Json.custom().encodeToString(serializer, obj)
            }
        }
    private val hasSubmodules = {}.javaClass.getResourceAsStream("/web") != null
    private val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    private val html404 = {}.javaClass.getResourceAsStream("/web/404.html")?.use { it.readBytes() }
    private val endpoints = mutableListOf<String>()
    private val maxRequestSize = Config["maxRequestSize"]?.toLong() ?: (256 * 1000 * 1000)
    val app: Javalin =
        Javalin.create { config ->
            config.compression.gzipOnly(4)
            config.http.prefer405over404 = true

            // align all request size limits
            config.http.maxRequestSize = maxRequestSize
            config.jetty.multipartConfig.maxFileSize(maxRequestSize, SizeUnit.BYTES)
            config.jetty.multipartConfig.maxTotalRequestSize(maxRequestSize, SizeUnit.MB)
            config.jetty.multipartConfig.maxInMemoryFileSize(20, SizeUnit.MB)

            config.routing.treatMultipleSlashesAsSingleSlash = true
            config.jsonMapper(jsonMapper)
            config.plugins.enableCors { cors -> cors.add { it.anyHost() } }
            if (hasSubmodules) {
                config.staticFiles.add("/web", Location.CLASSPATH)
                config.staticFiles.add { staticFiles ->
                    staticFiles.hostedPath = "/swagger"
                    staticFiles.directory = "/swagger"
                    staticFiles.location = Location.CLASSPATH
                }
            }
        }

    init {
        val store = Config["store"]
        val compression = Config["compression"] == "true"
        var index: LibraryIndex =
            store?.let { LibraryIndex.buildIndex(it) } ?: LibraryIndex(mutableListOf())
        val idRegex = "[a-f0-9-]{36}(?:-[0-9]+)?".toRegex()

        val reparseStrategy = Config.getOrDefault("reparse", "off")
        if (store != null && reparseStrategy != "off") {
            CoroutineScope(Dispatchers.IO).launch {
                reparseLibrary(reparseStrategy, store, index, compression)
                // Rebuild index
                index = LibraryIndex.buildIndex(store)
            }
        }

        app.exception(Exception::class.java) { e, _ -> e.printStackTrace() }
        app.error(HttpStatus.NOT_FOUND) { ctx ->
            if (html404 != null) {
                ctx.contentType(ContentType.HTML)
                ctx.result(html404)
            }
        }
        app.routes {
            ApiBuilder.before { ctx -> ctx.throwContentTooLargeIfContentTooLarge() }

            endpoints.add("/swagger")
            // Add / if missing
            ApiBuilder.before("/swagger") { ctx ->
                if (!ctx.path().endsWith("/")) {
                    ctx.redirect("/swagger/")
                }
            }

            // Add custom js and custom css
            addStaticGet("custom.js", Config["customJs"], ContentType.TEXT_JS)
            addStaticGet("custom.css", Config["customCss"], ContentType.TEXT_CSS)

            apiBuilderPost("/parse") { ctx ->
                try {
                    val request = ctx.bodyAsClassEfficient<RequestParse>()
                    val parsed = Parsing.fromRequest(request)!!
                    ctx.json(parsed.capabilities)
                    if (store != null) {
                        parsed.store(index, store, compression)
                    }
                } catch (_: Exception) {
                    return@apiBuilderPost ctx.badRequest()
                }
            }
            apiBuilderPost("/parse/multiPart") { ctx ->
                try {
                    val requestsStr = ctx.formParam("requests")!!
                    val requestsJson =
                        Json.custom().decodeFromString<List<RequestMultiPart>>(requestsStr)
                    val files = ctx.uploadedFiles()
                    val parsed = MultiParsing.fromRequest(requestsJson, files)!!
                    ctx.json(parsed.getMultiCapabilities())
                    if (store != null) {
                        parsed.store(index, store, compression)
                    }
                } catch (_: Exception) {
                    return@apiBuilderPost ctx.badRequest()
                }
            }
            apiBuilderPost("/csv") { ctx ->
                try {
                    val request = ctx.bodyAsClassEfficient<RequestCsv>()
                    val comboList = request.input
                    val type = request.type
                    val date = dataFormatter.format(ZonedDateTime.now(ZoneOffset.UTC))
                    val newFmt = (request as? RequestCsv.LteCa)?.newCsvFormat ?: false
                    ctx.attachFile(
                        IOUtils.toCsv(comboList, newFmt).toInputSource(),
                        "${type}-${date}.csv",
                        ContentType.TEXT_CSV
                    )
                } catch (_: Exception) {
                    return@apiBuilderPost ctx.badRequest()
                }
            }

            if (hasSubmodules) {
                apiBuilderGet("/openapi", "/swagger/openapi.json", handler = ::getOpenApi)
            }

            apiBuilderGet("/store/status") { ctx ->
                val enabled = store != null
                val json = buildJsonObject { put("enabled", enabled) }
                ctx.json(json)
            }

            if (store != null) {
                apiBuilderGet("/store/list") { ctx -> ctx.json(index) }
                apiBuilderGet("/store/getItem") { ctx ->
                    val id = ctx.queryParam("id") ?: return@apiBuilderGet ctx.badRequest()
                    val item = index.find(id) ?: return@apiBuilderGet ctx.notFound()
                    ctx.json(item)
                }
                apiBuilderGet("/store/getMultiItem") { ctx ->
                    val id = ctx.queryParam("id") ?: return@apiBuilderGet ctx.badRequest()
                    val item = index.findMulti(id) ?: return@apiBuilderGet ctx.notFound()
                    ctx.json(item)
                }
                apiBuilderGet("/store/getOutput") { ctx ->
                    val id = ctx.queryParam("id")
                    if (id == null || !id.matches(idRegex)) {
                        return@apiBuilderGet ctx.badRequest()
                    }

                    val indexLine = index.findByOutput(id) ?: return@apiBuilderGet ctx.notFound()
                    val compressed = indexLine.compressed
                    val filePath = "$store/output/$id.json"

                    try {
                        val text =
                            IOUtils.getInputSource(filePath, compressed)
                                ?: return@apiBuilderGet ctx.notFound()
                        val capabilities = Json.custom().decodeFromInputSource<Capabilities>(text)
                        ctx.json(capabilities)
                    } catch (ex: Exception) {
                        ctx.internalError()
                    }
                }
                apiBuilderGet("/store/getMultiOutput") { ctx ->
                    val id = ctx.queryParam("id")
                    if (id == null || !id.matches(idRegex)) {
                        return@apiBuilderGet ctx.badRequest()
                    }

                    val multiIndexLine = index.findMulti(id) ?: return@apiBuilderGet ctx.notFound()
                    val indexLineIds = multiIndexLine.indexLineIds
                    val capabilitiesList = mutableListWithCapacity<Capabilities>(indexLineIds.size)
                    try {
                        for (indexId in indexLineIds) {
                            val indexLine = index.find(indexId) ?: continue
                            val compressed = indexLine.compressed
                            val outputId = indexLine.id
                            val filePath = "$store/output/$outputId.json"
                            val text =
                                IOUtils.getInputSource(filePath, compressed)
                                    ?: return@apiBuilderGet ctx.notFound()
                            val capabilities =
                                Json.custom().decodeFromInputSource<Capabilities>(text)
                            capabilitiesList.add(capabilities)
                        }
                    } catch (ex: Exception) {
                        ctx.internalError()
                    }
                    val multiCapabilities =
                        MultiCapabilities(
                            capabilitiesList,
                            multiIndexLine.description,
                            multiIndexLine.id
                        )
                    ctx.json(multiCapabilities)
                }
                apiBuilderGet("/store/getInput") { ctx ->
                    val id = ctx.queryParam("id")
                    if (id == null || !id.matches(idRegex)) {
                        return@apiBuilderGet ctx.badRequest()
                    }

                    val indexLine = index.findByInput(id) ?: return@apiBuilderGet ctx.notFound()
                    val compressed = indexLine.compressed
                    val filePath = "$store/input/$id"

                    try {
                        val file =
                            IOUtils.getInputSource(filePath, compressed)
                                ?: return@apiBuilderGet ctx.notFound()
                        ctx.attachFile(file, id, ContentType.APPLICATION_OCTET_STREAM)
                    } catch (ex: Exception) {
                        ctx.internalError()
                    }
                }
            }

            apiBuilderGet("/version") { ctx ->
                val version = Config.getOrDefault("project.version", "")
                val json = buildJsonObject { put("version", version) }
                ctx.json(json)
            }

            apiBuilderGet("/status") { ctx ->
                val version = Config.getOrDefault("project.version", "")
                val logTypes = LogType.validEntries
                val requestMaxSize = app.cfg.http.maxRequestSize
                val status = ServerStatus(version, endpoints, logTypes, requestMaxSize)
                ctx.json(status)
            }
        }
    }

    private fun getOpenApi(ctx: Context) {
        val openapi = {}.javaClass.getResourceAsStream("/swagger/openapi.json")
        if (openapi != null) {
            val text = openapi.reader().readText()
            ctx.contentType(ContentType.JSON)
            ctx.result(text)
        }
    }

    private suspend fun reparseLibrary(
        strategy: String,
        store: String,
        index: LibraryIndex,
        compression: Boolean
    ) {
        val parserVersion = Config.getOrDefault("project.version", "")
        val auto = strategy !== "force"
        val threadCount = minOf(Runtime.getRuntime().availableProcessors(), 2)
        val dispatcher = Dispatchers.IO.limitedParallelism(threadCount)

        withContext(dispatcher) {
            IOUtils.createDirectories("$store/backup/output/")
            IOUtils.createDirectories("$store/backup/input/")
            index
                .getAll()
                .filterNot { auto && it.parserVersion == parserVersion }
                .map { async { reparseItem(it, store, compression) } }
                .awaitAll()
        }
    }

    private fun reparseItem(indexLine: IndexLine, store: String, compression: Boolean) {
        try {
            val compressed = indexLine.compressed
            val capPath = "/output/${indexLine.id}.json"
            val capText =
                IOUtils.inputSourceAndMove("$store$capPath", "$store/backup$capPath", compressed)
                    ?: NullInputSource

            val capabilities = Json.custom().decodeFromInputSource<Capabilities>(capText)
            val inputMap =
                indexLine.inputs.mapNotNull {
                    IOUtils.inputSourceAndMove(
                        "$store/input/$it",
                        "$store/backup/input/$it",
                        compressed
                    )
                }

            val request =
                RequestParse.buildRequest(
                    *inputMap.toTypedArray(),
                    type = capabilities.logType,
                    description = indexLine.description,
                    defaultNR =
                        indexLine.defaultNR ||
                            capabilities.lteBands.isEmpty() && capabilities.nrBands.isNotEmpty()
                )

            Parsing.fromRequest(request)?.let {
                // Reset capabilities id and timestamp
                it.capabilities.id = capabilities.id
                it.capabilities.timestamp = capabilities.timestamp
                it.store(null, store, compression)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun apiBuilderGet(vararg paths: String, handler: Handler) {
        for (path in paths) {
            ApiBuilder.get(path, handler)
            endpoints.add(path)
        }
    }

    private fun addStaticGet(webPath: String, filePath: String?, contentType: ContentType) {
        val source =
            if (filePath.isNullOrEmpty()) {
                NullInputSource
            } else {
                File(filePath).toInputSource()
            }
        apiBuilderGet(webPath) { ctx ->
            ctx.result(source.inputStream())
            ctx.contentType(contentType)
        }
    }

    private fun apiBuilderPost(vararg paths: String, handler: Handler) {
        for (path in paths) {
            ApiBuilder.post(path, handler)
            endpoints.add(path)
        }
    }
}
