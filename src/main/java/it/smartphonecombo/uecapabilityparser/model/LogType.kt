package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CD
import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CDBin
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilityInformation
import it.smartphonecombo.uecapabilityparser.importer.ImportLteCarrierPolicy
import it.smartphonecombo.uecapabilityparser.importer.ImportMTKLte
import it.smartphonecombo.uecapabilityparser.importer.ImportNrCapPrune
import it.smartphonecombo.uecapabilityparser.importer.ImportNvItem
import it.smartphonecombo.uecapabilityparser.importer.ImportQctModemCap
import it.smartphonecombo.uecapabilityparser.importer.ImportShannonNrUeCap
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat.isScatAvailable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class LogType(val description: String) {
    @SerialName("") INVALID(""),
    H("UE Capability Hex Dump"),
    W("Wireshark UE Capability Information"),
    N("NSG UE Capability Information"),
    C("Carrier policy"),
    CNR("NR Cap Prune"),
    E("28874 nvitem binary"),
    Q("QCAT 0xB0CD"),
    QLTE("0xB0CD hexdump"),
    QNR("0xB826 hexdump"),
    M("MEDIATEK CA_COMB_INFO"),
    O("OSIX UE Capability Information"),
    QC("QCAT UE Capability Information"),
    T("TEMS UE Capability Information"),
    A("Amarisoft UE Capability Information"),
    RF("QCT Modem Capabilities"),
    SHNR("Shannon NR UE Cap Config Protobuf"),
    P("PCAP"),
    DLF("DLF baseband log"),
    QMDL("QMDL baseband log"),
    HDF("HDF baseband log"),
    SDM("SDM baseband log");

    companion object {
        /** All entries except invalid ones */
        val validEntries =
            when (isScatAvailable()) {
                1 -> entries.drop(1)
                0 -> {
                    System.err.println("Warning: scat not available, scat log types disabled")
                    entries.drop(1).dropLast(4)
                }
                else -> {
                    System.err.println("Warning: scat is too old, scat log types disabled")
                    entries.drop(1).dropLast(4)
                }
            }
        /** Name of all entries except [INVALID] */
        val names = validEntries.map { it.name }.toTypedArray()
        /** Entries that only supports LTE-CA */
        val lteOnlyTypes = listOf(C, E, Q, QLTE, M, RF)
        /** One input -> multi capabilities * */
        val multiImporter = validEntries.intersect(listOf(P, DLF, QMDL, HDF, SDM))
        /** One input -> multi or single capability * */
        val singleInput = validEntries.intersect(listOf(E, SHNR, P, DLF, QMDL, HDF, SDM))

        /** Return [INVALID] if conversion fails * */
        fun of(string: String?): LogType {
            return try {
                valueOf(string!!)
            } catch (_: Exception) {
                INVALID
            }
        }

        fun getImporter(type: LogType) =
            when (type) {
                E -> ImportNvItem
                C -> ImportLteCarrierPolicy
                CNR -> ImportNrCapPrune
                Q -> Import0xB0CD
                QLTE -> Import0xB0CDBin
                M -> ImportMTKLte
                QNR -> Import0xB826
                RF -> ImportQctModemCap
                SHNR -> ImportShannonNrUeCap
                W,
                N,
                O,
                QC,
                H,
                T,
                A -> ImportCapabilityInformation
                else -> null
            }
    }
}
