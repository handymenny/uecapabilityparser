package it.smartphonecombo.uecapabilityparser.server

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.config.SizeUnit
import io.javalin.http.ContentType
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.staticfiles.Location
import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.attachFile
import it.smartphonecombo.uecapabilityparser.extension.badRequest
import it.smartphonecombo.uecapabilityparser.extension.getArray
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.internalError
import it.smartphonecombo.uecapabilityparser.extension.notFound
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Output
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.io.File
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

class JavalinApp {
    private val base64 = Base64.getDecoder()
    private val jsonMapper =
        object : JsonMapper {
            override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
                @Suppress("UNCHECKED_CAST")
                val deserializer = serializer(targetType) as KSerializer<T>
                return Json.decodeFromString(deserializer, json)
            }

            override fun toJsonString(obj: Any, type: Type): String {
                val serializer = serializer(obj.javaClass)
                return Json.encodeToString(serializer, obj)
            }
        }
    private val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    private val html404 = {}.javaClass.getResourceAsStream("/web/404.html")?.readAllBytes()
    private val openapi =
        {}.javaClass
            .getResourceAsStream("/swagger/openapi.json")
            ?.bufferedReader()
            ?.readText()
            ?.replace("http://localhost:8080", "/")

    val app: Javalin =
        Javalin.create { config ->
            config.compression.gzipOnly(4)
            config.http.prefer405over404 = true
            config.http.maxRequestSize = 100L * SizeUnit.MB.multiplier
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
        val index: LibraryIndex =
            store?.let { LibraryIndex.buildIndex(it) } ?: LibraryIndex(mutableListOf())
        val idRegex = "[a-f0-9-]{36}(?:-[0-9]+)?".toRegex()

        app.exception(Exception::class.java) { e, _ -> e.printStackTrace() }
        app.error(HttpStatus.NOT_FOUND) { ctx ->
            if (html404 != null) {
                ctx.contentType(ContentType.HTML)
                ctx.result(html404)
            }
        }
        app.routes {
            ApiBuilder.post("/parse/0.1.0") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val input = request.getString("input")?.let { base64.decode(it) }
                val inputNR = request.getString("inputNR")?.let { base64.decode(it) }
                val inputENDC = request.getString("inputENDC")?.let { base64.decode(it) }
                val defaultNR =
                    request.getString("defaultNR")?.let { it.toBoolean() } ?: (input == null)
                val multiple0xB826 =
                    request.getString("multiple0xB826")?.let { it.toBoolean() } ?: false
                val type = request.getString("type")

                if (input == null && inputNR == null || type == null) {
                    return@post ctx.badRequest()
                }
                val parsing =
                    Parsing(input ?: inputNR!!, inputNR, inputENDC, defaultNR, multiple0xB826, type)
                val description = request.getString("description")
                if (description != null) {
                    parsing.capabilities.setMetadata("description", description)
                }
                ctx.json(parsing.capabilities)
                if (store != null) {
                    parsing.store(index, store)
                }
            }
            ApiBuilder.post("/csv/0.1.0") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val type = request.getString("type")
                val input = request.getArray("input")

                if (input == null || type == null) {
                    return@post ctx.badRequest()
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
                    Output.toCsv(comboList).toByteArray(),
                    "${type}-${date}.csv",
                    ContentType.TEXT_CSV
                )
            }
            ApiBuilder.get("/openapi", ::getOpenApi)
            ApiBuilder.get("/swagger/openapi.json", ::getOpenApi)
            // Add / if missing
            ApiBuilder.before("/swagger") { ctx ->
                if (!ctx.path().endsWith("/")) {
                    ctx.redirect("/swagger/")
                }
            }
            ApiBuilder.get("/store/0.2.0/status") { ctx ->
                val enabled = store != null
                val json = buildJsonObject { put("enabled", enabled) }
                ctx.json(json)
            }
            ApiBuilder.get("/store/0.2.0/list") { ctx -> ctx.json(index) }
            ApiBuilder.get("/store/0.2.0/getItem") { ctx ->
                val id = ctx.queryParam("id") ?: return@get ctx.badRequest()
                val item = index.find(id) ?: return@get ctx.notFound()
                ctx.json(item)
            }

            ApiBuilder.get("/store/0.2.0/getOutput") { ctx ->
                val id = ctx.queryParam("id")
                if (id == null || !id.matches(idRegex)) {
                    return@get ctx.badRequest()
                }

                val file = File("$store/output/$id.json")
                if (!file.exists()) {
                    return@get ctx.notFound()
                }
                try {
                    val capabilities = Json.decodeFromString<Capabilities>(file.readText())
                    ctx.json(capabilities)
                } catch (ex: Exception) {
                    ctx.internalError()
                }
            }
            ApiBuilder.get("/store/0.2.0/getInput") { ctx ->
                val id = ctx.queryParam("id")
                if (id == null || !id.matches(idRegex)) {
                    return@get ctx.badRequest()
                }

                val file = File("$store/input/$id")
                if (!file.exists()) {
                    return@get ctx.notFound()
                }
                try {
                    ctx.attachFile(file.readBytes(), id, ContentType.APPLICATION_OCTET_STREAM)
                } catch (ex: Exception) {
                    ctx.internalError()
                }
            }
        }
    }

    private fun getOpenApi(ctx: Context) {
        if (openapi != null) {
            ctx.contentType(ContentType.JSON)
            ctx.result(openapi)
        }
    }
}
