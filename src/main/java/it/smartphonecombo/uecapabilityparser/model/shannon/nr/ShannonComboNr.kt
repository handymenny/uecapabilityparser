@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.nr

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("ComboGroup")
data class ComboNrGroup(
    /** Some features that applies to the whole combo group. */
    @ProtoNumber(1) val comboFeatures: ComboNrFeatures,
    /** List of combos that share the same [ComboNrFeatures]. */
    @ProtoNumber(2) val combos: List<ShannonComboNr> = emptyList(),
)

@Serializable
@SerialName("Combo")
data class ShannonComboNr(
    /** List of Components. */
    @ProtoNumber(1) val components: List<ShannonComponentNr> = emptyList(),
    /** A bit mask stored as unsigned int that enables or disables this combo for specific SKUs. */
    @ProtoNumber(2) val skuBitMask: Long
)

@Serializable
@SerialName("ComboFeatures")
data class ComboNrFeatures(
    /**
     * The supportedBandwidthCombinationSet that applies to the Nr Components.
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString. 0 means default i.e. only BCS 0 supported (if applicable).
     */
    @ProtoNumber(1) @SerialName("bcsNr") private val rawBcsNr: Long? = null,

    /**
     * The supportedBandwidthCombinationSet that applies to the IntraEnDc Components
     * (supportedBandwidthCombinationSetIntraENDC).
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString. 0 means default i.e. only BCS 0 supported (if applicable).
     */
    @ProtoNumber(2) @SerialName("bcsIntraEndc") private val rawBcsIntraEndc: Long? = null,

    /**
     * The supported Bandwidth Combination Set that applies to the Eutra Components
     * (supportedBandwidthCombinationSetEUTRA-v1530).
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString. 0 means default i.e. only BCS 0 supported (if applicable).
     */
    @ProtoNumber(3) @SerialName("bcsEutra") private val rawBcsEutra: Long? = null,

    /**
     * Power Class of the whole combination, it's stored as an enum.
     *
     * Note that this doesn't override the powerclass of the uplink bands.
     *
     * For FR1 0 -> Default, 1 -> PC2, 2 -> PC1.5
     *
     * For FR2 0 -> Default
     */
    @ProtoNumber(4) @SerialName("powerClass") private val rawPowerClass: Int? = null,

    /**
     * IntraBandENDC-Support is stored as an enum.
     *
     * 0 -> contiguous, 1 -> non-contiguous, 2 -> both.
     */
    @ProtoNumber(5)
    @SerialName("intraBandEnDcSupport")
    private val rawIntraBandEnDcSupport: Int? = null,
    /** SupportedNumberTAG is stored as unsigned int. */
    @ProtoNumber(6) val supportedNumTag: Long? = null,
) {
    val bcsNr
        get() = convertRawBcs(rawBcsNr)

    val bcsIntraEndc
        get() = convertRawBcs(rawBcsIntraEndc)

    val bcsEutra
        get() = convertRawBcs(rawBcsEutra)

    val powerClass
        get() =
            when (rawPowerClass) {
                1 -> PowerClass.PC2
                2 -> PowerClass.PC1dot5
                else -> PowerClass.NONE
            }

    private fun convertRawBcs(bcs: Long?): BCS {
        return when (bcs) {
            0L -> BCS.fromQualcommCP("0")
            null -> EmptyBCS
            else -> bcs.toString(2).padStart(32, '0').let { BCS.fromBinaryString(it) }
        }
    }
}
