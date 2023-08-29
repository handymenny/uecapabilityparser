package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.band.IBandBoxed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BandFilterNr(
    @SerialName("band") override var band: Band,
    @SerialName("maxBwDl") var maxBwDl: Int = 0,
    @SerialName("maxBwUl") var maxBwUl: Int = 0,
    @SerialName("maxCCsDl") var maxCCsDl: Int = 0,
    @SerialName("maxCCsUl") var maxCCsUl: Int = 0,
) : IBandBoxed
