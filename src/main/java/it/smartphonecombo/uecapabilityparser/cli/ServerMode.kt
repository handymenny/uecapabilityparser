package it.smartphonecombo.uecapabilityparser.cli

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.config.SizeUnit
import io.javalin.http.HttpStatus
import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.getArray
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.util.Output
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer

object ServerMode {
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

    fun run(port: Int) {
        val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")

        val app =
            Javalin.create { config ->
                    config.compression.gzipOnly(4)
                    config.http.prefer405over404 = true
                    config.http.maxRequestSize = 100L * SizeUnit.MB.multiplier
                    config.routing.treatMultipleSlashesAsSingleSlash = true
                    config.jsonMapper(jsonMapper)
                }
                .apply {
                    exception(Exception::class.java) { e, _ -> e.printStackTrace() }
                    error(HttpStatus.NOT_FOUND) { ctx -> ctx.json("not found") }
                }
                .start(port)

        app.routes {
            post("/parse/0.0.7") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val input = request.getString("input")?.let { base64.decode(it).inputStream() }
                val inputNR = request.getString("inputNR")?.let { base64.decode(it).inputStream() }
                val inputENDC =
                    request.getString("inputENDC")?.let { base64.decode(it).inputStream() }
                val defaultNR = request.getString("defaultNR")?.let { it.toBoolean() } ?: false
                val multiple0xB826 =
                    request.getString("multiple0xB826")?.let { it.toBoolean() } ?: false
                val type = request.getString("type")

                if (input == null || type == null) {
                    ctx.result("Bad Request")
                    ctx.status(HttpStatus.BAD_REQUEST)
                } else {
                    val parsing =
                        Parsing(input, inputNR, inputENDC, defaultNR, multiple0xB826, type)
                    ctx.json(parsing.capabilities)
                }
            }
            post("/csv/0.0.7") { ctx ->
                val request = Json.parseToJsonElement(ctx.body())
                val type = request.getString("type")
                val input = request.getArray("input")

                if (input == null || type == null) {
                    ctx.result("Bad Request")
                    ctx.status(HttpStatus.BAD_REQUEST)
                } else {
                    val comboList =
                        when (type) {
                            "lteca" -> Json.decodeFromJsonElement<List<ComboLte>>(input)
                            "endc" -> Json.decodeFromJsonElement<List<ComboEnDc>>(input)
                            "nrca" -> Json.decodeFromJsonElement<List<ComboNr>>(input)
                            "nrdc" -> Json.decodeFromJsonElement<List<ComboNrDc>>(input)
                            else -> emptyList()
                        }
                    val date = dataFormatter.format(ZonedDateTime.now(ZoneOffset.UTC))
                    ctx.result(Output.toCsv(comboList))
                        .contentType("text/csv")
                        .header("Content-Disposition", "attachment; filename=${type}-${date}.csv")
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread { app.stop() })
    }
}
