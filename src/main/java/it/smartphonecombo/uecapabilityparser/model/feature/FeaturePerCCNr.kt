package it.smartphonecombo.uecapabilityparser.model.feature

import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Modulation

class FeaturePerCCNr(
    type: LinkDirection = LinkDirection.DOWNLINK,
    mimo: Int = 2,
    qam: Modulation = Modulation.NONE,
    var bw: Int = 0,
    var scs: Int = 0,
    var channelBW90mhz: Boolean = false,
) : FeaturePerCCLte(type, mimo, qam) {
    override fun toString(): String {
        return "FeaturePerCCNr(type=$type, mimo=$mimo, qam=$qam, bw=$bw, scs=$scs, bw90MHz=$channelBW90mhz)"
    }
}
