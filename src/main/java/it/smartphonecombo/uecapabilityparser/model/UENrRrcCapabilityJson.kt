package it.smartphonecombo.uecapabilityparser.model

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

sealed interface UENrRrcCapabilityJson {
    val rootJson: JsonElement
}

class UEMrdcCapabilityJson(override val rootJson: JsonObject) : UENrRrcCapabilityJson

class UENrCapabilityJson(override val rootJson: JsonObject) : UENrRrcCapabilityJson
