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
    @SerialName("band") var band: Band = 0,
    @SerialName("bw-class-dl") var classDL: BwClass = BwClass.NONE,
    @SerialName("bw-class-ul") var classUL: BwClass = BwClass.NONE,
    @SerialName("mimo-dl") var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimo-ul") var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulation-dl") var modDL: Modulation = EmptyModulation,
    @SerialName("modulation-ul") var modUL: Modulation = EmptyModulation,
    @SerialName("max-uplink-duty-cycle") var maxUplinkDutyCycle: Int = 100,
    @SerialName("power-class") var powerClass: Int = 3,
    @SerialName("bandwidths") var bandwidths: List<BwsNr> = emptyList(),
    @SerialName("rate-matching-lte-crs") var rateMatchingLteCrs: Boolean = false
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
