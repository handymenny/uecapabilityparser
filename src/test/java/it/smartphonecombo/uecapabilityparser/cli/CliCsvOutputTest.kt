package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test

internal class CliCsvOutputTest {
    private val path = "src/test/resources/cli"

    @Test
    fun testCarrierPolicy() {
        test(
            "-i",
            "$path/input/carrierPolicy.xml",
            "-t",
            "C",
            "-c",
            "-",
            oracleFilename = "carrierPolicy.csv"
        )
    }

    @Test
    fun test0xB0CD() {
        test("-i", "$path/input/0xB0CD.txt", "-t", "Q", "-c", "-", oracleFilename = "0xB0CD.csv")
    }

    @Test
    fun test0xB0CDMultiHex() {
        test(
            "-i",
            "$path/input/0xB0CDMultiHex.txt",
            "-t",
            "QLTE",
            "-c",
            "-",
            oracleFilename = "0xB0CDMultiHex.csv"
        )
    }

    @Test
    fun testMtkLte() {
        test("-i", "$path/input/mtkLte.txt", "-t", "M", "-c", "-", oracleFilename = "mtkLte.csv")
    }

    @Test
    fun testNvItem() {
        test("-i", "$path/input/nvItem.bin", "-t", "E", "-c", "-", oracleFilename = "nvItem.csv")
    }

    @Test
    fun test0xB826() {
        test("-i", "$path/input/0xB826.hex", "-t", "QNR", "-c", "-", oracleFilename = "0xB826.csv")
    }

    @Test
    fun test0xB826Multi() {
        test(
            "-i",
            "$path/input/0xB826Multi.txt",
            "-t",
            "QNR",
            "-c",
            "-",
            oracleFilename = "0xB826Multi.csv"
        )
    }

    @Test
    fun test0xB826MultiV14() {
        test(
            "-i",
            "$path/input/0xB826MultiV14.txt",
            "-t",
            "QNR",
            "-c",
            "-",
            oracleFilename = "0xB826MultiV14.csv"
        )
    }

    @Test
    fun test0xB826MultiScat() {
        test(
            "-i",
            "$path/input/0xB826MultiScat.txt",
            "-t",
            "QNR",
            "-c",
            "-",
            oracleFilename = "0xB826MultiScat.csv"
        )
    }

    @Test
    fun test0xB826Multi0x9801() {
        test(
            "-i",
            "$path/input/0xB826Multi0x9801.txt",
            "-t",
            "QNR",
            "-c",
            "-",
            oracleFilename = "0xB826MultiScat0x9801.csv"
        )
    }

    @Test
    fun testNrCapPrune() {
        test(
            "-i",
            "$path/input/nrCapPrune.txt",
            "-t",
            "CNR",
            "-c",
            "-",
            oracleFilename = "nrCapPrune.csv"
        )
    }

    @Test
    fun testQctModemCap() {
        test(
            "-i",
            "$path/input/qctModemCap.txt",
            "-t",
            "RF",
            "-c",
            "-",
            oracleFilename = "qctModemCap.csv"
        )
    }

    @Test
    fun testShannonNrUeCap() {
        test(
            "-i",
            "$path/input/shannonNrUeCap.binarypb",
            "-t",
            "SHNR",
            "-c",
            "-",
            oracleFilename = "shannonNrUeCap.csv"
        )
    }

    @Test
    fun testWiresharkEutra() {
        test(
            "-i",
            "$path/input/wiresharkEutra.txt",
            "-t",
            "W",
            "-c",
            "-",
            oracleFilename = "wiresharkEutra.csv"
        )
    }

    @Test
    fun testWiresharkNr() {
        test(
            "-i",
            "$path/input/wiresharkNr.txt",
            "-t",
            "W",
            "-c",
            "-",
            oracleFilename = "wiresharkNr.csv"
        )
    }

    @Test
    fun testWiresharkMrdc() {
        test(
            "-i",
            "$path/input/wiresharkMrdc.txt",
            "-t",
            "W",
            "-c",
            "-",
            oracleFilename = "wiresharkMrdc.csv"
        )
    }

    @Test
    fun testWiresharkMrdcSplit() {
        test(
            "-i",
            "$path/input/wiresharkMrdcSplit_0.txt,$path/input/wiresharkMrdcSplit_1.txt",
            "-t",
            "W",
            "-c",
            "-",
            oracleFilename = "wiresharkMrdcSplit.csv"
        )
    }

    @Test
    fun testNsgEutra() {
        test(
            "-i",
            "$path/input/nsgEutra.txt",
            "-t",
            "N",
            "-c",
            "-",
            oracleFilename = "nsgEutra.csv"
        )
    }

    @Test
    fun testNsgNr() {
        test("-i", "$path/input/nsgNr.txt", "-t", "N", "-c", "-", oracleFilename = "nsgNr.csv")
    }

