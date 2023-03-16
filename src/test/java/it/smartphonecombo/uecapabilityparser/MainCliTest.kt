package it.smartphonecombo.uecapabilityparser

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MainCliTest {
    private val out: ByteArrayOutputStream = ByteArrayOutputStream()
    private val originalOut = System.out
    private val path = "src/test/resources/mainCli"
    private val main = MainCli

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
    fun mainHelpShort() {
        mainTest(arrayOf("-h"), "help.txt")
    }

    @Test
    fun mainHelpLong() {
        mainTest(arrayOf("--help"), "help.txt")
    }

    @Test
    fun mainCarrierPolicy() {
        mainTest(
            arrayOf("-i", "$path/input/carrierPolicy.xml", "-t", "C", "-c"),
            "carrierPolicy.txt"
        )
    }

    @Test
    fun main0xB0CD() {
        mainTest(arrayOf("-i", "$path/input/0xB0CD.txt", "-t", "Q", "-c"), "0xB0CD.txt")
    }

    @Test
    fun mainMtkLte() {
        mainTest(arrayOf("-i", "$path/input/mtkLte.txt", "-t", "M", "-c"), "mtkLte.txt")
    }

    @Test
    fun mainNvItem() {
        mainTest(arrayOf("-i", "$path/input/nvItem.bin", "-t", "E", "-c"), "nvItem.txt")
    }

    @Test
    fun main0xB826() {
        mainTest(arrayOf("-i", "$path/input/0xB826.hex", "-t", "QNR", "-c"), "0xB826.txt")
    }

    @Test
    fun main0xB826Multi() {
        mainTest(
            arrayOf("-i", "$path/input/0xB826Multi.txt", "-t", "QNR", "-multi", "-c"),
            "0xB826Multi.txt"
        )
    }

    @Test
    fun main0xB826MultiDebug() {
        mainTest(
            arrayOf("-i", "$path/input/0xB826Multi.txt", "-t", "QNR", "-multi", "--debug"),
            "0xB826MultiDebug.txt"
        )
    }

    @Test
    fun mainNrCapPrune() {
        mainTest(arrayOf("-i", "$path/input/nrCapPrune.txt", "-t", "CNR", "-c"), "nrCapPrune.txt")
    }

    @Test
    fun mainWiresharkEutraCsv() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkEutra.txt", "-t", "W", "-c"),
            "wiresharkEutraCsv.txt",
        )
    }

    @Test
    fun mainWiresharkEutraJson() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkEutra.txt", "-t", "W", "-l"),
            "wiresharkEutraJson.txt",
        )
    }

    @Test
    fun mainWiresharkNrCsv() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkNr.txt", "-t", "W", "-defaultNR", "-c"),
            "wiresharkNrCsv.txt",
        )
    }

    @Test
    fun mainWiresharkNrJson() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkNr.txt", "-t", "W", "-defaultNR", "-l"),
            "wiresharkNrJson.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcCsv() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-c"),
            "wiresharkMrdcCsv.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcDebug() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-d"),
            "wiresharkMrdcDebug.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcJson() {
        mainTest(
            arrayOf("-i", "$path/input/wiresharkMrdc.txt", "-t", "W", "-l"),
            "wiresharkMrdcJson.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkMrdcSplit_0.txt",
                "-inputENDC",
                "$path/input/wiresharkMrdcSplit_1.txt",
                "-t",
                "W",
                "-c",
            ),
            "wiresharkMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainWiresharkMrdcSplitJson() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/wiresharkMrdc.txt",
                "-inputENDC",
                "$path/input/wiresharkMrdcSplit_1.txt",
                "-t",
                "W",
                "-l",
            ),
            "wiresharkMrdcSplitJson.txt",
        )
    }

    @Test
    fun mainNsgEutraCsv() {
        mainTest(arrayOf("-i", "$path/input/nsgEutra.txt", "-t", "N", "-c"), "nsgEutraCsv.txt")
    }

    @Test
    fun mainNsgEutraJson() {
        mainTest(arrayOf("-i", "$path/input/nsgEutra.txt", "-t", "N", "-l"), "nsgEutraJson.txt")
    }

    @Test
    fun mainNsgNrCsv() {
        mainTest(
            arrayOf("-i", "$path/input/nsgNr.txt", "-t", "N", "-defaultNR", "-c"),
            "nsgNrCsv.txt",
        )
    }

    @Test
    fun mainNsgNrJson() {
        mainTest(
            arrayOf("-i", "$path/input/nsgNr.txt", "-t", "N", "-defaultNR", "-l"),
            "nsgNrJson.txt",
        )
    }

    @Test
    fun mainNsgMrdcCsv() {
        mainTest(arrayOf("-i", "$path/input/nsgMrdc.txt", "-t", "N", "-c"), "nsgMrdcCsv.txt")
    }

    @Test
    fun mainNsgMrdcJson() {
        mainTest(arrayOf("-i", "$path/input/nsgMrdc.txt", "-t", "N", "-l"), "nsgMrdcJson.txt")
    }

    @Test
    fun mainNsgMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/nsgMrdcSplit_0.txt",
                "-inputNR",
                "$path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-c",
            ),
            "nsgMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainNsgMrdcSplitJson() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/nsgMrdc.txt",
                "-inputNR",
                "$path/input/nsgMrdcSplit_1.txt",
                "-t",
                "N",
                "-l",
            ),
            "nsgMrdcSplitJson.txt",
        )
    }

    @Test
    fun mainOsixMrdcCsv() {
        mainTest(arrayOf("-i", "$path/input/osixMrdc.txt", "-t", "N", "-c"), "osixMrdcCsv.txt")
    }

    @Test
    fun mainOsixMrdcJson() {
        mainTest(arrayOf("-i", "$path/input/osixMrdc.txt", "-t", "N", "-l"), "osixMrdcJson.txt")
    }

    @Test
    fun mainUeCapHexEutraCsv() {
        mainTest(
            arrayOf("-i", "$path/input/ueCapHexEutra.hex", "-t", "H", "-c"),
            "ueCapHexEutraCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexEutraJson() {
        mainTest(
            arrayOf("-i", "$path/input/ueCapHexEutra.hex", "-t", "H", "-l"),
            "ueCapHexEutraJson.txt",
        )
    }

    @Test
    fun mainUeCapHexNrCsv() {
        mainTest(
            arrayOf("-i", "$path/input/ueCapHexNr.hex", "-t", "H", "-defaultNR", "-c"),
            "ueCapHexNrCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexNrJson() {
        mainTest(
            arrayOf("-i", "$path/input/ueCapHexNr.hex", "-t", "H", "-defaultNR", "-l"),
            "ueCapHexNrJson.txt",
        )
    }

    @Test
    fun mainUeCapHexMrdcSplitCsv() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex",
                "-inputNR",
                "$path/input/ueCapHexMrdcSplit_nr.hex",
                "-inputENDC",
                "$path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "-t",
                "H",
                "-c",
            ),
            "ueCapHexMrdcSplitCsv.txt",
        )
    }

    @Test
    fun mainUeCapHexMrdcSplitJson() {
        mainTest(
            arrayOf(
                "-i",
                "$path/input/ueCapHexMrdcSplit_eutra.hex",
                "-inputNR",
                "$path/input/ueCapHexMrdcSplit_nr.hex",
                "-inputENDC",
                "$path/input/ueCapHexMrdcSplit_eutra-nr.hex",
                "-t",
                "H",
                "-l",
            ),
            "ueCapHexMrdcSplitJson.txt",
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
