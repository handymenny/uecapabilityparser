package it.smartphonecombo.uecapabilityparser.server

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.EndpointGroup
import io.javalin.config.SizeUnit
import io.javalin.http.ContentType
import io.javalin.http.Handler
import io.javalin.http.HttpStatus
import io.javalin.http.staticfiles.Location
import it.smartphonecombo.uecapabilityparser.extension.badRequest
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.hasRat
import it.smartphonecombo.uecapabilityparser.extension.internalError
import it.smartphonecombo.uecapabilityparser.extension.throwContentTooLargeIfContentTooLarge
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.io.NullInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JavalinApp {
    private val hasSubmodules = {}.javaClass.getResourceAsStream("/web") != null
    private val html404 = {}.javaClass.getResourceAsStream("/web/404.html")?.use { it.readBytes() }
    private val endpoints = mutableListOf<String>()
    private val maxRequestSize = Config["maxRequestSize"]?.toLong() ?: (256 * 1000 * 1000)
    private val compression = Config["compression"] == "true"
    private val maxOutputCache = Config.getOrDefault("cache", "0").toInt().takeIf { it >= 0 }
    private val store = Config["store"]
    private var index = LibraryIndex()

    init {
        if (store != null) {
            index = LibraryIndex.buildIndex(store, maxOutputCache)
            val reparseStrategy = Config.getOrDefault("reparse", "off")
            if (reparseStrategy != "off") {
                CoroutineScope(Dispatchers.IO).launch {
                    reparseLibrary(reparseStrategy, store, index, compression)
                }
            }
        }
    }

    fun newServer(): Javalin {
        val server =
            Javalin.create { config ->
                config.http.gzipOnlyCompression(4)
                config.http.prefer405over404 = true

                // align all request size limits
                config.http.maxRequestSize = maxRequestSize
                config.jetty.multipartConfig.maxFileSize(maxRequestSize, SizeUnit.BYTES)
                config.jetty.multipartConfig.maxTotalRequestSize(maxRequestSize, SizeUnit.MB)
                config.jetty.multipartConfig.maxInMemoryFileSize(20, SizeUnit.MB)

                config.router.treatMultipleSlashesAsSingleSlash = true
                config.router.apiBuilder(buildRoutes(store, index, compression))
                config.jsonMapper(CustomJsonMapper)
                config.bundledPlugins.enableCors { cors -> cors.addRule { it.anyHost() } }

                if (hasSubmodules) {
                    config.staticFiles.add("/web", Location.CLASSPATH)
                    config.staticFiles.add { staticFiles ->
                        staticFiles.hostedPath = "/swagger"
                        staticFiles.directory = "/swagger"
                        staticFiles.location = Location.CLASSPATH
                    }
                }
            }

        server.exception(Exception::class.java) { e, ctx ->
            e.printStackTrace()
            if (e is IllegalArgumentException || e is NullPointerException) {
                ctx.badRequest()
            } else {
                ctx.internalError()
            }
        }

        server.error(HttpStatus.NOT_FOUND) { ctx ->
            if (html404 != null) {
                ctx.contentType(ContentType.HTML)
                ctx.result(html404)
            }
        }

        return server
    }

    private suspend fun reparseLibrary(
        strategy: String,
        store: String,
        index: LibraryIndex,
        compression: Boolean,
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
                .map { async { reparseItem(it, index, store, compression) } }
                .awaitAll()
        }
    }

    private fun reparseItem(
        indexLine: IndexLine,
        index: LibraryIndex,
        store: String,
        compression: Boolean,
    ) {
        val compressed = indexLine.compressed
        val capPath = "/output/${indexLine.id}.json"
        try {
            val capText =
                IOUtils.inputSourceAndMove("$store$capPath", "$store/backup$capPath", compressed)
                    ?: NullInputSource

            val capabilities = Json.custom().decodeFromInputSource<Capabilities>(capText)
            val inputMap =
                indexLine.inputs.mapNotNull {
                    IOUtils.inputSourceAndMove(
                        "$store/input/$it",
                        "$store/backup/input/$it",
                        compressed,
                    )
                }

            val request =
                RequestParse.buildRequest(
                    *inputMap.toTypedArray(),
                    type = capabilities.logType,
                    description = indexLine.description,
                    ratList = guessRats(capabilities, inputMap.size),
                )

            Parsing.fromRequest(request)?.let {
                // Reset capabilities id and timestamp
                it.capabilities.id = capabilities.id
                it.capabilities.timestamp = capabilities.timestamp
                it.store(null, store, compression)
            } ?: throw NullPointerException("Reparsed Capabilities is null")

            val newLine = indexLine.copy(compressed = compression)
            index.replaceLine(newLine)
        } catch (ex: Exception) {
            echoSafe("Error re-parsing ${indexLine.id}:\t${ex.message}", true)
            try {
                // restore prev version
                IOUtils.copy("$store/backup$capPath", "$store$capPath", compressed)
                indexLine.inputs.forEach {
                    IOUtils.copy("$store/backup/input/$it", "$store/input/$it", compressed)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun guessRats(capabilities: Capabilities, inputsLength: Int): List<Rat> {
        if (capabilities.logType != LogType.H) {
            return listOf(Rat.EUTRA)
        }

        val defaultRatList = listOf(Rat.EUTRA, Rat.NR, Rat.EUTRA_NR)

        if (inputsLength == 3) {
            return defaultRatList
        }

        val ratList = mutableListOf<Rat>()
        if (capabilities.lteBands.isNotEmpty() || capabilities.ueCapFilters.hasRat(Rat.EUTRA)) {
            ratList.add(Rat.EUTRA)
        }
        if (capabilities.nrBands.isNotEmpty() || capabilities.ueCapFilters.hasRat(Rat.NR)) {
            ratList.add(Rat.NR)
        }
        if (
            capabilities.enDcCombos.isNotEmpty() || capabilities.ueCapFilters.hasRat(Rat.EUTRA_NR)
        ) {
            ratList.add(Rat.EUTRA_NR)
        }

        if (ratList.isEmpty()) {
            return defaultRatList
        }

        return ratList
    }

    private fun buildRoutes(store: String?, index: LibraryIndex, compression: Boolean) =
        EndpointGroup {
            ApiBuilder.before { ctx -> ctx.throwContentTooLargeIfContentTooLarge(maxRequestSize) }

            if (hasSubmodules) {
                endpoints.add("/swagger")
                // Add / if missing
                ApiBuilder.before("/swagger") { ctx ->
                    if (!ctx.path().endsWith("/")) {
                        ctx.redirect("/swagger/")
                    }
                }
                addRoute("/openapi", "/swagger/openapi.json") { Routes.getOpenApi(it) }
            }

            // Add custom js and custom css
            addStaticGet("custom.js", Config["customJs"], ContentType.TEXT_JS)
            addStaticGet("custom.css", Config["customCss"], ContentType.TEXT_CSS)

            addRoute("/parse", post = true) { Routes.parse(it, store, index, compression) }
            addRoute("/parse/multiPart", post = true) {
                Routes.parseMultiPart(it, store, index, compression)
            }

            addRoute("/csv", post = true) { Routes.csv(it) }

            if (store != null) {
                addRoute("/store/list") { Routes.storeList(it, index) }
                addRoute("/store/getItem") { Routes.storeGetItem(it, index) }
                addRoute("/store/getMultiItem") { Routes.storeGetMultiItem(it, index) }
                addRoute("/store/getOutput") { Routes.storeGetOutput(it, index, store) }
                addRoute("/store/getMultiOutput") { Routes.storeGetMultiOutput(it, index, store) }
                addRoute("/store/getInput") { Routes.storeGetInput(it, index, store) }
                addRoute("/store/list/filtered", post = true) {
                    Routes.storeListFiltered(it, index, store)
                }
            }

            addRoute("/status") { Routes.status(it, maxRequestSize, endpoints) }
        }

    private fun addRoute(vararg paths: String, post: Boolean = false, handler: Handler) {
        for (path in paths) {
            if (post) {
                ApiBuilder.post(path, handler)
            } else {
                ApiBuilder.get(path, handler)
            }
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
        addRoute(webPath) { ctx ->
            ctx.result(source.inputStream())
            ctx.contentType(contentType)
        }
    }
}
