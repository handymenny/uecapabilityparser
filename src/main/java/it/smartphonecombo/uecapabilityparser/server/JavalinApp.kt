package it.smartphonecombo.uecapabilityparser.server

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.config.SizeUnit
import io.javalin.http.ContentType
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.HttpStatus
import io.javalin.http.staticfiles.Location
import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.attachFile
import it.smartphonecombo.uecapabilityparser.extension.badRequest
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.getArray
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.internalError
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.notFound
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.IO
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

@OptIn(ExperimentalCoroutinesApi::class)
class JavalinApp {
    private val jsonMapper =
        object : JsonMapper {
            override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
                @Suppress("UNCHECKED_CAST")
                val deserializer = serializer(targetType) as KSerializer<T>
                return Json.custom().decodeFromString(deserializer, json)
            }

            override fun toJsonString(obj: Any, type: Type): String {
                val serializer = serializer(obj.javaClass)
                return Json.custom().encodeToString(serializer, obj)
            }
        }
    private val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    private val html404 = {}.javaClass.getResourceAsStream("/web/404.html")?.readAllBytes()

    val app: Javalin =
        Javalin.create { config ->
            config.compression.gzipOnly(4)
            config.http.prefer405over404 = true
            config.http.maxRequestSize = 256L * SizeUnit.MB.multiplier
            config.routing.treatMultipleSlashesAsSingleSlash = true
            config.jsonMapper(jsonMapper)
            config.plugins.enableCors { cors -> cors.add { it.anyHost() } }
            config.staticFiles.add("/web", Location.CLASSPATH)
            config.staticFiles.add { staticFiles ->
                staticFiles.hostedPath = "/swagger"
                staticFiles.directory = "/swagger"
                staticFiles.location = Location.CLASSPATH
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
            // Add / if missing
            ApiBuilder.before("/swagger") { ctx ->
                if (!ctx.path().endsWith("/")) {
                    ctx.redirect("/swagger/")
                }
            }

            apiBuilderPost("/parse", "/parse/0.1.0") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val parsed =
                    Parsing.fromJsonRequest(request) ?: return@apiBuilderPost ctx.badRequest()
                ctx.json(parsed.capabilities)
                if (store != null) {
                    parsed.store(index, store, compression)
                }
            }
            apiBuilderPost("/parse/multi") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val parsed =
                    MultiParsing.fromJsonRequest(request) ?: return@apiBuilderPost ctx.badRequest()
                ctx.json(parsed.getMultiCapabilities())
                if (store != null) {
                    parsed.store(index, store, compression)
                }
            }
            apiBuilderPost("/csv", "/csv/0.1.0") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val type = request.getString("type")
                val input = request.getArray("input")

                if (input == null || type == null) {
                    return@apiBuilderPost ctx.badRequest()
                }
                val comboList =
                    when (type) {
                        "lteca" -> Json.decodeFromJsonElement<List<ComboLte>>(input)
                        "endc" -> Json.decodeFromJsonElement<List<ComboEnDc>>(input)
                        "nrca" -> Json.decodeFromJsonElement<List<ComboNr>>(input)
                        "nrdc" -> Json.decodeFromJsonElement<List<ComboNrDc>>(input)
                        else -> emptyList()
                    }
                val date = dataFormatter.format(ZonedDateTime.now(ZoneOffset.UTC))
                ctx.attachFile(
                    IO.toCsv(comboList).toByteArray(),
                    "${type}-${date}.csv",
                    ContentType.TEXT_CSV
                )
            }

            apiBuilderGet("/openapi", "/swagger/openapi.json", handler = ::getOpenApi)

            apiBuilderGet("/store/status", "/store/0.2.0/status") { ctx ->
                val enabled = store != null
                val json = buildJsonObject { put("enabled", enabled) }
                ctx.json(json)
            }

            if (store != null) {
                apiBuilderGet("/store/list", "/store/0.2.0/list") { ctx -> ctx.json(index) }
                apiBuilderGet("/store/getItem", "/store/0.2.0/getItem") { ctx ->
                    val id = ctx.queryParam("id") ?: return@apiBuilderGet ctx.badRequest()
                    val item = index.find(id) ?: return@apiBuilderGet ctx.notFound()
                    ctx.json(item)
                }
                apiBuilderGet("/store/getMultiItem") { ctx ->
                    val id = ctx.queryParam("id") ?: return@apiBuilderGet ctx.badRequest()
                    val item = index.findMulti(id) ?: return@apiBuilderGet ctx.notFound()
                    ctx.json(item)
                }
                apiBuilderGet("/store/getOutput", "/store/0.2.0/getOutput") { ctx ->
                    val id = ctx.queryParam("id")
                    if (id == null || !id.matches(idRegex)) {
                        return@apiBuilderGet ctx.badRequest()
                    }

                    val indexLine = index.findByOutput(id) ?: return@apiBuilderGet ctx.notFound()
                    val compressed = indexLine.compressed
                    val filePath = "$store/output/$id.json"

                    try {
                        val text =
                            IO.readTextFromFile(filePath, compressed)
                                ?: return@apiBuilderGet ctx.notFound()
                        val capabilities = Json.custom().decodeFromString<Capabilities>(text)
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
                                IO.readTextFromFile(filePath, compressed)
                                    ?: return@apiBuilderGet ctx.notFound()
                            val capabilities = Json.custom().decodeFromString<Capabilities>(text)
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
                apiBuilderGet("/store/getInput", "/store/0.2.0/getInput") { ctx ->
                    val id = ctx.queryParam("id")
                    if (id == null || !id.matches(idRegex)) {
                        return@apiBuilderGet ctx.badRequest()
                    }

                    val indexLine = index.findByInput(id) ?: return@apiBuilderGet ctx.notFound()
                    val compressed = indexLine.compressed
                    val filePath = "$store/input/$id"

                    try {
                        val bytes =
                            IO.readBytesFromFile(filePath, compressed)
                                ?: return@apiBuilderGet ctx.notFound()

                        ctx.attachFile(bytes, id, ContentType.APPLICATION_OCTET_STREAM)
                    } catch (ex: Exception) {
                        ctx.internalError()
                    }
                }
            }

            ApiBuilder.get("/version") { ctx ->
                val version = Config.getOrDefault("project.version", "")
                val json = buildJsonObject { put("version", version) }
                ctx.json(json)
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
            IO.createDirectories("$store/backup/output/")
            IO.createDirectories("$store/backup/input/")
            index
                .getAll()
                .filterNot { auto && it.parserVersion == parserVersion }
                .map { async { reparseItem(it, store, compression) } }
                .awaitAll()
        }
    }

    private fun reparseItem(indexLine: IndexLine, store: String, compression: Boolean) {
        val base64 = Base64.getEncoder()

        try {
            val compressed = indexLine.compressed
            val capPath = "/output/${indexLine.id}.json"
            val text =
                IO.readAndMove(
                        "$store$capPath",
                        "$store/backup$capPath",
                        compressed,
                    )
                    ?.decodeToString()
                    ?: return

            val capabilities = Json.decodeFromString<Capabilities>(text)
            val inputMap =
                indexLine.inputs.mapNotNull { input ->
                    val path = "/input/$input"
                    val bytes =
                        IO.readAndMove(
                            "$store$path",
                            "$store/backup$path",
                            compressed,
                        )
                    bytes?.let { base64.encodeToString(bytes) }
                }

            val json =
                buildParseJsonRequest(
                    *inputMap.toTypedArray(),
                    type = capabilities.logType,
                    description = indexLine.description,
                    defaultNR =
                        indexLine.defaultNR ||
                            capabilities.lteBands.isEmpty() && capabilities.nrBands.isNotEmpty(),
                )

            Parsing.fromJsonRequest(json)?.let {
                // Reset capabilities id and timestamp
                it.capabilities.id = capabilities.id
                it.capabilities.timestamp = capabilities.timestamp
                it.store(null, store, compression)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun buildParseJsonRequest(
        vararg inputs: String,
        type: String,
        description: String?,
        defaultNR: Boolean
    ): JsonObject {
        // We don't support more than 3 inputs for type H and 1 for others
        val maxInputs = if (type == "H") 3 else 1
        val inputSize = minOf(inputs.size, maxInputs)

        return buildJsonObject {
            for (i in 0 until inputSize) {
                val input = inputs[i]
                if (i == 0) {
                    put("input", input)
                } else if (i == 2 || i == 1 && type == "H" && defaultNR) {
                    put("inputENDC", input)
                } else {
                    put("inputNR", input)
                }
            }

            description?.let { put("description", it) }
            put("type", type)
            put("defaultNR", defaultNR)
        }
    }

    private fun apiBuilderGet(vararg paths: String, handler: Handler) {
        for (path in paths) {
            ApiBuilder.get(path, handler)
        }
    }

    private fun apiBuilderPost(vararg paths: String, handler: Handler) {
        for (path in paths) {
            ApiBuilder.post(path, handler)
        }
    }
}
