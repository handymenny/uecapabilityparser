package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CliJsonOutputTest {
    private val path = "src/test/resources/cli"

    @Test
    fun testCarrierPolicy() {
        test(
            "-i",
            "$path/input/carrierPolicy.xml",
            "-t",
            "C",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "carrierPolicy.json",
        )
    }

    @Test
    fun test0xb0CD() {
        test(
            "-i",
            "$path/input/0xB0CD.txt",
            "-t",
            "Q",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB0CD.json",
        )
    }

    @Test
    fun test0xb0CDMultiHex() {
        test(
            "-i",
            "$path/input/0xB0CDMultiHex.txt",
            "-t",
            "QLTE",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB0CDMultiHex.json",
        )
    }

    @Test
    fun testMtkLte() {
        test(
            "-i",
            "$path/input/mtkLte.txt",
            "-t",
            "M",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "mtkLte.json",
        )
    }

    @Test
    fun testNvItem() {
        test(
            "-i",
            "$path/input/nvItem.bin",
            "-t",
            "E",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nvItem.json",
        )
    }

    @Test
    fun test0xB826() {
        test(
            "-i",
            "$path/input/0xB826.hex",
            "-t",
            "QNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB826.json",
        )
    }

    @Test
    fun test0xB826Multi() {
        test(
            "-i",
            "$path/input/0xB826Multi.txt",
            "-t",
            "QNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB826Multi.json",
        )
    }

    @Test
    fun test0xB826MultiV14() {
        test(
            "-i",
            "$path/input/0xB826MultiV14.txt",
            "-t",
            "QNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB826MultiV14.json",
        )
    }

    @Test
    fun test0xB826MultiScat() {
        test(
            "-i",
            "$path/input/0xB826MultiScat.txt",
            "-t",
            "QNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB826MultiScat.json",
        )
    }

    @Test
    fun test0xB826Multi0x9801() {
        test(
            "-i",
            "$path/input/0xB826Multi0x9801.txt",
            "-t",
            "QNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "0xB826Multi0x9801.json",
        )
    }

    @Test
    fun testNrCapPrune() {
        test(
            "-i",
            "$path/input/nrCapPrune.txt",
            "-t",
            "CNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nrCapPrune.json",
        )
    }

    @Test
    fun testQctModemCap() {
        test(
            "-i",
            "$path/input/qctModemCap.txt",
            "-t",
            "RF",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "qctModemCap.json",
        )
    }

    @Test
    fun testShannonNrUeCap() {
        test(
            "-i",
            "$path/input/shannonNrUeCap.binarypb",
            "-t",
            "SHNR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "shannonNrUeCap.json",
        )
    }

    @Test
    fun testShannonLteUeCap() {
        test(
            "-i",
            "$path/input/shannonLteUeCap.binarypb",
            "-t",
            "SHLTE",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "shannonLteUeCap.json",
        )
    }

    @Test
    fun testWiresharkEutra() {
        test(
            "-i",
            "$path/input/wiresharkEutra.txt",
            "-t",
            "W",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkEutra.json",
        )
    }

    @Test
    fun testWiresharkNr() {
        test(
            "-i",
            "$path/input/wiresharkNr.txt",
            "-t",
            "W",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkNr.json",
        )
    }

    @Test
    fun testWiresharkMrdc() {
        test(
            "-i",
            "$path/input/wiresharkMrdc.txt",
            "-t",
            "W",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkMrdc.json",
        )
    }

    @Test
    fun testWiresharkMrdcSplit() {
        test(
            "-i",
            "$path/input/wiresharkMrdcSplit_0.txt,$path/input/wiresharkMrdcSplit_1.txt",
            "-t",
            "W",
            "-j",
            "-",
            oracleFilename = "wiresharkMrdcSplit.json",
        )
    }

    @Test
    fun testNsgEutra() {
        test(
            "-i",
            "$path/input/nsgEutra.txt",
            "-t",
            "N",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgEutra.json",
        )
    }

    @Test
    fun testNsgNr() {
        test(
            "-i",
            "$path/input/nsgNr.txt",
            "-t",
            "N",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgNr.json",
        )
    }

    @Test
    fun testNsgMrdc() {
        test(
            "-i",
            "$path/input/nsgMrdc.txt",
            "-t",
            "N",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgMrdc.json",
        )
    }

    @Test
    fun testNsgMrdcSplit() {
        test(
            "-i",
            "$path/input/nsgMrdcSplit_0.txt,$path/input/nsgMrdcSplit_1.txt",
            "-t",
            "N",
            "-j",
            "-",
            oracleFilename = "nsgMrdcSplit.json",
        )
    }

    @Test
    fun testNsgSul() {
        test(
            "-i",
            "$path/input/nsgSul.txt",
            "-t",
            "N",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgSul.json",
        )
    }

    @Test
    fun testOsixMrdc() {
        test(
            "-i",
            "$path/input/osixMrdc.txt",
            "-t",
            "O",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "osixMrdc.json",
        )
    }

    @Test
    fun testUeCapHexEutra() {
        test(
            "-i",
            "$path/input/ueCapHexEutra.hex",
            "--sub-types",
            "LTE",
            "-t",
            "H",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexEutra.json",
        )
    }

    @Test
    fun testUeCapHexNr() {
        test(
            "-i",
            "$path/input/ueCapHexNr.hex",
            "-t",
            "H",
            "--sub-types",
            "NR",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexNr.json",
        )
    }

    @Test
    fun testUeCapHexMrdcSplit() {
        test(
            "-i",
            "$path/input/ueCapHexMrdcSplit_eutra.hex,$path/input/ueCapHexMrdcSplit_nr.hex,$path/input/ueCapHexMrdcSplit_eutra-nr.hex",
            "--sub-types",
            "LTE,NR,ENDC",
            "-t",
            "H",
            "-j",
            "-",
            oracleFilename = "ueCapHexMrdcSplit.json",
        )
    }

    @Test
    fun testUeCapHexSegmented() {
        test(
            "-i",
            "$path/input/ueCapHexSegmented.hex",
            "--sub-types",
            "LTE",
            "-t",
            "H",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexSegmented.json",
        )
    }

    @Test
    fun testQcatMrdc() {
        test(
            "-i",
            "$path/input/qcatMrdc.txt",
            "-t",
            "QC",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "qcatMrdc.json",
        )
    }

    @Test
    fun testQcatNrdc() {
        test(
            "-i",
            "$path/input/qcatNrdc.txt",
            "-t",
            "QC",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "qcatNrdc.json",
        )
    }

    @Test
    fun testTemsEutra() {
        test(
            "-i",
            "$path/input/temsEutra.txt",
            "-t",
            "T",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "temsEutra.json",
        )
    }

    @Test
    fun testTemsMrdcSplit() {
        test(
            "-i",
            "$path/input/temsMrdcSplit_0.txt,$path/input/temsMrdcSplit_1.txt",
            "-t",
            "T",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "temsMrdcSplit.json",
        )
    }

    @Test
    fun testAmarisoftEutra() {
        test(
            "-i",
            "$path/input/amarisoftEutra.txt",
            "-t",
            "A",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "amarisoftEutra.json",
        )
    }

    @Test
    fun testAmarisoftNr() {
        test(
            "-i",
            "$path/input/amarisoftNr.txt",
            "-t",
            "A",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "amarisoftNr.json",
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleJson/$oracleFilename"

        val result = Cli.test(*args)
        val actual = Json.decodeFromString<Capabilities>(result.stdout)
        val expected = Json.decodeFromString<Capabilities>(File(oraclePath).readText())

        expected.setMetadata("processingTime", actual.getStringMetadata("processingTime") ?: "")

        Assertions.assertEquals(expected, actual)
    }
}
