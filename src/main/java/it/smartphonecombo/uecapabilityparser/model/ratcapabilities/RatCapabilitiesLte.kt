package it.smartphonecombo.uecapabilityparser.model.ratcapabilities

import it.smartphonecombo.uecapabilityparser.model.Rat
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RatCapabilitiesLte")
data class RatCapabilitiesLte(@SerialName("release") override val release: Int? = null) :
    IRatCapabilities {
    @Required @SerialName("rat") override val rat: Rat = Rat.EUTRA
}
