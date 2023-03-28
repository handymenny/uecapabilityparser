package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr

data class BandNrDetails(
    var band: Band = 0,
    var mimoDL: Mimo = 0,
    var mimoUL: Mimo = 0,
    var modDL: Modulation = Modulation.NONE,
    var modUL: Modulation = Modulation.NONE
) : Comparable<BandNrDetails> {
    var maxUplinkDutyCycle = 100
    var powerClass = 3
    var bandwidths: Array<BwsNr> = emptyArray()
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
            bandwidths
                .filter { it.bwsDL.isNotEmpty() }
                .joinToString(
                    prefix = "BwDL:[",
                    postfix = "]",
                    transform = { "${it.scs}kHz: ${it.bwsDL.joinToString()}" },
                    separator = "; ",
                )
        val ulString =
            bandwidths
                .filter { it.bwsUL.isNotEmpty() }
                .joinToString(
                    prefix = "BwUL:[",
                    postfix = "]",
                    transform = { "${it.scs}kHz: ${it.bwsUL.joinToString()}" },
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

        return bandwidths.contentEquals(other.bandwidths)
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

        return 31 * result + bandwidths.contentHashCode()
    }

    override fun toString(): String {
        return "BandNrDetails(band=$band, mimoDL=$mimoDL, mimoUL=$mimoUL, modDL=$modDL, modUL=$modUL, maxUplinkDutyCycle=$maxUplinkDutyCycle, powerClass=$powerClass, bandwidths=${bandwidths.contentToString()}, rateMatchingLteCrs=$rateMatchingLteCrs)"
    }

    val isFR2: Boolean
        get() = band > 256
}
