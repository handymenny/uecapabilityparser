package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportLteCarrierPolicyTest {
    private val path = "src/test/resources/carrierPolicy/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportLteCarrierPolicy.parse(File(filePath).readBytes())

        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())
        // Override dynamic properties
        expected.parserVersion = actual.parserVersion
        expected.timestamp = actual.timestamp
        expected.id = actual.id

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseNoMimo() {
        parse("noMimo.xml", "noMimo.json")
    }

    @Test
    fun parseMimoBcsAll() {
        parse("mimoBcsAll.xml", "mimoBcsAll.json")
    }

    @Test
    fun parseMimoMultiBcs() {
        parse("mimoMultiBcs.xml", "mimoMultiBcs.json")
    }

    @Test
    fun parseMimoMultiBcsAbove9() {
        parse("mimoMultiBcsAbove9.xml", "mimoMultiBcsAbove9.json")
    }

    @Test
    fun parseFullCarrierPolicy() {
        parse("fullCarrierPolicy.xml", "fullCarrierPolicy.json")
    }

    @Test
    fun parseLteCapPrune() {
        parse("lteCapPrune.txt", "lteCapPrune.json")
    }

    @Test
    fun parseLteCapPruneWithSpaces() {
        parse("lteCapPruneWithSpaces.txt", "lteCapPruneWithSpaces.json")
    }
}
