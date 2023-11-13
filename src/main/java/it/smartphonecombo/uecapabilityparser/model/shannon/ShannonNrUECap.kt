@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.SingleMimo
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCLte
import it.smartphonecombo.uecapabilityparser.model.feature.FeatureSet
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import kotlin.math.max
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoPacked

/**
 * This is the result of reverse-engineering Shannon Ue Cap Configs stored as binary protobufs in
 * Google Pixel firmware.
 *
 * See also ShannonNrUeCap.proto in src/main/resources/definition.
 *
 * This work wouldn't have been possible without the help of @NXij.
 */
@Serializable
data class ShannonNrUECap(
    /** ShannonNrUECap version. */
    @ProtoNumber(1) val version: UInt,
    /** ID assigned to this ShannonNrUECap. */
    @ProtoNumber(2) val id: Int = 0,
    /** List of combo groups. */
    @ProtoNumber(3) val comboGroups: List<ComboGroup> = emptyList(),
    /** List of FeatureSetDownlinkPerCC. */
    @ProtoNumber(6) val dlFeaturePerCCList: List<ShannonFeatureSetDlPerCCNr> = emptyList(),
    /** List of FeatureSetUplinkPerCC. */
    @ProtoNumber(7) val ulFeaturePerCCList: List<ShannonFeatureSetUlPerCCNr> = emptyList()
)

