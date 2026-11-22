package it.smartphonecombo.uecapabilityparser.model.pcap.capability

import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.Rat

// Cap from RRC
class UeCapInfo(
    data: ByteArrayDeepEquals,
    ratTypes: Set<Rat>,
    timestamp: Long,
    val isNrRrc: Boolean,
    val arfcn: Int,
    val ip: String?,
) {
    val ratTypes: Set<Rat>
        field = ratTypes.toMutableSet()

    val data: List<ByteArrayDeepEquals>
        field = mutableListOf(data)

    val timestamps: List<Long>
        field = mutableListOf(timestamp)

    fun addData(data: List<ByteArrayDeepEquals>, ratTypes: Set<Rat>, timestamp: Long) {
        this.data.addAll(data)
        this.ratTypes.addAll(ratTypes)
        timestamps.add(timestamp)
    }
}
