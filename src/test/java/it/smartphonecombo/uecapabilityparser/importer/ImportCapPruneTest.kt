package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportCapPruneTest {
    private val path = "src/test/resources/nrCapPrune/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportNrCapPrune.parse(File(filePath).readBytes())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())
        // Override dynamic properties
        expected.parserVersion = actual.parserVersion
        expected.timestamp = actual.timestamp
        expected.id = actual.id

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseNoMimoFR1() {
        parse("noMimoFR1.txt", "noMimoFR1.json")
    }

    @Test
    fun parseNoMimoFR2() {
        parse("noMimoFR2.txt", "noMimoFR2.json")
    }

    @Test
    fun parseMimoFR1() {
        parse("mimoFR1.txt", "mimoFR1.json")
    }

    @Test
    fun parseMimoFR2() {
        parse("mimoFR2.txt", "mimoFR2.json")
    }

    @Test
    fun parseMixedMimoFR1() {
        parse("mixedMimoFR1.txt", "mixedMimoFR1.json")
    }
}
