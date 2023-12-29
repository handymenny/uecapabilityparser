package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CliUeLogOutputTest {
    private val path = "src/test/resources/cli"

    @Test
    fun testWiresharkEutra() {
        test(
            "-i",
            "$path/input/wiresharkEutra.txt",
            "-t",
            "W",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkEutra.json"
        )
    }

    @Test
    fun testWiresharkNr() {
        test(
            "-i",
            "$path/input/wiresharkNr.txt",
            "-t",
            "W",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkNr.json"
        )
    }

    @Test
    fun testWiresharkMrdc() {
        test(
            "-i",
            "$path/input/wiresharkMrdc.txt",
            "-t",
            "W",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkMrdc.json"
        )
    }

    @Test
    fun testWiresharkMrdcSplit() {
        test(
            "-i",
            "$path/input/wiresharkMrdcSplit_0.txt,$path/input/wiresharkMrdcSplit_1.txt",
            "-t",
            "W",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "wiresharkMrdcSplit.json"
        )
    }

    @Test
    fun testNsgEutra() {
        test(
            "-i",
            "$path/input/nsgEutra.txt",
            "-t",
            "N",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgEutra.json"
        )
    }

    @Test
    fun testNsgNr() {
        test(
            "-i",
            "$path/input/nsgNr.txt",
            "-t",
            "N",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgNr.json"
        )
    }

    @Test
    fun testNsgMrdc() {
        test(
            "-i",
            "$path/input/nsgMrdc.txt",
            "-t",
            "N",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgMrdc.json"
        )
    }

    @Test
    fun testNsgMrdcSplit() {
        test(
            "-i",
            "$path/input/nsgMrdcSplit_0.txt,$path/input/nsgMrdcSplit_1.txt",
            "-t",
            "N",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgMrdcSplit.json"
        )
    }

    @Test
    fun testNsgSul() {
        test(
            "-i",
            "$path/input/nsgSul.txt",
            "-t",
            "N",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "nsgSul.json"
        )
    }

    @Test
    fun testOsixMrdc() {
        test(
            "-i",
            "$path/input/osixMrdc.txt",
            "-t",
            "O",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "osixMrdc.json"
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
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexEutra.json"
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
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexNr.json"
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
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "ueCapHexMrdcSplit.json"
        )
    }

    @Test
    fun testQcatMrdc() {
        test(
            "-i",
            "$path/input/qcatMrdc.txt",
            "-t",
            "QC",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "qcatMrdc.json"
        )
    }

    @Test
    fun testQcatNrdc() {
        test(
            "-i",
            "$path/input/qcatNrdc.txt",
            "-t",
            "QC",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "qcatNrdc.json"
        )
    }

    @Test
    fun testTemsEutra() {
        test(
            "-i",
            "$path/input/temsEutra.txt",
            "-t",
            "T",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "temsEutra.json"
        )
    }

    @Test
    fun testTemsMrdcSplit() {
        test(
            "-i",
            "$path/input/temsMrdcSplit_0.txt,$path/input/temsMrdcSplit_1.txt",
            "-t",
            "T",
            "-l",
            "-",
            "--json-pretty-print",
            oracleFilename = "temsMrdcSplit.json"
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleUeLog/$oracleFilename"

        val result = Cli.test(*args)
        val stdoutLines = result.stdout.lines().dropLastWhile(String::isBlank)
        val oracleLines = File(oraclePath).readLines().dropLastWhile(String::isBlank)

        Assertions.assertLinesMatch(oracleLines, stdoutLines)
    }
}
