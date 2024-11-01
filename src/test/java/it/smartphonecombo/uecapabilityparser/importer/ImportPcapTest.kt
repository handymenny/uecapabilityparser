package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.importer.multi.ImportPcap
import org.junit.jupiter.api.Test

internal class ImportPcapTest :
    AbstractImportMultiCapabilities(ImportPcap, "src/test/resources/pcap/") {
    // Only utra
    @Test
    fun testPduUtra() {
        parse("exportedPduUtra.pcap", "exportedPduUtra-capabilities.json")
    }

    // One eutra
    @Test
    fun testPduEutra() {
        parse("exportedPduEutra.pcap", "exportedPduEutra-capabilities.json")
    }

    // different filters, one duplicate
    @Test
    fun testPduEutraDifferentFilters() {
        parse(
            "exportedPduEutraDifferentFilters.pcap",
            "exportedPduEutraDifferentFilters-capabilities.json",
        )
    }

    // nr rrc + lte rrc
    @Test
    fun testPDUNrRrcLteRrc() {
        parse("exportedPduNrRrcLteRrc.pcap", "exportedPduNrRrcLteRrc-capabilities.json")
    }

    // different MRDC capabilities all split
    @Test
    fun testPduMrdcFullSplit() {
        parse("exportedPduMrdcFullSplit.pcap", "exportedPduMrdcFullSplit-capabilities.json")
    }

    // different capabilities mixed split
    @Test
    fun testPduMrdcMixedSplit() {
        parse("exportedPduMrdcMixedSplit.pcap", "exportedPduMrdcMixedSplit-capabilities.json")
    }

    // SIM 1: 127.0.0.1, SIM 2: 127.0.0.2
    @Test
    fun testGsmTapDualSim() {
        parse("gsmTapDualSim.pcap", "gsmTapDualSim-capabilities.json")
    }

    // SIM 1: 127.0.0.1, SIM 2: 127.0.0.1
    @Test
    fun testGsmTapDualSimSameIp() {
        parse("gsmTapDualSimSameIP.pcap", "gsmTapDualSimSameIP-capabilities.json")
    }

    // many capabilities
    @Test
    fun testGsmTapComplex() {
        parse("gsmTapComplex.pcap", "gsmTapComplex-capabilities.json")
    }

    // eutra + nr/eutra-nr, no 0xB826
    @Test
    fun testGsmTapMrdcSemiSplit() {
        parse("gsmTapMrdcSemiSplit.pcap", "gsmTapMrdcSemiSplit-capabilities.json")
    }

    // shannon
    @Test
    fun testGsmTapShannon() {
        parse("gsmTapShannon.pcap", "gsmTapShannon-capabilities.json")
    }

    // 0xB826 + spurious data
    @Test
    fun testGsmTapSpuriousData() {
        parse("gsmTapSpuriousData.pcap", "gsmTapSpuriousData-capabilities.json")
    }

    // NGAP radio cap + S1AP radio cap + spurious data
    @Test
    fun testSCTP() {
        parse("sctpS1apNgap.pcap", "sctpS1apNgap-capabilities.json")
    }

    // GSMTAPv3 Draft/proposal
    @Test
    fun testGsmTapV3() {
        parse("gsmTapV3.pcap", "gsmTapV3.json")
    }

    // GSMTAPv3 Draft/proposal  NR ue capability segmented
    @Test
    fun testGsmTapV3NrRrcSegmented() {
        parse("gsmTapV3NrRrcSegmented.pcap", "gsmTapV3NrRrcSegmented.json")
    }

    // GSMTAPv2 MRDC ue capability segmented
    @Test
    fun testGsmTapMrdcSegmented() {
        parse("gsmTapMrdcSegmented.pcap", "gsmTapMrdcSegmented.json")
    }

    // Exported PDU MRDC ue capability segmented
    @Test
    fun testPduSegmented() {
        parse("exportedPduMrdcSegmented.pcap", "exportedPduMrdcSegmented.json")
    }
}
