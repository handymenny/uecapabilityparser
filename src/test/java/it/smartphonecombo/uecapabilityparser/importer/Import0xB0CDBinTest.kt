package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Import0xB0CDBinTest {
    private val path = "src/test/resources/0xB0CDBin/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = Import0xB0CDBin.parse(File(filePath).readBytes())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        Assertions.assertEquals(expected, actual)
    }

    // Unknown version below v24
    @Test
    fun parsePreV24() {
        parse("0xB0CD-preV24-headless.bin", "0xB0CD-preV24-headless.json")
    }

    @Test
    fun parseV24() {
        parse("0xB0CD-v24-headless.bin", "0xB0CD-v24-headless.json")
    }

    @Test
    fun parseV32() {
        parse("0xB0CD-v32.bin", "0xB0CD-v32.json")
    }

    @Test
    fun parseV40() {
        parse("0xB0CD-v40.bin", "0xB0CD-v40.json")
    }

    @Test
    fun parseV403ULCA() {
        parse("0xB0CD-v40-3ULCA.bin", "0xB0CD-v40-3ULCA.json")
    }

    @Test
    fun parseV41() {
        parse("0xB0CD-v41.bin", "0xB0CD-v41.json")
    }

    @Test
    fun parseV41qam64UL() {
        parse("0xB0CD-v41-64qamUL.bin", "0xB0CD-v41-64qamUL.json")
    }
}
