package it.smartphonecombo.uecapabilityparser.model.pcap

import kotlinx.serialization.json.JsonArray

class UeCapRatContainers(
    val ratContainers: JsonArray,
    val timestamp: Long,
    val isNrRrc: Boolean,
) {

    val messageName
        get() = if (isNrRrc) "UE Radio Cap Info Indication" else "UE Cap Info Indication"
}
