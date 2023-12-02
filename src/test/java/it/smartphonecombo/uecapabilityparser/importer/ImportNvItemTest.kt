package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportNvItemTest {
    private val path = "src/test/resources/nvitem/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportNvItem.parse(File(filePath).readBytes())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseNvItem137() {
        parse("28874_137.bin", "28874_137.json")
    }

    @Test
    fun parseNvItem137201() {
        parse("28874_137_201.bin", "28874_137_201.json")
    }

    @Test
    fun parseNvItem201() {
        parse("28874_201.bin", "28874_201.json")
    }

    @Test
    fun parseNvItem333() {
        parse("28874_333.bin", "28874_333.json")
    }

    @Test
    fun parseNvItemZlib() {
        parse("28874_zlib.bin", "28874_zlib.json")
    }
}
