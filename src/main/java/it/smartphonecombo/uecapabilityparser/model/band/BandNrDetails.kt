package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder

data class BandNrDetails(
    var band: Band = 0,
    var mimoDL: Mimo = EmptyMimo,
    var mimoUL: Mimo = EmptyMimo,
    var modDL: ModulationOrder = ModulationOrder.NONE,
    var modUL: ModulationOrder = ModulationOrder.NONE,
    var maxUplinkDutyCycle: Int = 100,
    var powerClass: Int = 3,
    var bandwidths: List<BwsNr> = emptyList(),
    var rateMatchingLteCrs: Boolean = false
) : Comparable<BandNrDetails> {

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

    val isFR2: Boolean
        get() = band > 256
}
