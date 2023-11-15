@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
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
@SerialName("ShannonNrUECap")
data class ShannonNrUECap(
    /** ShannonNrUECap version. */
    @ProtoNumber(1) val version: UInt,
    /** ID assigned to this ShannonNrUECap. */
    @ProtoNumber(2) val id: Int? = null,
    /** List of combo groups. */
    @ProtoNumber(3) val comboGroups: List<ComboGroup> = emptyList(),
    /** List of FeatureSetDownlinkPerCC. */
    @ProtoNumber(6) val dlFeaturePerCCList: List<ShannonFeatureDlPerCCNr> = emptyList(),
    /** List of FeatureSetUplinkPerCC. */
    @ProtoNumber(7) val ulFeaturePerCCList: List<ShannonFeatureUlPerCCNr> = emptyList()
)
