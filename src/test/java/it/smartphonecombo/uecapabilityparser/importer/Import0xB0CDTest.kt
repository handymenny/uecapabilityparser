package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Import0xB0CDTest {
    private val path = "src/test/resources/0xB0CD/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = Import0xB0CD.parse(File(filePath).toInputSource())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parse0xB0CDv32() {
        parse("v32.txt", "v32.json")
    }

    @Test
    fun parse0xB0CDv40() {
        parse("v40.txt", "v40.json")
    }

    @Test
    fun parse0xB0CDv41() {
        parse("v41.txt", "v41.json")
    }

    @Test
    fun parse0xB0CDv32Multi() {
        parse("v32multi.txt", "v32multi.json")
    }

    @Test
    fun parse0xB0CDv40Multi() {
        parse("v40multi.txt", "v40multi.json")
    }

    @Test
    fun parse0xB0CDv41Multi() {
        parse("v41multi.txt", "v41multi.json")
    }

    @Test
    fun parse0xB0CDv40MixedMimo() {
        parse("v40mixedMimo.txt", "v40mixedMimo.json")
    }

    @Test
    fun parse0xB0CDv41MixedMimo() {
        parse("v41mixedMimo.txt", "v41mixedMimo.json")
    }
}
