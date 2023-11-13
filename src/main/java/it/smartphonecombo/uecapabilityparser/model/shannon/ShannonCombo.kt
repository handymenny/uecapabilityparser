@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ComboGroup(
    /** Some features that applies to the whole combo group. */
    @ProtoNumber(1) val comboFeatures: ComboFeatures,
    /** List of combos that share the same [ComboFeatures]. */
    @ProtoNumber(2) val combos: List<ShannonCombo> = emptyList(),
)

@Serializable
data class ShannonCombo(
    /** List of Components. */
    @ProtoNumber(1) val components: List<ShannonComponent> = emptyList(),
    /** A bit mask stored as unsigned int that enables or disables some features. */
    @ProtoNumber(2) val bitMask: UInt
)

@Serializable
data class ComboFeatures(
    /**
     * The supportedBandwidthCombinationSet that applies to the Nr Components.
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString.
     */
    @ProtoNumber(1) private val rawBcsNr: UInt = 0u,

    /**
     * The supportedBandwidthCombinationSet that applies to the IntraEnDc Components
     * (supportedBandwidthCombinationSetIntraENDC).
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString.
     */
    @ProtoNumber(2) private val rawBcsIntraEndc: UInt = 0u,

    /**
     * The supported Bandwidth Combination Set that applies to the Eutra Components
     * (supportedBandwidthCombinationSetEUTRA-v1530).
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString.
     */
    @ProtoNumber(3) private val rawBcsEutra: UInt = 0u,

    /**
     * Power Class of the whole combination, it's stored as an enum.
     *
     * Note that this doesn't override the powerclass of the uplink bands.
     *
     * For FR1 0 -> Default, 1 -> PC2, 2 -> PC1.5
     *
     * For FR2 0 -> Default
     */
    @ProtoNumber(4) private val rawPowerClass: Int = 0,

    /**
     * intraBandENDC-Support is stored as an enum.
     *
     * 0 -> contiguous, 1 -> non-contiguous, 2 -> both.
     */
    @ProtoNumber(5) private val rawIntraBandEnDcSupport: Int = 0
) {
    val bcsNr
        get() = BCS.fromBinaryString(rawBcsNr.toString(2))

    val bcsIntraEndc
        get() = BCS.fromBinaryString(rawBcsIntraEndc.toString(2))

    val bcsEutra
        get() = BCS.fromBinaryString(rawBcsIntraEndc.toString(2))

    val powerClass
        get() =
            when (rawPowerClass) {
                1 -> PowerClass.PC2
                2 -> PowerClass.PC1dot5
                else -> PowerClass.NONE
            }
}
