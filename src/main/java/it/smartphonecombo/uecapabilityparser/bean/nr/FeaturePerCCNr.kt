package it.smartphonecombo.uecapabilityparser.bean.nr

import it.smartphonecombo.uecapabilityparser.bean.lte.FeaturePerCCLte

class FeaturePerCCNr(
    type: Int = DOWNlINK,
    mimo: Int = 2,
    qam: String? = null,
    var bw: Int = 0,
    var scs: Int = 0,
    var channelBW90mhz: Boolean = false,
) : FeaturePerCCLte(type, mimo, qam) {
    companion object {
        const val UPLINK = 1
        const val DOWNlINK = 0
    }
}