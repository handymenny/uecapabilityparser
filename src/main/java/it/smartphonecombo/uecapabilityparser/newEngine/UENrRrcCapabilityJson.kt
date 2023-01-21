package it.smartphonecombo.uecapabilityparser.newEngine

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

sealed interface UENrRrcCapabilityJson {
    val rootJson: JsonElement
}

class UEMrdcCapabilityJsonUE(override val rootJson: JsonObject) : UENrRrcCapabilityJson
class UENrCapabilityJson(override val rootJson: JsonObject) : UENrRrcCapabilityJson
