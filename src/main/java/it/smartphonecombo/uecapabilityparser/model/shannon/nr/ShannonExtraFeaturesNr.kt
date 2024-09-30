@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.nr

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("ExtraFeatures")
data class ShannonExtraFeaturesNr(
    @ProtoNumber(1) val caParamEutraBitmap: Long? = null,
    @ProtoNumber(2) val caParamEutraSupportedNaics2CrSAp: Long? = null,
    @ProtoNumber(3) val caParamNrBitmap: Long? = null,
    @ProtoNumber(4) val caParamNrSimultaneousSrsAssocCsiRsAllCc: Long? = null,
    @ProtoNumber(5) val caParamNrMaxNumberSimultaneousNzpCsiRsActBwpAllCc: Long? = null,
    @ProtoNumber(6) val caParamNrTotalNumberPortsSimultaneousNzpCsiRsActBwpAllCc: Long? = null,
    @ProtoNumber(7) val caParamNrSimultaneousCsiReportsAllCc: Long? = null,
    @ProtoNumber(8) val caParamMrdcBitmap: Long? = null,
    @ProtoNumber(9) val caParamMrdcUlSharingEutraNr: Long? = null,
    @ProtoNumber(10) val caParamMrdcUlSwitchingTimeEutraNr: Long? = null
)
