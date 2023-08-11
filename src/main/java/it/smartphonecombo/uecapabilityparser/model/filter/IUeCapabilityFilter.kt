package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.model.Rat
import kotlinx.serialization.Serializable

@Serializable
sealed interface IUeCapabilityFilter {
    var rat: Rat
    var lteBands: List<BandFilterLte>
    var includeNrDc: Boolean
    var includeNeDc: Boolean
    var omitEnDc: Boolean
    var uplinkTxSwitchRequest: Boolean
}
