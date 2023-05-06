package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BandNrDetails(
    @SerialName("band") var band: Band,
    @SerialName("bwClassDl") var classDL: BwClass = BwClass.NONE,
    @SerialName("bwClassUl") var classUL: BwClass = BwClass.NONE,
    @SerialName("mimoDl") var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimoUl") var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulationDl") var modDL: Modulation = EmptyModulation,
    @SerialName("modulationUl") var modUL: Modulation = EmptyModulation,
    @SerialName("maxUplinkDutyCycle") var maxUplinkDutyCycle: Int = 100,
    @SerialName("powerClass") var powerClass: Int = 3,
    @SerialName("bandwidths") var bandwidths: List<BwsNr> = emptyList(),
    @SerialName("rateMatchingLteCrs") var rateMatchingLteCrs: Boolean = false
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
