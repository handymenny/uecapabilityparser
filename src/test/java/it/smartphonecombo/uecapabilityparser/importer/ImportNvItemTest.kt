package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.util.Output
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportNvItemTest {
    private fun parse(inputFilename: String, oracleFilename: String) {
        val path = "src/test/resources/nvitem/"

        val inputPath = "$path/input/$inputFilename"
        val oraclePath = "$path/oracle/$oracleFilename"

        val capabilities = ImportNvItem.parse(File(inputPath).readBytes())
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        val expectedCsv =
            File(oraclePath).bufferedReader().readLines().dropLastWhile { it.isBlank() }

        Assertions.assertLinesMatch(expectedCsv, actualCsv)
    }

    @Test
    fun parseNvItem137() {
        parse("28874_137.bin", "28874_137.csv")
    }

    @Test
    fun parseNvItem137201() {
        parse("28874_137_201.bin", "28874_137_201.csv")
    }

    @Test
    fun parseNvItem201() {
        parse("28874_201.bin", "28874_201.csv")
    }

    @Test
    fun parseNvItem333() {
        parse("28874_333.bin", "28874_333.csv")
    }

    @Test
    fun parseNvItemZlib() {
        parse("28874_zlib.bin", "28874_zlib.csv")
    }
}
