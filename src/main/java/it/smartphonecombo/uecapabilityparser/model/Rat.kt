package it.smartphonecombo.uecapabilityparser.model

/**
 * Enumeration of Rat types, the id represents the corresponding value in the LTE UE Capability
 * enquiry
 */
enum class Rat {
    EUTRA,
    UTRA,
    GERAN_CS,
    GERAN_PS,
    CDMA2000_1XRTT,
    NR,
    EUTRA_NR,
    SPARE1;

    val id
        get() = ordinal

    val ratCapabilityIdentifier
        get() =
            when (this) {
                EUTRA -> "UE-EUTRA-Capability"
                UTRA -> "InterRATHandoverInfo"
                GERAN_PS -> "MS Radio Access Capability"
                GERAN_CS -> "Mobile Station Classmark 2"
                CDMA2000_1XRTT -> "A21 Mobile Subscription Information"
                NR -> "UE-NR-Capability"
                EUTRA_NR -> "UE-MRDC-Capability"
                SPARE1 -> "Spare 1"
            }

    override fun toString(): String {
        return when (this) {
            EUTRA -> "eutra"
            UTRA -> "utra"
            GERAN_CS -> "geran-cs"
            GERAN_PS -> "geran-ps"
            CDMA2000_1XRTT -> "cdma2000-1XRTT"
            NR -> "nr"
            EUTRA_NR -> "eutra-nr"
            SPARE1 -> "spare1"
        }
    }

    companion object {
        fun of(string: String?): Rat? {
            return when (string) {
                "eutra" -> EUTRA
                "utra" -> UTRA
                "geran-cs" -> GERAN_CS
                "geran-ps" -> GERAN_PS
                "cdma2000-1XRTT" -> CDMA2000_1XRTT
                "nr" -> NR
                "eutra-nr" -> EUTRA_NR
                "spare1" -> SPARE1
                else -> null
            }
        }
    }
}
