package it.smartphonecombo.uecapabilityparser.model.json

import it.smartphonecombo.uecapabilityparser.extension.getObject
import it.smartphonecombo.uecapabilityparser.extension.getObjectAtPath
import it.smartphonecombo.uecapabilityparser.extension.repeat
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

sealed interface UENrRrcCapabilityJson {
    val rootJson: JsonElement
    val nrRrcCapabilityV1560: JsonObject?
}

class UEMrdcCapabilityJson(override val rootJson: JsonObject) : UENrRrcCapabilityJson {
    override val nrRrcCapabilityV1560 =
        rootJson.getObjectAtPath("nonCriticalExtension".repeat(4, "."))
}

class UENrCapabilityJson(override val rootJson: JsonObject) : UENrRrcCapabilityJson {
    override val nrRrcCapabilityV1560 =
        rootJson.getObjectAtPath("nonCriticalExtension".repeat(4, "."))

    val nrRrcCapabilityV1690 =
        nrRrcCapabilityV1560?.getObjectAtPath("nonCriticalExtension".repeat(5, "."))

    val nrRrcCapabilityV1700 = nrRrcCapabilityV1690?.getObject("nonCriticalExtension")
}
