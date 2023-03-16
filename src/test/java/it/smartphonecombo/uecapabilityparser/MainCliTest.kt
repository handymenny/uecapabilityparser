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
