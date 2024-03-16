package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportQctModemCapTest {
    private val path = "src/test/resources/qctModemCap/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportQctModemCap.parse(File(filePath).toInputSource())

        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseEfsQuery() {
        parse("efs-query.txt", "efs-query.json")
    }

    @Test
    fun parseFtmQuery() {
        parse("ftm-query.txt", "ftm-query.json")
    }

    @Test
    fun parseLowercase() {
        parse("lowercase.txt", "lowercase.json")
    }

    @Test
    fun parseTwoRow() {
        parse("two-row.txt", "two-row.json")
    }

    @Test
    fun parseMulti() {
        parse("multi.txt", "multi.json")
    }
}
