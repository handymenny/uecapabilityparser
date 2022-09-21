package it.smartphonecombo.uecapabilityparser.bean


/**
 * Enumeration of Rat types, the id represents the corresponding value in the LTE UE Capability enquiry
 */
enum class Rat
    (val id: Int) {
    eutra(0), utra(1), geran_cs(2), geran_ps(3), cdma2000_1XRTT(4), nr(5), eutra_nr(6), spare1(7);
}