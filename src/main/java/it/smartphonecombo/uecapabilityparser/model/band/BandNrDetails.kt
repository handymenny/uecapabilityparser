package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BandNrDetails(
    @SerialName("band") override var band: Band,
    @SerialName("mimoDl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimoUl") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulationDl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulationUl") override var modUL: Modulation = EmptyModulation,
    @SerialName("maxUplinkDutyCycle") var maxUplinkDutyCycle: Int = 100,
    @SerialName("powerClass") override var powerClass: PowerClass = PowerClass.PC3,
    @SerialName("bandwidths") var bandwidths: List<BwsNr> = emptyList(),
    @SerialName("rateMatchingLteCrs") var rateMatchingLteCrs: Boolean = false
) : IBandDetails, Comparable<BandNrDetails> {

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

    fun bw90MHzSupported(): Boolean {
        return bandwidths.any { bwsNr -> bwsNr.bwsDL.contains(90) || bwsNr.bwsUL.contains(90) }
    }

    val isFR2: Boolean
        get() = band > 256
}
