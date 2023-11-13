@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import kotlin.math.max
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

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
