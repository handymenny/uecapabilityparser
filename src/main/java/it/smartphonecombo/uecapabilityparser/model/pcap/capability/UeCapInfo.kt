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
    private val _ratTypes = ratTypes.toMutableSet()
    private val _data = mutableListOf(data)
    private val _timestamps = mutableListOf(timestamp)

    val ratTypes: Set<Rat>
        get() = _ratTypes

    val data: List<ByteArrayDeepEquals>
        get() = _data

    val timestamps: List<Long>
        get() = _timestamps

    fun addData(data: List<ByteArrayDeepEquals>, ratTypes: Set<Rat>, timestamp: Long) {
        this._data.addAll(data)
        this._ratTypes.addAll(ratTypes)
        this._timestamps.add(timestamp)
    }
}
