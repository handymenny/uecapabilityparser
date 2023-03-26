package it.smartphonecombo.uecapabilityparser.model.lte

import it.smartphonecombo.uecapabilityparser.model.Modulation

open class FeaturePerCCLte(
    var type: Int = DOWNlINK,
    var mimo: Int = 0,
    var qam: Modulation = Modulation.NONE
) {
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
        return "FeaturePerCCLte(type=$typeString, mimo=$mimo, qam=$qam)"
    }
}
