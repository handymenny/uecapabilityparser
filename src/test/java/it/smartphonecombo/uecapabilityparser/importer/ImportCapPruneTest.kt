package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.util.Utility
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportCapPruneTest {
    private fun parse(
        inputFilename: String,
        oracleEnDcFilename: String,
        oracleNrCaFilename: String
    ) {
        val path = "src/test/resources/nrCapPrune/"

        val inputPath = "$path/input/$inputFilename"
        val oracleEnDcPath = "$path/oracle/$oracleEnDcFilename"
        val oracleNrCaPath = "$path/oracle/$oracleNrCaFilename"

        val capabilities = ImportNrCapPrune.parse(File(inputPath).inputStream())

        val actualEnDcCsv =
            Utility.toCsv(capabilities.enDcCombos ?: emptyList()).lines().dropLastWhile {
                it.isBlank()
            }
        val expectedEnDcCsv =
            File(oracleEnDcPath).bufferedReader().readLines().dropLastWhile { it.isBlank() }
        Assertions.assertLinesMatch(expectedEnDcCsv, actualEnDcCsv)

        val actualNrCaCsv =
            Utility.toCsv(capabilities.nrCombos ?: emptyList()).lines().dropLastWhile {
                it.isBlank()
            }
        val expectedNrCaCsv =
            File(oracleNrCaPath).bufferedReader().readLines().dropLastWhile { it.isBlank() }

        Assertions.assertLinesMatch(expectedNrCaCsv, actualNrCaCsv)
    }

    @Test
    fun parseNoMimoFR1() {
        parse("noMimoFR1.txt", "noMimoFR1-EN-DC.csv", "noMimoFR1-NR.csv")
    }

    @Test
    fun parseNoMimoFR2() {
        parse("noMimoFR2.txt", "noMimoFR2-EN-DC.csv", "noMimoFR2-NR.csv")
    }

    @Test
    fun parseMimoFR1() {
        parse("mimoFR1.txt", "mimoFR1-EN-DC.csv", "mimoFR1-NR.csv")
    }

    @Test
    fun parseMimoFR2() {
        parse("mimoFR2.txt", "mimoFR2-EN-DC.csv", "mimoFR2-NR.csv")
    }
}
