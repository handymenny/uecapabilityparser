package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CliDebugOutputTest {
    private val path = "src/test/resources/cli"

    @BeforeEach
    @AfterEach
    fun resetConfig() {
        Config.clear()
    }

    @Test
    fun test0xB0CDMultiHex() {
        test(
            "-i",
            "$path/input/0xB0CDMultiHex.txt",
            "-t",
            "QLTE",
            "-d",
            oracleFilename = "0xB0CDMultiHex.txt",
        )
    }

    @Test
    fun test0xB826Multi() {
        test(
            "-i",
            "$path/input/0xB826Multi.txt",
            "-t",
            "QNR",
            "--debug",
            oracleFilename = "0xB826Multi.txt",
        )
    }

    @Test
    fun testWiresharkMrdc() {
        test(
            "-i",
            "$path/input/wiresharkMrdc.txt",
            "-t",
            "W",
            "-d",
            oracleFilename = "wiresharkMrdc.txt",
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleDebug/$oracleFilename"

        val result = Cli.test(*args)
        val stdoutLines = result.stdout.lines().dropLastWhile(String::isBlank)
        val oracleLines = File(oraclePath).readLines().dropLastWhile(String::isBlank)

        Assertions.assertLinesMatch(oracleLines, stdoutLines)
    }
}
