package it.smartphonecombo.uecapabilityparser.extension

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

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
