package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Import0xB826Test {
    private val path = "src/test/resources/0xB826/"

    private fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val actual = Import0xB826.parse(File(filePath).readBytes())
        val expected =
            Json.decodeFromString<Capabilities>(File("$path/oracle/$oracleFilename").readText())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun parseV2NSA() {
        // Modulation UL invalid
        parse("0xB826-v2-NSA.bin", "0xB826-v2-NSA.json")
    }

    @Test
    fun parseV3NSA() {
        parse("0xB826-v3-NSA.bin", "0xB826-v3-NSA.json")
    }

    @Test
    fun parseV3NSAmmWave() {
        parse("0xB826-v3-NSA-mmWave.bin", "0xB826-v3-NSA-mmWave.json")
    }

    @Test
    fun parseV3NSA64qamUL() {
        // LTE UL 256qam and 64qam
        parse("0xB826-v3-NSA-64qamUL.bin", "0xB826-v3-NSA-64qamUL.json")
    }

    @Test
    fun parseV3SA() {
        parse("0xB826-v3-SA.bin", "0xB826-v3-SA.json")
    }

    @Test
    fun parseV4NSA() {
        parse("0xB826-v4-NSA.bin", "0xB826-v4-NSA.json")
    }

    @Test
    fun parseV4NSAmmWave() {
        parse("0xB826-v4-NSA-mmWave.bin", "0xB826-v4-NSA-mmWave.json")
    }

    @Test
    fun parseV4SA() {
        parse("0xB826-v4-SA.bin", "0xB826-v4-SA.json")
    }

    @Test
    fun parseV6NSA() {
        parse("0xB826-v6-NSA.bin", "0xB826-v6-NSA.json")
    }

    @Test
    fun parseV6NSAmmWave() {
        parse("0xB826-v6-NSA-mmWave.bin", "0xB826-v6-NSA-mmWave.json")
    }

    @Test
    fun parseV6SA() {
        parse("0xB826-v6-SA.bin", "0xB826-v6-SA.json")
    }

    @Test
    fun parseV7NSA() {
        parse("0xB826-v7-NSA.bin", "0xB826-v7-NSA.json")
    }

    @Test
    fun parseV7NSA2() {
        parse("0xB826-v7-NSA2.bin", "0xB826-v7-NSA2.json")
    }

    @Test
    fun parseV7NSAmmWave() {
        parse("0xB826-v7-NSA-mmWave.bin", "0xB826-v7-NSA-mmWave.json")
    }

    @Test
    fun parseV7NSAmmWave2() {
        parse("0xB826-v7-NSA-mmWave2.bin", "0xB826-v7-NSA-mmWave2.json")
    }

    @Test
    fun parseV7NSAmmWave3() {
        parse("0xB826-v7-SA2.bin", "0xB826-v7-SA2.json")
    }

    @Test
    fun parseV7NSA64qamUL() {
        // LTE UL 64qam
        parse("0xB826-v7-NSA-64qamUL.bin", "0xB826-v7-NSA-64qamUL.json")
    }

    @Test
    fun parseV8NSA() {
        parse("0xB826-v8-NSA.bin", "0xB826-v8-NSA.json")
    }

    @Test
    fun parseV8NSA2() {
        parse("0xB826-v8-NSA2.bin", "0xB826-v8-NSA2.json")
    }

    @Test
    fun parseV8NSAmmWave() {
        parse("0xB826-v8-NSA-mmWave.bin", "0xB826-v8-NSA-mmWave.json")
    }

    @Test
    fun parseV8NSAmmWave2() {
        parse("0xB826-v8-NSA-mmWave2.bin", "0xB826-v8-NSA-mmWave2.json")
    }

    @Test
    fun parseV8SA() {
        parse("0xB826-v8-SA.bin", "0xB826-v8-SA.json")
    }

    @Test
    fun parseV8SA2() {
        parse("0xB826-v8-SA2.bin", "0xB826-v8-SA2.json")
    }

    @Test
    fun parseV9NSA() {
        parse("0xB826-v9-NSA-header.bin", "0xB826-v9-NSA-header.json")
    }

    @Test
    fun parseV9NSA2() {
        parse("0xB826-v9-NSA2.bin", "0xB826-v9-NSA2.json")
    }

    @Test
    fun parseV9NSAmmWave() {
        parse("0xB826-v9-NSA-mmWave.bin", "0xB826-v9-NSA-mmWave.json")
    }

    @Test
    fun parseV9NSAmmWave2() {
        parse("0xB826-v9-NSA-mmWave2.bin", "0xB826-v9-NSA-mmWave2.json")
    }

    @Test
    fun parseV9SA() {
        parse("0xB826-v9-SA.bin", "0xB826-v9-SA.json")
    }

    @Test
    fun parseV9SA2() {
        parse("0xB826-v9-SA2.bin", "0xB826-v9-SA2.json")
    }

    @Test
    fun parseV9NrDc() {
        parse("0xB826-v9-NRDC.bin", "0xB826-v9-NRDC.json")
    }

    @Test
    fun parseV10NSA() {
        parse("0xB826-v10-NSA.bin", "0xB826-v10-NSA.json")
    }

    @Test
    fun parseV10NSA2() {
        parse("0xB826-v10-NSA2.bin", "0xB826-v10-NSA2.json")
    }

    @Test
    fun parseV10NSAmmWave() {
        parse("0xB826-v10-NSAmmWave.bin", "0xB826-v10-NSAmmWave.json")
    }

    @Test
    fun parseV10NrDc() {
        parse("0xB826-v10-NRDC.bin", "0xB826-v10-NRDC.json")
    }

    @Test
    fun parseV13NSA() {
        parse("0xB826-v13-NSA.bin", "0xB826-v13-NSA.json")
    }

    @Test
    fun parseV13NSA2() {
        parse("0xB826-v13-NSA2.bin", "0xB826-v13-NSA2.json")
    }

    @Test
    fun parseV13SA() {
        parse("0xB826-v13-SA.bin", "0xB826-v13-SA.json")
    }

    @Test
    fun parseV13SA2() {
        parse("0xB826-v13-SA2.bin", "0xB826-v13-SA2.json")
    }

    @Test
    fun parseV13NRDC() {
        parse("0xB826-v13-NRDC.bin", "0xB826-v13-NRDC.json")
    }

    @Test
    fun parseV14NSA() {
        parse("0xB826-v14-NSA.bin", "0xB826-v14-NSA.json")
    }

    @Test
    fun parseV14SA() {
        parse("0xB826-v14-SA.bin", "0xB826-v14-SA.json")
    }
}
