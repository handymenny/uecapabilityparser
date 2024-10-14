package it.smartphonecombo.uecapabilityparser.server

import io.javalin.json.JsonMapper
import it.smartphonecombo.uecapabilityparser.extension.custom
import java.lang.reflect.Type
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Suppress("UNCHECKED_CAST")
object CustomJsonMapper : JsonMapper {
    override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
        val deserializer = serializer(targetType) as KSerializer<T>
        return Json.custom().decodeFromString(deserializer, json)
    }

    override fun toJsonString(obj: Any, type: Type): String {
        val serializer = serializer(obj.javaClass)
        return Json.custom().encodeToString(serializer, obj)
    }
}
