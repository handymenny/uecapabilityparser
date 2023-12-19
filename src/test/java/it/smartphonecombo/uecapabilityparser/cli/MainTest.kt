package it.smartphonecombo.uecapabilityparser.cli

import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MainTest {
    private val out: ByteArrayOutputStream = ByteArrayOutputStream()
    private val originalOut = System.out
    private val path = "src/test/resources/mainCli"
    private val main = Main

    @BeforeEach
    fun resetConfig() {
        Config.clear()
    }

    private fun setUpStreams() {
        System.setOut(PrintStream(out))
    }

    private fun restoreStreams() {
        System.setOut(originalOut)
    }

    @Test
    fun mainCarrierPolicy() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/carrierPolicy.xml", "-t", "C", "-c", "-"),
            "carrierPolicy.txt"
        )
    }

    @Test
    fun mainCarrierPolicyNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/carrierPolicy.xml",
                "-t",
                "C",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "carrierPolicyNewCsv.txt"
        )
    }

    @Test
    fun main0xB0CD() {
        mainTest(arrayOf("cli", "-i", "$path/input/0xB0CD.txt", "-t", "Q", "-c", "-"), "0xB0CD.txt")
    }

    @Test
    fun main0xB0CDNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB0CD.txt",
                "-t",
                "Q",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "0xB0CDNewCsv.txt"
        )
    }

    @Test
    fun main0xB0CDMultiHex() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/0xB0CDMultiHex.txt",
                "-t",
                "QLTE",
                "--debug",
                "-c",
                "-"
            ),
            "0xB0CDMultiHex.txt"
        )
    }

    @Test
    fun mainMtkLte() {
        mainTest(arrayOf("cli", "-i", "$path/input/mtkLte.txt", "-t", "M", "-c", "-"), "mtkLte.txt")
    }

    @Test
    fun mainMtkLteNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/mtkLte.txt",
                "-t",
                "M",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "mtkLteNewCsv.txt"
        )
    }

    @Test
    fun mainNvItem() {
        mainTest(arrayOf("cli", "-i", "$path/input/nvItem.bin", "-t", "E", "-c", "-"), "nvItem.txt")
    }

    @Test
    fun mainNvItemNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nvItem.bin",
                "-t",
                "E",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "nvItemNewCsv.txt"
        )
    }

    @Test
    fun main0xB826() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826.hex", "-t", "QNR", "-c", "-"),
            "0xB826.txt"
        )
    }

    @Test
    fun main0xB826Multi() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826Multi.txt", "-t", "QNR", "-c", "-"),
            "0xB826Multi.txt"
        )
    }

    @Test
    fun main0xB826MultiDebug() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826Multi.txt", "-t", "QNR", "--debug"),
            "0xB826MultiDebug.txt"
        )
    }

    @Test
    fun main0xB826MultiV14() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826MultiV14.txt", "-t", "QNR", "-c", "-"),
            "0xB826MultiV14.txt"
        )
    }

    @Test
    fun main0xB826MultiScat() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826MultiScat.txt", "-t", "QNR", "-c", "-"),
            "0xB826MultiScat.txt"
        )
    }

    @Test
    fun main0xB826Multi0x9801() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/0xB826Multi0x9801.txt", "-t", "QNR", "-c", "-"),
            "0xB826MultiScat0x9801.txt"
        )
    }

    @Test
    fun mainNrCapPrune() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nrCapPrune.txt", "-t", "CNR", "-c", "-"),
            "nrCapPrune.txt"
        )
    }

    @Test
    fun mainQctModemCap() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/qctModemCap.txt", "-t", "RF", "-c", "-"),
            "qctModemCap.txt"
        )
    }

    @Test
    fun mainQctModemCapNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/qctModemCap.txt",
                "-t",
                "RF",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "qctModemCapNewCsv.txt"
        )
    }

    @Test
    fun mainShannonNrUeCap() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/shannonNrUeCap.binarypb", "-t", "SHNR", "-c", "-"),
            "shannonNrUeCap.txt"
        )
    }

    @Test
    fun mainWiresharkEutraCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkEutra.txt", "-t", "W", "-c", "-"),
            "wiresharkEutraCsv.txt",
        )
    }

    @Test
    fun mainWiresharkEutraNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/wiresharkEutra.txt",
                "-t",
                "W",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "wiresharkEutraNewCsv.txt",
        )
    }

    @Test
    fun mainWiresharkEutraJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/wiresharkEutra.txt",
                "-t",
                "W",
                "-l",
                "-",
                "--json-pretty-print"
            ),
            "wiresharkEutraJson.txt",
        )
    }

    @Test
    fun mainWiresharkNrCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkNr.txt", "-t", "W", "-c", "-"),
            "wiresharkNrCsv.txt",
        )
    }

    @Test
    fun mainWiresharkNrJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkNr.txt", "-t", "W", "-l", "-"),
            "wiresharkNrJson.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-c", "-"),
            "wiresharkMrdcCsv.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcDebug() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-d"),
            "wiresharkMrdcDebug.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-l", "-"),
            "wiresharkMrdcJson.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/wiresharkMrdcSplit_0.txt, $path/input/wiresharkMrdcSplit_1.txt",
                "-t",
                "W",
                "-c",
                "-"
            ),
            "wiresharkMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcSplitJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/wiresharkMrdc.txt, $path/input/wiresharkMrdcSplit_1.txt",
                "-t",
                "W",
                "-l",
                "-"
            ),
            "wiresharkMrdcSplitJson.txt",
        )
    }

    @Test
    fun mainNsgEutraCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgEutra.txt", "-t", "N", "-c", "-"),
            "nsgEutraCsv.txt"
        )
    }

    @Test
    fun mainNsgEutraNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgEutra.txt",
                "-t",
                "N",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "nsgEutraNewCsv.txt"
        )
    }

    @Test
    fun mainNsgEutraJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgEutra.txt", "-t", "N", "-l", "-"),
            "nsgEutraJson.txt"
        )
    }

    @Test
    fun mainNsgNrCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgNr.txt", "-t", "N", "-c", "-"),
            "nsgNrCsv.txt",
        )
    }

    @Test
    fun mainNsgNrJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgNr.txt", "-t", "N", "-l", "-"),
            "nsgNrJson.txt",
        )
    }

    @Test
    fun mainNsgMrdcCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgMrdc.txt", "-t", "N", "-c", "-"),
            "nsgMrdcCsv.txt"
        )
    }

    @Test
    fun mainNsgMrdcJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgMrdc.txt", "-t", "N", "-l", "-"),
            "nsgMrdcJson.txt"
        )
    }

    @Test
    fun mainNsgMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgMrdcSplit_0.txt, $path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-c",
                "-"
            ),
            "nsgMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainNsgMrdcSplitJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/nsgMrdc.txt, $path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-l",
                "-"
            ),
            "nsgMrdcSplitJson.txt",
        )
    }

    @Test
    fun mainNsgSulCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgSul.txt", "-t", "N", "-c", "-"),
            "nsgSulCsv.txt",
        )
    }

    @Test
    fun mainNsgSulJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/nsgSul.txt", "-t", "N", "-l", "-"),
            "nsgSulJson.txt",
        )
    }

    @Test
    fun mainOsixMrdcCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/osixMrdc.txt", "-t", "O", "-c", "-"),
            "osixMrdcCsv.txt"
        )
    }

    @Test
    fun mainOsixMrdcJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/osixMrdc.txt", "-t", "O", "-l", "-"),
            "osixMrdcJson.txt"
        )
    }

    @Test
    fun mainUeCapHexEutraNewCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexEutra.hex",
                "-t",
                "H",
                "--sub-types",
                "LTE",
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "ueCapHexEutraNewCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexEutraJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexEutra.hex",
                "-t",
                "H",
                "--sub-types",
                "LTE",
                "-l",
                "-"
            ),
            "ueCapHexEutraJson.txt",
        )
    }

    @Test
    fun mainUeCapHexNrCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexNr.hex",
                "-t",
                "H",
                "--sub-types",
                "NR",
                "-c",
                "-"
            ),
            "ueCapHexNrCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexNrJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexNr.hex",
                "-t",
                "H",
                "--sub-types",
                "NR",
                "-l",
                "-"
            ),
            "ueCapHexNrJson.txt",
        )
    }

    @Test
    fun mainUeCapHexMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex, $path/input/ueCapHexMrdcSplit_nr.hex, $path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "-t",
                "H",
                "--sub-types",
                "LTE,NR,ENDC",
                "-c",
                "-"
            ),
            "ueCapHexMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexMrdcSplitJson() {
        mainTest(
            arrayOf(
                "cli",
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex, $path/input/ueCapHexMrdcSplit_nr.hex, $path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "-t",
                "H",
                "--sub-types",
                "LTE,NR,ENDC",
                "-l",
                "-"
            ),
            "ueCapHexMrdcSplitJson.txt",
        )
    }

    @Test
    fun mainQcatMrdcCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/qcatMrdc.txt", "-t", "QC", "-c", "-"),
            "qcatMrdcCsv.txt"
        )
    }

    @Test
    fun mainQcatMrdcJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/qcatMrdc.txt", "-t", "QC", "-l", "-"),
            "qcatMrdcJson.txt"
        )
    }

    @Test
    fun mainQcatNrdcCsv() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/qcatNrdc.txt", "-t", "QC", "-c", "-"),
            "qcatNrdcCsv.txt"
        )
    }

    @Test
    fun mainQcatNrdcJson() {
        mainTest(
            arrayOf("cli", "-i", "$path/input/qcatNrdc.txt", "-t", "QC", "-l", "-"),
            "qcatNrdcJson.txt"
        )
    }

    @Test
    fun mainPcapCsv() {
        mainTest(arrayOf("cli", "-i", "$path/input/pcap.pcap", "-t", "P", "-c", "-"), "pcap.txt")
    }

    @Test
    fun mainMultiInputCsv() {
        mainTest(
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
                "-c",
                "-"
            ),
            "0xB826-0xB0CD.txt"
        )
    }

    @Test
    fun mainMultiInputNewCsv() {
        mainTest(
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
                "-c",
                "-",
                "--new-csv-format",
                "true"
            ),
            "0xB826-0xB0CDNewCsv.txt"
        )
    }

    @Test
    fun mainMultiScatCsv() {
        Assumptions.assumeTrue(scatAvailable)
        mainTest(
            arrayOf(
                "cli",
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
                "-"
            ),
            "scat.txt"
        )
    }

    private fun mainTest(args: Array<String>, oracleFilename: String) {
        setUpStreams()
        main.main(args)
        restoreStreams()

        Assertions.assertLinesMatch(
            File("$path/oracle/$oracleFilename").readLines().dropLastWhile { it.isBlank() },
            out.toString().lines().dropLastWhile { it.isBlank() },
        )
    }
}
