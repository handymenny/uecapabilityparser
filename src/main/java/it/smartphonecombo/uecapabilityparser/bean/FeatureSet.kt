package it.smartphonecombo.uecapabilityparser.bean

import it.smartphonecombo.uecapabilityparser.bean.lte.FeaturePerCCLte

data class FeatureSet(
    val featureSetsPerCC: List<FeaturePerCCLte>?,
    val type: Int
) {
    companion object {
        const val UPLINK = 1
        const val DOWNlINK = 0
    }
}