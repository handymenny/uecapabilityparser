package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.band.IBandBoxed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Stores LTE BandFilter for NR/MRDC Capabilities. For LTE Capabilities use
 * [BandBoxed][it.smartphonecombo.uecapabilityparser.model.band.BandBoxed]
 */
@Serializable
data class BandFilterLte(
    @SerialName("band") override var band: Band,
    @SerialName("bwClassDl") var classDL: BwClass = BwClass.NONE,
    @SerialName("bwClassUl") var classUL: BwClass = BwClass.NONE,
) : IBandBoxed
