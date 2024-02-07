package it.smartphonecombo.uecapabilityparser.server

import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.custom
import java.io.InputStream
import java.lang.reflect.Type
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
object CustomJsonMapper : JsonMapper {
    override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
        val deserializer = serializer(targetType) as KSerializer<T>
        return Json.custom().decodeFromString(deserializer, json)
    }

    override fun <T : Any> fromJsonStream(json: InputStream, targetType: Type): T {
        val deserializer = serializer(targetType) as KSerializer<T>
        return Json.custom().decodeFromStream(deserializer, json)
    }

    override fun toJsonString(obj: Any, type: Type): String {
        val serializer = serializer(obj.javaClass)
        return Json.custom().encodeToString(serializer, obj)
    }
}
