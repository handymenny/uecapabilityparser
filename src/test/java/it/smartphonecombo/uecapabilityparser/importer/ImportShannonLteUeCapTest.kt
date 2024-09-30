package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportShannonLteUeCapTest {
    private val path = "src/test/resources/shannon/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportShannonLteUeCap.parse(File(filePath).toInputSource())
        val expected =
            Json.decodeFromString<Capabilities>(
                File("$path/oracleForImport/$oracleFilename").readText()
            )

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseLte() {
        parse("lte.binarypb", "lte.json")
    }

    @Test
    fun parseLte2() {
        parse("lte2.binarypb", "lte2.json")
    }
}
