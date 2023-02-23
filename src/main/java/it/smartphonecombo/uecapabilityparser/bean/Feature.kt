package it.smartphonecombo.uecapabilityparser.bean

data class Feature(val isNR: Boolean, val downlink: Int, val uplink: Int) {
    override fun toString(): String {
        return "DL: " + downlink + " UL: " + uplink + if (isNR) " NR" else " EUTRA"
    }
}
