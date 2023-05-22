package it.smartphonecombo.uecapabilityparser.cli

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CliJsonOutputTest {
    private val out: ByteArrayOutputStream = ByteArrayOutputStream()
    private val originalOut = System.out
    private val path = "src/test/resources/mainCli"
    private val cli = Clikt

    private fun setUpStreams() {
        System.setOut(PrintStream(out))
    }

    private fun restoreStreams() {
        System.setOut(originalOut)
    }

    @Test
    fun carrierPolicyJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/carrierPolicy.xml",
                "-t",
                "C",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "carrierPolicy.json"
        )
    }

    @Test
    fun b0CDJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/0xB0CD.txt", "-t", "Q", "-j", "-", "--json-pretty-print"),
            "0xB0CD.json"
        )
    }

    @Test
    fun mtkLteJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/mtkLte.txt", "-t", "M", "-j", "-", "--json-pretty-print"),
            "mtkLte.json"
        )
    }

    @Test
    fun nvItemJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/nvItem.bin", "-t", "E", "-j", "-", "--json-pretty-print"),
            "nvItem.json"
        )
    }

    @Test
    fun b826JsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/0xB826.hex", "-t", "QNR", "-j", "-", "--json-pretty-print"),
            "0xB826.json"
        )
    }

    @Test
    fun b826MultiJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/0xB826Multi.txt",
                "-t",
                "QNR",
                "--multi",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826Multi.json"
        )
    }

    @Test
    fun nrCapPruneJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/nrCapPrune.txt",
                "-t",
                "CNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nrCapPrune.json"
        )
    }

    @Test
    fun wiresharkEutraJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkEutra.txt",
                "-t",
                "W",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "wiresharkEutra.json",
        )
    }

    @Test
    fun wiresharkNrJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkNr.txt",
                "-t",
                "W",
                "--defaultNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "wiresharkNr.json",
        )
    }

    @Test
    fun wiresharkMrdcJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkMrdc.txt",
                "-t",
                "W",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "wiresharkMrdc.json",
        )
    }

    @Test
    fun wiresharkMrdcSplitJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkMrdcSplit_0.txt",
                "--inputENDC",
                "$path/input/wiresharkMrdcSplit_1.txt",
                "-t",
                "W",
                "-j",
                "-"
            ),
            "wiresharkMrdcSplit.json",
        )
    }

    @Test
    fun nsgEutraJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/nsgEutra.txt", "-t", "N", "-j", "-", "--json-pretty-print"),
            "nsgEutra.json"
        )
    }

    @Test
    fun nsgNrJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/nsgNr.txt",
                "-t",
                "N",
                "--defaultNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nsgNr.json",
        )
    }

    @Test
    fun nsgMrdcJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/nsgMrdc.txt", "-t", "N", "-j", "-", "--json-pretty-print"),
            "nsgMrdc.json"
        )
    }

    @Test
    fun nsgMrdcSplitJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/nsgMrdcSplit_0.txt",
                "--inputNR",
                "$path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-j",
                "-"
            ),
            "nsgMrdcSplit.json",
        )
    }

    @Test
    fun osixMrdcJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/osixMrdc.txt", "-t", "O", "-j", "-", "--json-pretty-print"),
            "osixMrdc.json"
        )
    }

    @Test
    fun ueCapHexEutraJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/ueCapHexEutra.hex",
                "-t",
                "H",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "ueCapHexEutra.json",
        )
    }

    @Test
    fun ueCapHexNrJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/ueCapHexNr.hex",
                "-t",
                "H",
                "--defaultNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "ueCapHexNr.json",
        )
    }

    @Test
    fun ueCapHexMrdcSplitJsonOutput() {
        cliTest(
            arrayOf(
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex",
                "--inputNR",
                "$path/input/ueCapHexMrdcSplit_nr.hex",
                "--inputENDC",
                "$path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "-t",
                "H",
                "-j",
                "-"
            ),
            "ueCapHexMrdcSplit.json",
        )
    }

    @Test
    fun qcatMrdcJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/qcatMrdc.txt", "-t", "QC", "-j", "-", "--json-pretty-print"),
            "qcatMrdc.json"
        )
    }

    @Test
    fun qcatNrdcJsonOutput() {
        cliTest(
            arrayOf("-i", "$path/input/qcatNrdc.txt", "-t", "QC", "-j", "-", "--json-pretty-print"),
            "qcatNrdc.json"
        )
    }

    private fun cliTest(args: Array<String>, oracleFilename: String) {
        setUpStreams()
        cli.main(args)
        restoreStreams()

        val actual = Json.decodeFromString<Capabilities>(out.toString())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracleJson/$oracleFilename").readText())

        // Override dynamic properties
        expected.parserVersion = actual.parserVersion
        expected.timestamp = actual.timestamp
        expected.id = actual.id
        expected.setMetadata("processingTime", actual.getStringMetadata("processingTime") ?: "")

        Assertions.assertEquals(expected, actual)
    }
}
