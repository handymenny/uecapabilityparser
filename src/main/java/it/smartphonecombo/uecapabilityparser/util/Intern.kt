package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.EmptyBandwidth
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation

open class InternMap<T>(maxCapacity: Int) {
    @Transient private val lock = Any()

    private val internalMap: LinkedHashMap<T, T> =
        object : LinkedHashMap<T, T>(computeInitialCapacity(maxCapacity)) {
            override fun removeEldestEntry(eldest: Map.Entry<T, T>): Boolean {
                return size > maxCapacity
            }
        }

    private fun put(value: T): T {
        synchronized(lock) { internalMap[value] = value }
        return value
    }

    fun intern(value: T): T = internalMap[value] ?: put(value)

    companion object {
        private fun computeInitialCapacity(maxCapacity: Int): Int {
            // A value that ensures no re-hash is maxCapacity / 0.75 + 1
            // Compute that value / 2
            val initialCapacity = Math.floorDiv(maxCapacity * 2, 3) + 1

            return maxOf(16, initialCapacity)
        }
    }
}

object MimoInternMap : InternMap<Mimo>(100)

object ModulationInternMap : InternMap<Modulation>(100)

object BandwidthInternMap : InternMap<Bandwidth>(100)

internal fun Mimo.intern(): Mimo = if (this == EmptyMimo) this else MimoInternMap.intern(this)

internal fun Modulation.intern() =
    if (this == EmptyModulation) this else ModulationInternMap.intern(this)

internal fun Bandwidth.intern() =
    if (this == EmptyBandwidth) this else BandwidthInternMap.intern(this)
