package it.smartphonecombo.uecapabilityparser.extension

import io.javalin.config.SizeUnit
import it.smartphonecombo.uecapabilityparser.io.GzipFileInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

private val json = Json { ignoreUnknownKeys = true }

// custom Json instance
internal fun Json.custom() = json

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T> Json.decodeFromInputSource(input: InputSource): T {
    val stream = input is GzipFileInputSource || input.size() > SizeUnit.MB.multiplier

    return if (stream) {
        input.inputStream().use { decodeFromStream<T>(it) }
    } else {
        decodeFromString<T>(input.readText())
    }
}

internal fun JsonElement.getInt(key: String) =
    ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.intOrNull

internal fun JsonElement.getString(key: String) =
    ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.contentOrNull

internal fun JsonElement.getObject(key: String) = (this as? JsonObject)?.get(key) as? JsonObject

internal fun JsonElement.getArray(key: String) = (this as? JsonObject)?.get(key) as? JsonArray

internal fun JsonElement.getObjectAtPath(path: String): JsonObject? {
    var obj = this as? JsonObject
    path.split(".").forEach { obj = obj?.getObject(it) }
    return obj
}

internal fun JsonElement.getArrayAtPath(path: String): JsonArray? {
    val split = path.split(".")
    var obj = this as? JsonObject
    for (i in 0 until split.size - 1) {
        obj = obj?.getObject(split[i])
    }
    return obj?.getArray(split.last())
}

internal fun JsonElement.asIntOrNull(): Int? {
    return (this as? JsonPrimitive)?.intOrNull
}

internal fun JsonElement.asArrayOrNull(): JsonArray? {
    return this as? JsonArray
}
