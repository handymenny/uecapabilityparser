package it.smartphonecombo.uecapabilityparser.bean

/**
 * Enumeration of Rat types, the id represents the corresponding value in the LTE UE Capability enquiry
 */
enum class Rat(val id: Int) {
    eutra(0), utra(1), geran_cs(2), geran_ps(3), cdma2000_1XRTT(4), nr(5), eutra_nr(6), spare1(7);

    val ratCapabilityIdentifier
        get() = when (this) {
            eutra -> "UE-EUTRA-Capability"
            utra -> "InterRATHandoverInfo"
            geran_ps -> "MS Radio Access Capability"
            geran_cs -> "Mobile Station Classmark 2"
            cdma2000_1XRTT -> "A21 Mobile Subscription Information"
            nr -> "UE-NR-Capability"
            eutra_nr -> "UE-MRDC-Capability"
            spare1 -> "Spare 1"
        }

    override fun toString(): String {
        return when (this) {
            eutra -> "eutra"
            utra -> "utra"
            geran_cs -> "geran-cs"
            geran_ps -> "geran-ps"
            cdma2000_1XRTT -> "cdma2000-1XRTT"
            nr -> "nr"
            eutra_nr -> "eutra-nr"
            spare1 -> "spare1"
        }
    }

    companion object {
        fun of(string: String?): Rat? {
            return when (string) {
                "eutra" -> eutra
                "utra" -> utra
                "geran-cs" -> geran_cs
                "geran-ps" -> geran_ps
                "cdma2000-1XRTT" -> cdma2000_1XRTT
                "nr" -> nr
                "eutra-nr" -> eutra_nr
                "spare1" -> spare1
                else -> {
                    null
                }
            }
        }
    }
}
