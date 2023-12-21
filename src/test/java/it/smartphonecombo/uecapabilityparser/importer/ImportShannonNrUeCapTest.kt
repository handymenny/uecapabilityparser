package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.io.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportShannonNrUeCapTest {
    private val path = "src/test/resources/shannon/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportShannonNrUeCap.parse(File(filePath).toInputSource())
        val expected =
            Json.decodeFromString<Capabilities>(
                File("$path/oracleForImport/$oracleFilename").readText()
            )

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseEmpty() {
        parse("empty.binarypb", "empty.json")
    }

    @Test
    fun parseSub6() {
        parse("sub6.binarypb", "sub6.json")
    }

    @Test
    fun parseSub62() {
        parse("sub6_2.binarypb", "sub6_2.json")
    }

    @Test
    fun parseMmWave() {
        parse("mmWave.binarypb", "mmWave.json")
    }

    @Test
    fun parseMmWave2() {
        parse("mmWave_2.binarypb", "mmWave_2.json")
    }

    @Test
    fun parseMmWaveSA() {
        parse("mmWaveSA.binarypb", "mmWaveSA.json")
    }
}
