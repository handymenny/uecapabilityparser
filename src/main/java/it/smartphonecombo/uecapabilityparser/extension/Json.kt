package it.smartphonecombo.uecapabilityparser.extension

import io.javalin.config.SizeUnit
import it.smartphonecombo.uecapabilityparser.io.GzipFileInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

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
