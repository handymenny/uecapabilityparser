package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.util.Output
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportLteCarrierPolicyTest {
    private fun parse(inputFilename: String, oracleFilename: String) {
        val path = "src/test/resources/carrierPolicy/"

        val inputPath = "$path/input/$inputFilename"
        val oraclePath = "$path/oracle/$oracleFilename"

        val capabilities = ImportLteCarrierPolicy.parse(File(inputPath).inputStream())
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        val expectedCsv =
            File(oraclePath).bufferedReader().readLines().dropLastWhile { it.isBlank() }

        Assertions.assertLinesMatch(expectedCsv, actualCsv)
    }

    @Test
    fun parseNoMimo() {
        parse("noMimo.xml", "noMimo.csv")
    }

    @Test
    fun parseMimoBcsAll() {
        parse("mimoBcsAll.xml", "mimoBcsAll.csv")
    }

    @Test
    fun parseMimoMultiBcs() {
        parse("mimoMultiBcs.xml", "mimoMultiBcs.csv")
    }

    @Test
    fun parseMimoMultiBcsAbove9() {
        parse("mimoMultiBcsAbove9.xml", "mimoMultiBcsAbove9.csv")
    }

    @Test
    fun parseFullCarrierPolicy() {
        parse("fullCarrierPolicy.xml", "fullCarrierPolicy.csv")
    }

    @Test
    fun parseLteCapPrune() {
        parse("lteCapPrune.txt", "lteCapPrune.csv")
    }

    @Test
    fun parseLteCapPruneWithSpaces() {
        parse("lteCapPruneWithSpaces.txt", "lteCapPruneWithSpaces.csv")
    }
}
