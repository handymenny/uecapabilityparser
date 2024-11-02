package it.smartphonecombo.uecapabilityparser.model.ratcapabilities

import it.smartphonecombo.uecapabilityparser.model.Rat
import kotlinx.serialization.Serializable

@Serializable
sealed interface IRatCapabilities {
    val rat: Rat
    val release: Int?
    val ueCapSegmentationSupported: Boolean?
}
