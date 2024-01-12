package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.EmptyBandwidth
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation

open class InternMap<T>(maxCapacity: Int) {
    private val internalMap: LinkedHashMap<T, T> =
        object : LinkedHashMap<T, T>(minOf(16, maxCapacity), 0.75f) {
            override fun removeEldestEntry(eldest: Map.Entry<T, T>): Boolean {
                return size > maxCapacity
            }
        }

    private fun put(value: T): T {
        internalMap[value] = value
        return value
    }

    fun intern(value: T): T = internalMap[value] ?: put(value)
}

object MimoInternMap : InternMap<Mimo>(100)

object ModulationInternMap : InternMap<Modulation>(100)

object BandwidthInternMap : InternMap<Bandwidth>(100)

internal fun Mimo.intern(): Mimo = if (this == EmptyMimo) this else MimoInternMap.intern(this)

internal fun Modulation.intern() =
    if (this == EmptyModulation) this else ModulationInternMap.intern(this)

internal fun Bandwidth.intern() =
    if (this == EmptyBandwidth) this else BandwidthInternMap.intern(this)
