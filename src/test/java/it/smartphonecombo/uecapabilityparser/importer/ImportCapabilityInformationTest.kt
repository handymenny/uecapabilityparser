package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ImportCapabilityInformationTest {
    private val path = "src/test/resources/capabilityInformation/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = ImportCapabilityInformation.parse(File(filePath).readBytes())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        // Override dynamic properties
        expected.parserVersion = actual.parserVersion
        expected.timestamp = actual.timestamp
        expected.id = actual.id

        assertEquals(expected, actual)
    }

    @Test
    fun ueCapEutraCombinationAdd() {
        parse("ueCapEutraCombinationAdd.json", "ueCapEutraCombinationAdd.json")
    }

    @Test
    fun ueCapEutraCombinationReduced() {
        parse("ueCapEutraCombinationReduced.json", "ueCapEutraCombinationReduced.json")
    }

    @Test
    fun ueCapEutra1024qam() {
        parse("ueCapEutra1024qam.json", "ueCapEutra1024qam.json")
    }

    @Test
    fun ueCapEutraCombinationReduced1024qam() {
        parse(
            "ueCapEutraCombinationReduced1024qam.json",
            "ueCapEutraCombinationReduced1024qam.json"
        )
    }

    @Test
    fun ueCapEutra64qamDLMimoUL() {
        parse("ueCapEutra64qamDLMimoUL.json", "ueCapEutra64qamDLMimoUL.json")
    }

    @Test
    fun ueCapEutra256qamDLMimoUL() {
        parse("ueCapEutra256qamDLMimoUL.json", "ueCapEutra256qamDLMimoUL.json")
    }

    @Test
    fun ueCapEutraCombinationReducedMimoPerCC() {
        parse(
            "ueCapEutraCombinationReducedMimoPerCC.json",
            "ueCapEutraCombinationReducedMimoPerCC.json"
        )
    }

    @Test
    fun ueCapEutraOmitEnDc() {
        parse("ueCapEutraOmitEnDc.json", "ueCapEutraOmitEnDc.json")
    }

    @Test
    fun ueCapEutraRequestDiffFallback() {
        parse("ueCapEutraRequestDiffFallback.json", "ueCapEutraRequestDiffFallback.json")
    }

    @Test
    fun ueCapNrOneCC() {
        parse("ueCapNrOneCC.json", "ueCapNrOneCC.json")
    }

    @Test
    fun ueCapNrThreeCC() {
        parse("ueCapNrThreeCC.json", "ueCapNrThreeCC.json")
    }

    @Test
    fun ueCapMrdcDefaultBws() {
        parse("ueCapMrdcDefaultBws.json", "ueCapMrdcDefaultBws.json")
    }

    @Test
    fun ueCapMrdcFR2() {
        parse("ueCapMrdcFR2.json", "ueCapMrdcFR2.json")
    }

    @Test
    fun ueCapMrdcExynos() {
        parse("ueCapMrdcExynos.json", "ueCapMrdcExynos.json")
    }

    @Test
    fun ueCapMrdcIntraEnDc() {
        parse("ueCapMrdcIntraEnDc.json", "ueCapMrdcIntraEnDc.json")
    }

    @Test
    fun ueCapMrdcIntraEnDcV1590() {
        parse("ueCapMrdcIntraEnDcV1590.json", "ueCapMrdcIntraEnDcV1590.json")
    }

    @Test
    fun ueCapEutraNrOnlyIntraBcsAll() {
        parse("ueCapEutraNrOnlyIntraBcsAll.json", "ueCapEutraNrOnlyIntraBcsAll.json")
    }

    @Test
    fun ueCapNrDc() {
        parse("ueCapNrDc.json", "ueCapNrDc.json")
    }

    @Test
    fun ueCapNrSUL() {
        parse("ueCapNrSUL.json", "ueCapNrSUL.json")
    }

    @Test
    fun ueCapNrOmitEnDc() {
        parse("ueCapNrOmitEnDc.json", "ueCapNrOmitEnDc.json")
    }

    @Test
    fun ueCapNrBwRequested() {
        parse("ueCapNrBwRequested.json", "ueCapNrBwRequested.json")
    }

    @Test
    fun ueCapNrUnfiltered() {
        parse("ueCapNrUnfiltered.json", "ueCapNrUnfiltered.json")
    }
}
