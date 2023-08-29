package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.band.IBandBoxed
import kotlinx.serialization.Serializable

@Serializable
sealed interface IUeCapabilityFilter {
    var rat: Rat
    // BandBoxed for LTE Capabilities BandFilterLte for NR Capabilities
    val lteBands: List<IBandBoxed>
    var includeNrDc: Boolean
    var includeNeDc: Boolean
    var omitEnDc: Boolean
    var uplinkTxSwitchRequest: Boolean
}
