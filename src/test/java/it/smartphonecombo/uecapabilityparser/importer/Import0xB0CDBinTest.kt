package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.util.Output
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Import0xB0CDBinTest {
    private val path = "src/test/resources/0xB0CDBin/"

    private fun parse(
        srcFilename: String,
        version: Int,
        logSize: Int,
        numCombos: Int,
        oracleFilename: String?,
    ) {
        val filePath = "$path/input/$srcFilename"
        val comboList = Import0xB0CDBin.parse(File(filePath).readBytes())
        Assertions.assertEquals(version, comboList.getIntMetadata("version"))
        Assertions.assertEquals(logSize, comboList.getIntMetadata("logSize"))
        Assertions.assertEquals(numCombos, comboList.getIntMetadata("numCombos"))
        val lteCombos = comboList.lteCombos
        Assertions.assertNotNull(lteCombos)
        Assertions.assertEquals(numCombos, lteCombos.size)

        val actualCsv = Output.toCsv(lteCombos).lines().dropLastWhile { it.isBlank() }
        val oraclePath = "$path/oracle/$oracleFilename"

        val expectedCsv =
            File(oraclePath).bufferedReader().readLines().dropLastWhile { it.isBlank() }
        Assertions.assertLinesMatch(expectedCsv, actualCsv)
    }

    // Unknown version below v24
    @Test
    fun parsePreV24() {
        parse("0xB0CD-preV24-headless.bin", 0, 263, 10, "0xB0CD-preV24-headless.csv")
    }

    @Test
    fun parseV24() {
        parse("0xB0CD-v24-headless.bin", 24, 1974, 82, "0xB0CD-v24-headless.csv")
    }

    @Test
    fun parseV32() {
        parse("0xB0CD-v32.bin", 32, 3615, 46, "0xB0CD-v32.csv")
    }

    @Test
    fun parseV40() {
        parse("0xB0CD-v40.bin", 40, 4215, 100, "0xB0CD-v40.csv")
    }

    @Test
    fun parseV403ULCA() {
        parse("0xB0CD-v40-3ULCA.bin", 40, 4215, 100, "0xB0CD-v40-3ULCA.csv")
    }

    @Test
    fun parseV41() {
        parse("0xB0CD-v41.bin", 41, 2704, 100, "0xB0CD-v41.csv")
    }

    @Test
    fun parseV41qam64UL() {
        parse("0xB0CD-v41-64qamUL.bin", 41, 2277, 100, "0xB0CD-v41-64qamUL.csv")
    }
}
