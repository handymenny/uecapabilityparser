package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BandFilterLte(
    @SerialName("band") override var band: Band,
    @SerialName("bwClassDl") var classDL: BwClass = BwClass.NONE,
    @SerialName("bwClassUl") var classUL: BwClass = BwClass.NONE,
) : IBandFilter
