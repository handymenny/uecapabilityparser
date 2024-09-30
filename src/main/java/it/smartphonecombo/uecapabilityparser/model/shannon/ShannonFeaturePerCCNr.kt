@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.toMimo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
sealed class ShannonFeaturePerCCNr {
    /** Max SCS is stored as numerology + 1, i.e. 1 = 15kHz, 2 = 30kHz, 3 = 60kHz, 4 = 120kHz. */
    protected abstract val rawMaxScs: Int

    /**
     * Max Mimo is stored as an enum.
     *
     * Max Mimo DL: 0 -> not supported, 1 -> 2, 2 -> 4, 3 -> 8.
     *
     * Max Mimo UL: 0 -> not supported, 1 -> 1, 2 -> 2, 3 -> 4.
     */
    protected abstract val rawMaxMimo: Int

    /** Max Bandwidth is stored as it's. */
    abstract val maxBw: Long

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

    fun toFeaturePerCCNr(): FeaturePerCCNr {
        val direction =
            if (this is ShannonFeatureDlPerCCNr) {
                LinkDirection.DOWNLINK
            } else {
                LinkDirection.UPLINK
            }

        // maxBw doesn't have a 90MHz field
        val bw = if (bw90MHzSupported && maxBw == 80L) 90L else maxBw

        return FeaturePerCCNr(
            direction,
            maxMimo.toMimo(),
            maxModOrder,
            bw.toInt(),
            maxScs,
            bw90MHzSupported,
        )
    }
}

@Serializable
@SerialName("FeatureDlPerCCNr")
data class ShannonFeatureDlPerCCNr(
    @ProtoNumber(1) @SerialName("maxScs") override val rawMaxScs: Int,
    @ProtoNumber(2) @SerialName("maxMimo") override val rawMaxMimo: Int,
    @ProtoNumber(3) override val maxBw: Long,
    @ProtoNumber(4) @SerialName("maxModOrder") override val rawMaxModOrder: Int,
    @ProtoNumber(5) override val bw90MHzSupported: Boolean
) : ShannonFeaturePerCCNr() {
    override val maxMimo
        get() =
            when (rawMaxMimo) {
                1 -> 2
                2 -> 4
                3 -> 8
                else -> 0
            }
}

@Serializable
@SerialName("FeatureUlPerCCNr")
data class ShannonFeatureUlPerCCNr(
    @ProtoNumber(1) @SerialName("maxScs") override val rawMaxScs: Int,
    @ProtoNumber(2) @SerialName("maxMimo") override val rawMaxMimo: Int,
    @ProtoNumber(3) override val maxBw: Long,
    @ProtoNumber(4) @SerialName("maxModOrder") override val rawMaxModOrder: Int,
    @ProtoNumber(5) override val bw90MHzSupported: Boolean,
    /** MaxNumberSRS-ResourcePerSet is stored as unsigned int */
    @ProtoNumber(6) @SerialName("maxNumSRSResPerSet") val maxNumSRSResPerSet: Long
) : ShannonFeaturePerCCNr() {
    override val maxMimo
        get() =
            when (rawMaxMimo) {
                1 -> 1
                2 -> 2
                3 -> 4
                else -> 0
            }
}
