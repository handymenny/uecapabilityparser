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
                "cli",
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
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB0CD.txt",
                "-t",
                "Q",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB0CD.json"
        )
    }

    @Test
    fun b0CDMultiHexJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB0CDMultiHex.txt",
                "-t",
                "QLTE",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB0CDMultiHex.json"
        )
    }

    @Test
    fun mtkLteJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/mtkLte.txt",
                "-t",
                "M",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "mtkLte.json"
        )
    }

    @Test
    fun nvItemJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nvItem.bin",
                "-t",
                "E",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nvItem.json"
        )
    }

    @Test
    fun b826JsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826.hex",
                "-t",
                "QNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826.json"
        )
    }

    @Test
    fun b826MultiJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826Multi.txt",
                "-t",
                "QNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826Multi.json"
        )
    }

    @Test
    fun b826MultiV14JsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826MultiV14.txt",
                "-t",
                "QNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826MultiV14.json"
        )
    }

    @Test
    fun b826MultiScatJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826MultiScat.txt",
                "-t",
                "QNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826MultiScat.json"
        )
    }

    @Test
    fun b826Multi0x9801JsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826Multi0x9801.txt",
                "-t",
                "QNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "0xB826Multi0x9801.json"
        )
    }

    @Test
    fun nrCapPruneJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
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
    fun mainQctModemCap() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/qctModemCap.txt",
                "-t",
                "RF",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "qctModemCap.json"
        )
    }

    @Test
    fun mainShannonNrUeCap() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/shannonNrUeCap.binarypb",
                "-t",
                "SHNR",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "shannonNrUeCap.json"
        )
    }

    @Test
    fun wiresharkEutraJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
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
                "cli",
                "-i",
                "$path/input/wiresharkNr.txt",
                "-t",
                "W",
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
                "cli",
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
                "cli",
                "-i",
                "$path/input/wiresharkMrdcSplit_0.txt, $path/input/wiresharkMrdcSplit_1.txt",
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
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgEutra.txt",
                "-t",
                "N",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nsgEutra.json"
        )
    }

    @Test
    fun nsgNrJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgNr.txt",
                "-t",
                "N",
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
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgMrdc.txt",
                "-t",
                "N",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nsgMrdc.json"
        )
    }

    @Test
    fun nsgMrdcSplitJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgMrdcSplit_0.txt, $path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-j",
                "-"
            ),
            "nsgMrdcSplit.json",
        )
    }

    @Test
    fun nsgSulJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgSul.txt",
                "-t",
                "N",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "nsgSul.json"
        )
    }

    @Test
    fun osixMrdcJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/osixMrdc.txt",
                "-t",
                "O",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "osixMrdc.json"
        )
    }

    @Test
    fun ueCapHexEutraJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexEutra.hex",
                "--subTypes",
                "LTE",
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
                "cli",
                "-i",
                "$path/input/ueCapHexNr.hex",
                "-t",
                "H",
                "--subTypes",
                "NR",
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
                "cli",
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex, $path/input/ueCapHexMrdcSplit_nr.hex, $path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "--subTypes",
                "LTE,NR,ENDC",
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
            arrayOf(
                "cli",
                "-i",
                "$path/input/qcatMrdc.txt",
                "-t",
                "QC",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "qcatMrdc.json"
        )
    }

    @Test
    fun qcatNrdcJsonOutput() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/qcatNrdc.txt",
                "-t",
                "QC",
                "-j",
                "-",
                "--json-pretty-print"
            ),
            "qcatNrdc.json"
        )
    }

    @Test
    fun mainMultiInputCsv() {
        cliTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB826.hex",
                "-t",
                "QNR",
                "-i",
                "$path/input/0xB0CD.txt",
                "-t",
                "Q",
                "-j",
                "-"
            ),
            "0xB826-0xB0CD.json",
            true
        )
    }

    private fun cliTest(args: Array<String>, oracleFilename: String, multi: Boolean = false) {
        setUpStreams()
        cli.main(args)
        restoreStreams()

        if (!multi) {
            val actual = Json.decodeFromString<Capabilities>(out.toString())
            val expected =
                Json.decodeFromString<Capabilities>(
                    File("$path/oracleJson/$oracleFilename").readText()
                )

            // Override dynamic properties
            expected.parserVersion = actual.parserVersion
            expected.timestamp = actual.timestamp
            expected.id = actual.id
            expected.setMetadata("processingTime", actual.getStringMetadata("processingTime") ?: "")

            Assertions.assertEquals(expected, actual)
        } else {
            val actual = Json.decodeFromString<Array<Capabilities>>(out.toString())
            val expected =
                Json.decodeFromString<Array<Capabilities>>(
                    File("$path/oracleJson/$oracleFilename").readText()
                )

            // Check size
            Assertions.assertEquals(expected.size, actual.size)

            // Override dynamic properties

            for (i in expected.indices) {
                val capA = actual[i]
                val capE = expected[i]

                capE.parserVersion = capA.parserVersion
                capE.timestamp = capA.timestamp
                capE.id = capA.id
                capE.setMetadata("processingTime", capA.getStringMetadata("processingTime") ?: "")
            }

            // check files
            Assertions.assertArrayEquals(expected, actual)
        }
    }
}
