package it.smartphonecombo.uecapabilityparser.model.ratcapabilities

import it.smartphonecombo.uecapabilityparser.model.Rat
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RatCapabilitiesNr")
data class RatCapabilitiesNr(
    @SerialName("release") override val release: Int? = null,
    @SerialName("ueCapSegmentationSupported")
    override val ueCapSegmentationSupported: Boolean? = null,
    @SerialName("ueType") val ueType: UeType = UeType.EMBB,
) : IRatCapabilities {
    @Required @SerialName("rat") override val rat: Rat = Rat.NR
}

@Serializable
enum class UeType {
    @SerialName("eMBB") EMBB,
    @SerialName("RedCap_R17") RED_CAP_R17,
}
