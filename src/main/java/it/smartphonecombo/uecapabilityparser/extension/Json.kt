package it.smartphonecombo.uecapabilityparser.extension

import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// custom Json instance
internal fun Json.custom() = json
