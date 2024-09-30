@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.lte

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * This is the result of reverse-engineering Shannon Ue Cap Configs stored as binary protobufs in
 * Google Pixel firmware.
 *
 * See also ShannonLteUeCap.proto in src/main/resources/definition.
 *
 * This work wouldn't have been possible without the help of @NXij.
 */
@Serializable
@SerialName("ShannonLteUECap")
data class ShannonLteUECap(
    /** ShannonUECapLte version. */
    @ProtoNumber(1) val version: Long,
    /** List of combos. */
    @ProtoNumber(2) val combos: List<ShannonComboLte> = emptyList(),
    @ProtoNumber(3) val bitmask: Long,
)
