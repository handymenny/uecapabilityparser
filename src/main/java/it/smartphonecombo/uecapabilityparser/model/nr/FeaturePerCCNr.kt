package it.smartphonecombo.uecapabilityparser.model.nr

import it.smartphonecombo.uecapabilityparser.model.lte.FeaturePerCCLte

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

    override fun toString(): String {
        val typeString =
            when (type) {
                DOWNlINK -> "DL"
                UPLINK -> "UL"
                else -> "Unknown"
            }
        return "FeaturePerCCNr(type=$typeString, mimo=$mimo, qam=$qam, bw=$bw, scs=$scs, bw90MHz=$channelBW90mhz)"
    }
}
