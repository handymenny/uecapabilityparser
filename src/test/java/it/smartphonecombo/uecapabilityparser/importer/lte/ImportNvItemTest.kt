package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.Utility
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportNvItemTest {

    companion object {
        val importerNvItem = ImportNvItem()
    }

    private fun parse(inputFilename: String, oracleFilename: String) {
        val path = "src/test/resources/nvitem/"

        val inputPath = "$path/input/$inputFilename"
        val oraclePath = "$path/oracle/$oracleFilename"

        val capabilities = importerNvItem.parse(inputPath)
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
}
