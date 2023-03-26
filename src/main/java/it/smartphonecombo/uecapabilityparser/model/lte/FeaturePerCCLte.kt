package it.smartphonecombo.uecapabilityparser.model.lte

open class FeaturePerCCLte(var type: Int = DOWNlINK, var mimo: Int = 0, var qam: String? = null) {
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
