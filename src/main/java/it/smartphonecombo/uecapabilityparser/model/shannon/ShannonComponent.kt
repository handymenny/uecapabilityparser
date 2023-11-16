@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoPacked

@Serializable
@SerialName("Component")
data class ShannonComponent(
    /**
     * LTE Bands are stored as they are.
     *
     * NR Bands are stored as band number + 10000.
     */
    @ProtoNumber(1) @SerialName("band") private val rawBand: Int,

    /** BwClass DL is stored as ASCII value - 0x40. 0 means DL not supported. */
    @ProtoNumber(2) @SerialName("bwClassDl") private val rawBwClassDl: Int,

    /** BwClass UL is stored as ASCII value - 0x40. 0 means UL not supported. */
    @ProtoNumber(3) @SerialName("bwClassUl") private val rawBwClassUL: Int,

    /**
     * For LTE this is FeatureSetEUTRA-DownlinkId, the corresponding FeatureSetDL-r15 seems to be
     * hardcoded elsewhere (see [ShannonFeatureSetEutra]). Note that the index starts from 1 as per
     * 3GPP spec, 0 means DL not supported.
     *
     * For NR this sets some features that applies to the whole component (not PerCC). Empirically 1
     * -> FR1, 2 -> FR2.
     */
    @ProtoNumber(4) val dlFeatureIndex: Int,

    /**
     * For LTE this is FeatureSetEUTRA-UplinkId, the corresponding FeatureSetUL-r15 seems to be
     * hardcoded elsewhere (see [ShannonFeatureSetEutra]). Note that the index starts from 1 as per
     * 3GPP spec, 0 means UL not supported.
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
