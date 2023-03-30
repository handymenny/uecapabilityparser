package it.smartphonecombo.uecapabilityparser.model.feature

import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Modulation

open class FeaturePerCCLte(
    var type: LinkDirection = LinkDirection.DOWNLINK,
    var mimo: Int = 0,
    var qam: Modulation = Modulation.NONE
) {
    override fun toString(): String {
        return "FeaturePerCCLte(type=$type, mimo=$mimo, qam=$qam)"
    }
}
