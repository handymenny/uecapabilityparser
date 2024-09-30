@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.nr

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("FeatureNr")
data class ShannonFeatureNr(
    /** Intraband Freq Separation is stored as unsigned int */
    @ProtoNumber(1) private val intrabandFreqSeparation: Long? = null,
)
