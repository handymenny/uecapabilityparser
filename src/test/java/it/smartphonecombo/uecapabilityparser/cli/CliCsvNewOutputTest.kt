package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CliCsvNewOutputTest {
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
            "--new-csv-format",
            "true",
            oracleFilename = "carrierPolicy.csv"
        )
    }

    @Test
    fun test0xB0CD() {
        test(
            "-i",
            "$path/input/0xB0CD.txt",
            "-t",
            "Q",
            "-c",
            "-",
            "--new-csv-format",
            "true",
            oracleFilename = "0xB0CD.csv"
        )
    }

    @Test
    fun testMtkLte() {
        test(
            "-i",
            "$path/input/mtkLte.txt",
            "-t",
            "M",
            "-c",
            "-",
            "--new-csv-format",
            "true",
            oracleFilename = "mtkLte.csv"
        )
    }

    @Test
    fun testNvItem() {
        test(
            "-i",
            "$path/input/nvItem.bin",
            "-t",
            "E",
            "-c",
            "-",
            "--new-csv-format",
            "true",
            oracleFilename = "nvItem.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "qctModemCap.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "wiresharkEutra.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "nsgEutra.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "temsEutra.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "amarisoftEutra.csv"
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
            "--new-csv-format",
            "true",
            oracleFilename = "ueCapHexEutra.csv"
        )
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
            "--new-csv-format",
            "true",
            oracleFilename = "0xB826-0xB0CD.csv"
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleCsvNew/$oracleFilename"

        val result = Cli.test(*args)
        val stdoutLines = result.stdout.lines().dropLastWhile(String::isBlank)
        val oracleLines = File(oraclePath).readLines().dropLastWhile(String::isBlank)

        Assertions.assertLinesMatch(oracleLines, stdoutLines)
    }
}
