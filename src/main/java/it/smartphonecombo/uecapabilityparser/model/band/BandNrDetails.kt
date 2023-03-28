package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.Modulation

data class BandNrDetails(
    var band: Band = 0,
    var mimoDL: Mimo = 0,
    var mimoUL: Mimo = 0,
    var modDL: Modulation = Modulation.NONE,
    var modUL: Modulation = Modulation.NONE
) : Comparable<BandNrDetails> {
    var maxUplinkDutyCycle = 100
    var powerClass = 3
    var bandwidthsDL: Map<Int, IntArray>? = null
    var bandwidthsUL: Map<Int, IntArray>? = null
    var rateMatchingLteCrs = false

    override fun compareTo(other: BandNrDetails): Int {
        val bandCmp = band.compareTo(other.band)

        if (bandCmp != 0) {
            return bandCmp
        }

        // Return 0 only if they're equal
        return if (this == other) 0 else -1
    }

    fun bwsToString(): String {
        val dlString =
            bandwidthsDL
                ?.entries
                ?.filter { it.value.isNotEmpty() }
                ?.joinToString(
                    prefix = "BwDL:[",
                    postfix = "]",
                    transform = { "${it.key}kHz: ${it.value.joinToString()}" },
                    separator = "; ",
                )
        val ulString =
            bandwidthsUL
                ?.entries
                ?.filter { it.value.isNotEmpty() }
                ?.joinToString(
                    prefix = "BwUL:[",
                    postfix = "]",
                    transform = { "${it.key}kHz: ${it.value.joinToString()}" },
                    separator = "; ",
                )
        return "n$band $dlString $ulString"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BandNrDetails) return false

        if (band != other.band) return false
        if (mimoDL != other.mimoDL) return false
        if (mimoUL != other.mimoUL) return false
        if (modDL != other.modDL) return false
        if (modUL != other.modUL) return false
        if (maxUplinkDutyCycle != other.maxUplinkDutyCycle) return false
        if (powerClass != other.powerClass) return false
        if (rateMatchingLteCrs != other.rateMatchingLteCrs) return false
        if (bwsToString() != other.bwsToString()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = band
        result = 31 * result + mimoDL
        result = 31 * result + mimoUL
        result = 31 * result + modDL.hashCode()
        result = 31 * result + modUL.hashCode()
        result = 31 * result + maxUplinkDutyCycle
        result = 31 * result + powerClass
        result = 31 * result + rateMatchingLteCrs.hashCode()
        result = 31 * result + bwsToString().hashCode()

        return result
    }

    val isFR2: Boolean
        get() = band > 256
}
