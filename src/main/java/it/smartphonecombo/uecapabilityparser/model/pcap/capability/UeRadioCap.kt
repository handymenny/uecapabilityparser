package it.smartphonecombo.uecapabilityparser.model.pcap.capability

import kotlinx.serialization.json.JsonArray

//  Cap from S1/NG
class UeRadioCap(
    val ratContainers: JsonArray,
    val timestamp: Long,
    val isNrRrc: Boolean,
) {

    val messageName
        get() = if (isNrRrc) "UE Radio Cap Info Indication" else "UE Cap Info Indication"
}
