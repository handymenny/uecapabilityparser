@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
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
    @ProtoNumber(1) @SerialName("band") private val rawBand: Long,

    /** BwClass DL is stored as ASCII value - 0x40. 0 means DL not supported. */
    @ProtoNumber(2) @SerialName("bwClassDl") private val rawBwClassDl: Long,

    /** BwClass UL is stored as ASCII value - 0x40. 0 means UL not supported. */
    @ProtoNumber(3) @SerialName("bwClassUl") private val rawBwClassUL: Long,

    /**
     * For LTE this is FeatureSetEUTRA-DownlinkId, the corresponding FeatureSetDL-r15 seems to be
     * hardcoded elsewhere (see [ShannonFeatureSetEutra]).
     *
     * For NR this is FeatureSetDownlinkId, the corresponding FeatureSetDownlink is stored in
     * [ShannonNrUECap.dlFeatureList].
     *
     * Note that the index starts from 1 as per 3GPP spec, 0 means DL not supported.
     */
    @ProtoNumber(4) val dlFeatureIndex: Long,

    /**
     * For LTE this is FeatureSetEUTRA-UplinkId, the corresponding FeatureSetUL-r15 seems to be
     * hardcoded elsewhere (see [ShannonFeatureSetEutra]).
     *
     * For NR this is FeatureSetUplinkId, the corresponding FeatureSetUplink is stored in
     * [ShannonNrUECap.ulFeatureList].
     *
     * Note that the index starts from 1 as per 3GPP spec, 0 means UL not supported.
     */
    @ProtoNumber(5) val ulFeatureIndex: Long,

    /**
     * This is a list of FeatureSetDownlinkPerCC-Id per each CC. This only applies to NR.
     *
     * The corresponding FeatureSetDownlinkPerCC is stored in [ShannonNrUECap.dlFeaturePerCCList].
     */
    @ProtoNumber(6) @ProtoPacked val dlFeaturePerCCIds: List<Long> = emptyList(),

    /**
     * This is a list of FeatureSetUplinkPerCC-Id per each CC. This only applies to NR.
     *
     * The corresponding FeatureSetUplinkPerCC are stored in [ShannonNrUECap.ulFeaturePerCCList].
     */
    @ProtoNumber(7) @ProtoPacked val ulFeaturePerCCIds: List<Long> = emptyList(),

    /**
     * SupportedSRS-TxPortSwitch is stored as enum.
     *
     * 1 -> t1r1, 2 -> t1r2, 3 -> t1r4, 4 -> t2r2, 5 -> t2r4, 6 -> t1r4-t2r4
     */
    @ProtoNumber(8) val srsTxSwitch: Int? = null
) {
    @Transient val isNr = rawBand > 10000
    val band: Band
        get() = (if (isNr) rawBand - 10000 else rawBand).toInt()

    val bwClassDl: BwClass
        get() = BwClass.valueOf(rawBwClassDl.toInt())

    val bwClassUl: BwClass
        get() = BwClass.valueOf(rawBwClassUL.toInt())

    fun toComponent(): IComponent {
        return if (isNr) {
            ComponentNr(band, bwClassDl, bwClassUl)
        } else {
            ComponentLte(band, bwClassDl, bwClassUl)
        }
    }
}