@Serializable
data class ComboGroup(
    /** Some features that applies to the whole combo group. */
    @ProtoNumber(1) val comboFeatures: ComboFeatures,
    /** List of combos that share the same [ComboFeatures]. */
    @ProtoNumber(2) val combos: List<ShannonCombo> = emptyList(),
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

@Serializable
data class ShannonCombo(
    /** List of Components. */
    @ProtoNumber(1) val components: List<ShannonComponent> = emptyList(),
    /** A bit mask stored as unsigned int that enables or disables some features. */
    @ProtoNumber(2) val bitMask: UInt
)

@Serializable
data class ShannonComponent(
    /**
     * LTE Bands are stored as they are.
     *
     * NR Bands are stored as band number + 10000.
     */
    @ProtoNumber(1) private val rawBand: Int,

    /** BwClass DL is stored as ASCII value - 0x40. 0 means DL not supported. */
    @ProtoNumber(2) private val rawBwClassDl: Int,

    /** BwClass UL is stored as ASCII value - 0x40. 0 means UL not supported. */
    @ProtoNumber(3) private val rawBwClassUL: Int,

    /**
     * For LTE this is FeatureSetEUTRA-DownlinkId, the corresponding FeatureSetDL-r15 seems to be
     * hardcoded elsewhere (see [ShannonHardCodedFeatureSetEutra]). Note that the index starts from
     * 1 as per 3GPP spec, 0 means DL not supported.
     *
     * For NR this sets some features that applies to the whole component (not PerCC). Empirically 1
     * -> FR1, 2 -> FR2.
     */
    @ProtoNumber(4) val dlFeatureIndex: Int,

    /**
     * For LTE this is FeatureSetEUTRA-UplinkId, the corresponding FeatureSetUL-r15 seems to be
     * hardcoded elsewhere (see [ShannonHardCodedFeatureSetEutra]). Note that the index starts from
     * 1 as per 3GPP spec, 0 means UL not supported.
     *
     * For NR this sets some features that applies to the whole component (not PerCC). Empirically 1
     * -> SRS ports per resource 1, 2 -> SRS ports per resource 2.
     */
    @ProtoNumber(5) val ulFeatureIndex: Int,

    /**
     * This is a list of FeatureSetDownlinkPerCC-Id per each CC. This only applies to NR.
     *
     * The corresponding FeatureSetDownlinkPerCC are stored in [ShannonNrUECap.dlFeaturePerCCList].
     *
     * Note that the index starts from 1 as per 3GPP spec, 0 means DL not supported.
     */
    @ProtoNumber(6) @ProtoPacked val dlFeaturePerCCIds: List<Int> = emptyList(),

    /**
     * This is a list of FeatureSetUplinkPerCC-Id per each CC. This only applies to NR.
     *
     * The corresponding FeatureSetUplinkPerCC are stored in [ShannonNrUECap.ulFeaturePerCCList].
     *
     * Note that the index starts from 1 as per 3GPP spec, 0 means UL not supported.
     */
    @ProtoNumber(7) @ProtoPacked val ulFeaturePerCCIds: List<Int> = emptyList()
) {
    @Transient val isNr = rawBand > 10000
    val band: Band
        get() = if (isNr) rawBand - 10000 else rawBand

    val bwClassDl: BwClass
        get() = BwClass.valueOf(rawBwClassDl)

    val bwClassUl: BwClass
        get() = BwClass.valueOf(rawBwClassUL)
}

@Serializable
sealed class ShannonFeatureSetPerCCNr {
    /** Max SCS is stored as numerology + 1, i.e. 1 = 15kHz, 2 = 30kHz, 3 = 60kHz, 4 = 120kHz. */
    protected abstract val rawMaxScs: Int

    /**
     * Max Mimo is stored as an enum.
     *
     * Max Mimo DL: 0 -> not supported, 1 -> 2, 2 -> 4.
     *
     * Max Mimo CB UL: 0 -> not supported, 1 -> 1, 2 -> 2.
     */
    protected abstract val rawMaxMimo: Int

    /** Max Bandwidth is stored as it's. */
    abstract val maxBw: Int

    /**
     * Max Modulation Order is stored as an enum.
     *
     * 0 -> not supported, 1 -> QAM64, 2 -> QAM256.
     *
     * Note that as TS 38 306 4.2.7.8 and 4.2.7.6, this doesn't specify the maximum (nor the
     * minimum) modulation supported.
     */
    protected abstract val rawMaxModOrder: Int

    /** BW 90MHz supported is stored as boolean. */
    abstract val bw90MHzSupported: Boolean

    val maxScs
        get() =
            when (rawMaxScs) {
                1 -> 15
                2 -> 30
                3 -> 60
                4 -> 120
                else -> rawMaxScs
            }

    /**
     * As TS 38 306 4.2.7.8 and 4.2.7.6, this only sets the modulation order to be used in the
     * calculation of the max data rate. It doesn't specify the maximum (nor the minimum) modulation
     * supported.
     */
    val maxModOrder
        get() =
            when (rawMaxModOrder) {
                1 -> ModulationOrder.QAM64
                2 -> ModulationOrder.QAM256
                else -> ModulationOrder.NONE
            }

    abstract val maxMimo: Int
}

@Serializable
data class ShannonFeatureSetDlPerCCNr(
    @ProtoNumber(1) override val rawMaxScs: Int,
    @ProtoNumber(2) override val rawMaxMimo: Int,
    @ProtoNumber(3) override val maxBw: Int,
    @ProtoNumber(4) override val rawMaxModOrder: Int,
    @ProtoNumber(5) override val bw90MHzSupported: Boolean
) : ShannonFeatureSetPerCCNr() {
    override val maxMimo
        get() =
            when (rawMaxMimo) {
                1 -> 2
                2 -> 4
                else -> 0
            }
}

@Serializable
data class ShannonFeatureSetUlPerCCNr(
    @ProtoNumber(1) override val rawMaxScs: Int,
    @ProtoNumber(2) override val rawMaxMimo: Int,
    @ProtoNumber(3) override val maxBw: Int,
    @ProtoNumber(4) override val rawMaxModOrder: Int,
    @ProtoNumber(5) override val bw90MHzSupported: Boolean,
    /** Same as [rawMaxMimo] but for non CB Uplink (with non-codebook precoding) */
    @ProtoNumber(6) private val rawMaxMimoNonCb: Int
) : ShannonFeatureSetPerCCNr() {
    override val maxMimo
        get() =
            when (max(rawMaxMimo, rawMaxMimoNonCb)) {
                1 -> 1
                2 -> 2
                else -> 0
            }
}

/**
 * On Shannon modems the Eutra FeatureSet (featureSetsEUTRA-r15, featureSetsDL-PerCC-r15) seems to
 * be hardcoded.
 *
 * Analyzing the ue capabilities of different shannon devices, going from Galaxy SM-G977B to Pixel
 * GP4BC, the Eutra FeatureSet is always the same or at the least a reduced version that shares
 * the same ids. That is, the proposed version has 10 ids, a reduced version has 6, but proposed id
 * 1 = reduced id 1 and so on.
 *
 * That hardcoded Eutra FeatureSet is proposed below. Modulation is removed as it's controlled by
 * other bits.
 */
object ShannonHardCodedFeatureSetEutra {
    private val downlinkPerCC =
        listOf(FeaturePerCCLte(mimo = SingleMimo(4)), FeaturePerCCLte(mimo = SingleMimo(2)))
    private val uplinkPerCC =
        listOf(
            FeaturePerCCLte(LinkDirection.UPLINK, mimo = SingleMimo(2)),
            FeaturePerCCLte(LinkDirection.UPLINK, mimo = SingleMimo(1)),
        )

    val downlink =
        listOf(
            FeatureSet(listOf(downlinkPerCC[0]), LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[1]), LinkDirection.DOWNLINK),
            FeatureSet(List(2) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[1], downlinkPerCC[0]), LinkDirection.DOWNLINK),
            FeatureSet(listOf(downlinkPerCC[0], downlinkPerCC[1]), LinkDirection.DOWNLINK),
            FeatureSet(List(2) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
            FeatureSet(List(3) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(List(3) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
            FeatureSet(List(4) { downlinkPerCC[0] }, LinkDirection.DOWNLINK),
            FeatureSet(List(4) { downlinkPerCC[1] }, LinkDirection.DOWNLINK),
        )

    val uplink =
        listOf(
            FeatureSet(listOf(uplinkPerCC[0]), LinkDirection.UPLINK),
            FeatureSet(listOf(uplinkPerCC[1]), LinkDirection.UPLINK),
            FeatureSet(listOf(uplinkPerCC[0], uplinkPerCC[1]), LinkDirection.UPLINK),
            FeatureSet(List(2) { downlinkPerCC[1] }, LinkDirection.UPLINK),
        )
}
