package it.smartphonecombo.uecapabilityparser.bean.lte

open class FeaturePerCCLte(
    var type: Int = DOWNlINK,
    var mimo: Int = 0,
    var qam: String? = null
) {
    companion object {
        const val UPLINK = 1
        const val DOWNlINK = 0
    }
}