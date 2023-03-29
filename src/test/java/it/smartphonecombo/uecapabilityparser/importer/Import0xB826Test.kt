package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.util.Utility
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Import0xB826Test {
    private val path = "src/test/resources/0xB826/"
    private fun parse(
        srcFilename: String,
        version: Int,
        logSize: Int,
        numCombos: Int,
        totalCombos: Int?,
        index: Int?,
        source: String?,
        endc: Boolean,
        oracleFilename: String?,
        nrdc: Boolean = false,
    ) {
        val filePath = "$path/input/$srcFilename"
        val comboList = Import0xB826.parse(File(filePath).inputStream())
        Assertions.assertEquals(version, comboList.getMetadata("version"))
        Assertions.assertEquals(logSize, comboList.getMetadata("logSize"))
        Assertions.assertEquals(numCombos, comboList.getMetadata("numCombos"))
        Assertions.assertEquals(totalCombos, comboList.getMetadata("totalCombos"))
        Assertions.assertEquals(index, comboList.getMetadata("index"))
        Assertions.assertEquals(source, comboList.getMetadata("source"))
        val comboNR: List<ICombo>?
        if (endc) {
            comboNR = comboList.enDcCombos
            Assertions.assertNull(comboList.nrCombos)
            Assertions.assertNull(comboList.nrDcCombos)
        } else if (nrdc) {
            comboNR = comboList.nrDcCombos
            Assertions.assertNull(comboList.enDcCombos)
            Assertions.assertNull(comboList.nrCombos)
        } else {
            comboNR = comboList.nrCombos
            Assertions.assertNull(comboList.enDcCombos)
            Assertions.assertNull(comboList.nrDcCombos)
        }
        Assertions.assertNotNull(comboNR)
        Assertions.assertEquals(numCombos, comboNR!!.size)

        val actualCsv = Utility.toCsv(comboNR).lines().dropLastWhile { it.isBlank() }
        val oraclePath = "$path/oracle/$oracleFilename"

        val expectedCsv =
            File(oraclePath).bufferedReader().readLines().dropLastWhile { it.isBlank() }
        Assertions.assertLinesMatch(expectedCsv, actualCsv)
    }

    @Test
    fun parseV2NSA() {
        parse("0xB826-v2-NSA.bin", 2, 356, 10, null, null, null, true, "0xB826-v2-NSA.csv")
    }

    @Test
    fun parseV3NSA() {
        parse("0xB826-v3-NSA.bin", 3, 9391, 265, null, null, null, true, "0xB826-v3-NSA.csv")
    }

    @Test
    fun parseV3NSAmmWave() {
        parse(
            "0xB826-v3-NSA-mmWave.bin",
            3,
            4076,
            100,
            null,
            null,
            null,
            true,
            "0xB826-v3-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV3SA() {
        parse("0xB826-v3-SA.bin", 3, 50, 4, null, null, null, false, "0xB826-v3-SA.csv")
    }

    @Test
    fun parseV4NSA() {
        parse("0xB826-v4-NSA.bin", 4, 3232, 81, 181, 100, "RF", true, "0xB826-v4-NSA.csv")
    }

    @Test
    fun parseV4NSAmmWave() {
        parse(
            "0xB826-v4-NSA-mmWave.bin",
            4,
            4431,
            100,
            478,
            0,
            "RF",
            true,
            "0xB826-v4-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV4SA() {
        parse("0xB826-v4-SA.bin", 4, 44, 3, 3, 0, "RF", false, "0xB826-v4-SA.csv")
    }

    @Test
    fun parseV6NSA() {
        parse("0xB826-v6-NSA.bin", 6, 3581, 100, 475, 100, "RF", true, "0xB826-v6-NSA.csv")
    }

    @Test
    fun parseV6NSAmmWave() {
        parse(
            "0xB826-v6-NSA-mmWave.bin",
            6,
            4391,
            100,
            710,
            0,
            "RF",
            true,
            "0xB826-v6-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV6SA() {
        parse("0xB826-v6-SA.bin", 6, 83, 6, 6, 0, "RF", false, "0xB826-v6-SA.csv")
    }

    @Test
    fun parseV7NSA() {
        parse("0xB826-v7-NSA.bin", 7, 4311, 100, 116, 0, "RF", true, "0xB826-v7-NSA.csv")
    }

    @Test
    fun parseV7NSA2() {
        parse("0xB826-v7-NSA2.bin", 7, 3215, 96, 96, 0, "RF", true, "0xB826-v7-NSA2.csv")
    }

    @Test
    fun parseV7NSAmmWave() {
        parse(
            "0xB826-v7-NSA-mmWave.bin",
            7,
            3849,
            82,
            82,
            0,
            "RF",
            true,
            "0xB826-v7-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV7NSAmmWave2() {
        parse(
            "0xB826-v7-NSA-mmWave2.bin",
            7,
            5351,
            100,
            1542,
            200,
            "RF",
            true,
            "0xB826-v7-NSA-mmWave2.csv"
        )
    }

    @Test
    fun parseV7NSAmmWave3() {
        parse("0xB826-v7-SA2.bin", 7, 249, 17, 17, 0, "RF", false, "0xB826-v7-SA2.csv")
    }

    @Test
    fun parseV8NSA() {
        parse("0xB826-v8-NSA.bin", 8, 2671, 76, 276, 200, "RF", true, "0xB826-v8-NSA.csv")
    }

    @Test
    fun parseV8NSA2() {
        parse("0xB826-v8-NSA2.bin", 8, 3631, 100, 472, 200, "RF", true, "0xB826-v8-NSA2.csv")
    }

    @Test
    fun parseV8NSAmmWave() {
        parse(
            "0xB826-v8-NSA-mmWave.bin",
            8,
            3687,
            100,
            207,
            0,
            "RF",
            true,
            "0xB826-v8-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV8NSAmmWave2() {
        parse(
            "0xB826-v8-NSA-mmWave2.bin",
            8,
            3551,
            100,
            3894,
            1300,
            "RF",
            true,
            "0xB826-v8-NSA-mmWave2.csv"
        )
    }

    @Test
    fun parseV8SA() {
        parse("0xB826-v8-SA.bin", 8, 155, 8, 8, 0, "RF", false, "0xB826-v8-SA.csv")
    }

    @Test
    fun parseV8SA2() {
        parse("0xB826-v8-SA2.bin", 8, 275, 16, 16, 0, "RF", false, "0xB826-v8-SA2.csv")
    }

    @Test
    fun parseV9NSA() {
        parse(
            "0xB826-v9-NSA-header.bin",
            9,
            4515,
            100,
            406,
            0,
            "RF",
            true,
            "0xB826-v9-NSA-header.csv"
        )
    }

    @Test
    fun parseV9NSA2() {
        parse("0xB826-v9-NSA2.bin", 9, 4703, 100, 541, 100, "RF", true, "0xB826-v9-NSA2.csv")
    }

    @Test
    fun parseV9NSAmmWave() {
        parse(
            "0xB826-v9-NSA-mmWave.bin",
            9,
            4751,
            100,
            2126,
            300,
            "RF",
            true,
            "0xB826-v9-NSA-mmWave.csv"
        )
    }

    @Test
    fun parseV9NSAmmWave2() {
        parse(
            "0xB826-v9-NSA-mmWave2.bin",
            9,
            5383,
            100,
            2126,
            100,
            "RF",
            true,
            "0xB826-v9-NSA-mmWave2.csv"
        )
    }

    @Test
    fun parseV9SA() {
        parse("0xB826-v9-SA.bin", 9, 2967, 100, 222, 100, "RF", false, "0xB826-v9-SA.csv")
    }

    @Test
    fun parseV9SA2() {
        parse("0xB826-v9-SA2.bin", 9, 2511, 92, 92, 0, "RF", false, "0xB826-v9-SA2.csv")
    }

    @Test
    fun parseV9NrDc() {
        parse("0xB826-v9-NRDC.bin", 9, 3264, 97, 97, 0, "RF", false, "0xB826-v9-NRDC.csv", true)
    }

    @Test
    fun parseV10NSA() {
        parse("0xB826-v10-NSA.bin", 10, 4103, 100, 742, 600, "RF", true, "0xB826-v10-NSA.csv")
    }

    @Test
    fun parseV10NSA2() {
        parse("0xB826-v10-NSA2.bin", 10, 3298, 91, 91, 0, "RF", true, "0xB826-v10-NSA2.csv")
    }
    @Test
    fun parseV10NSAmmWave() {
        parse(
            "0xB826-v10-NSAmmWave.bin",
            10,
            4903,
            100,
            289,
            0,
            "RF",
            true,
            "0xB826-v10-NSAmmWave.csv"
        )
    }

    @Test
    fun parseV10NrDc() {
        parse("0xB826-v10-NRDC.bin", 10, 359, 12, 12, 0, "RF", false, "0xB826-v10-NRDC.csv", true)
    }

    @Test
    fun parseV13NSA() {
        parse("0xB826-v13-NSA.bin", 13, 5323, 100, 884, 0, "RF_ENDC", true, "0xB826-v13-NSA.csv")
    }

    @Test
    fun parseV13NSA2() {
        parse(
            "0xB826-v13-NSA2.bin",
            13,
            4963,
            100,
            1721,
            600,
            "RF_ENDC",
            true,
            "0xB826-v13-NSA2.csv"
        )
    }

    @Test
    fun parseV13SA() {
        parse("0xB826-v13-SA.bin", 13, 4491, 100, 717, 0, "RF_NRCA", false, "0xB826-v13-SA.csv")
    }

    @Test
    fun parseV13SA2() {
        parse("0xB826-v13-SA2.bin", 13, 3531, 100, 518, 300, "RF_NRCA", false, "0xB826-v13-SA2.csv")
    }

    @Test
    fun parseV13NRDC() {
        parse(
            "0xB826-v13-NRDC.bin",
            13,
            3805,
            99,
            99,
            0,
            "RF_NRDC",
            false,
            "0xB826-v13-NRDC.csv",
            true
        )
    }

    @Test
    fun parseV14NSA() {
        parse("0xB826-v14-NSA.bin", 14, 3889, 65, 4965, 4900, "RF_ENDC", true, "0xB826-v14-NSA.csv")
    }

    @Test
    fun parseV14SA() {
        parse("0xB826-v14-SA.bin", 14, 4963, 100, 405, 300, "RF_NRCA", false, "0xB826-v14-SA.csv")
    }
}