    @Test
    fun testNsgMrdc() {
        test("-i", "$path/input/nsgMrdc.txt", "-t", "N", "-c", "-", oracleFilename = "nsgMrdc.csv")
    }

    @Test
    fun testNsgMrdcSplit() {
        test(
            "-i",
            "$path/input/nsgMrdcSplit_0.txt,$path/input/nsgMrdcSplit_1.txt",
            "-t",
            "N",
            "-c",
            "-",
            oracleFilename = "nsgMrdcSplit.csv"
        )
    }

    @Test
    fun testNsgSul() {
        test("-i", "$path/input/nsgSul.txt", "-t", "N", "-c", "-", oracleFilename = "nsgSul.csv")
    }

    @Test
    fun testOsixMrdc() {
        test(
            "-i",
            "$path/input/osixMrdc.txt",
            "-t",
            "O",
            "-c",
            "-",
            oracleFilename = "osixMrdc.csv"
        )
    }

    @Test
    fun testUeCapHexEutra() {
        test(
            "-i",
            "$path/input/ueCapHexEutra.hex",
            "-t",
            "H",
            "--sub-types",
            "LTE",
            "-c",
            "-",
            oracleFilename = "ueCapHexEutra.csv"
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
            "-c",
            "-",
            oracleFilename = "ueCapHexNr.csv"
        )
    }

    @Test
    fun testUeCapHexMrdcSplit() {
        test(
            "-i",
            "$path/input/ueCapHexMrdcSplit_eutra.hex,$path/input/ueCapHexMrdcSplit_nr.hex,$path/input/ueCapHexMrdcSplit_eutra-nr.hex",
            "-t",
            "H",
            "--sub-types",
            "LTE,NR,ENDC",
            "-c",
            "-",
            oracleFilename = "ueCapHexMrdcSplit.csv"
        )
    }

    @Test
    fun testQcatMrdc() {
        test(
            "-i",
            "$path/input/qcatMrdc.txt",
            "-t",
            "QC",
            "-c",
            "-",
            oracleFilename = "qcatMrdc.csv"
        )
    }

    @Test
    fun testQcatNrdc() {
        test(
            "-i",
            "$path/input/qcatNrdc.txt",
            "-t",
            "QC",
            "-c",
            "-",
            oracleFilename = "qcatNrdc.csv"
        )
    }

    @Test
    fun testTemsEutra() {
        test(
            "-i",
            "$path/input/temsEutra.txt",
            "-t",
            "T",
            "-c",
            "-",
            oracleFilename = "temsEutra.csv"
        )
    }

    @Test
    fun testTemsMrdcSplit() {
        test(
            "-i",
            "$path/input/temsMrdcSplit_0.txt,$path/input/temsMrdcSplit_1.txt",
            "-t",
            "T",
            "-c",
            "-",
            oracleFilename = "temsMrdcSplit.csv"
        )
    }

    @Test
    fun testAmarisoftEutra() {
        test(
            "-i",
            "$path/input/amarisoftEutra.txt",
            "-t",
            "A",
            "-c",
            "-",
            oracleFilename = "amarisoftEutra.csv"
        )
    }

    @Test
    fun testAmarisoftNr() {
        test(
            "-i",
            "$path/input/amarisoftNr.txt",
            "-t",
            "A",
            "-c",
            "-",
            oracleFilename = "amarisoftNr.csv"
        )
    }

    @Test
    fun testPcap() {
        test("-i", "$path/input/pcap.pcap", "-t", "P", "-c", "-", oracleFilename = "pcap.txt")
    }

    @Test
    fun testMultiInput() {
        test(
            "-i",
            "$path/input/0xB826.hex",
            "-t",
            "QNR",
            "-i",
            "$path/input/0xB0CD.txt",
            "-t",
            "Q",
            "-c",
            "-",
            oracleFilename = "0xB826-0xB0CD.csv"
        )
    }

    @Test
    fun testMultiScat() {
        Assumptions.assumeTrue(scatAvailable)
        test(
            "-i",
            "$path/input/scat.dlf",
            "-t",
            "DLF",
            "-i",
            "$path/input/scat.sdm",
            "-t",
            "SDM",
            "-i",
            "$path/input/scat.hdf",
            "-t",
            "HDF",
            "-i",
            "$path/input/scat.qmdl",
            "-t",
            "QMDL",
            "-c",
            "-",
            oracleFilename = "scat.csv"
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleCsv/$oracleFilename"

        val result = Cli.test(*args)
        val stdoutLines = result.stdout.lines().dropLastWhile(String::isBlank)
        val oracleLines = File(oraclePath).readLines().dropLastWhile(String::isBlank)

        Assertions.assertLinesMatch(oracleLines, stdoutLines)
    }
}
