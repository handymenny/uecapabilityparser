package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsStream
import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsText
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ImportCapabilityInformationJsonTest {

    @Test
    fun ueCapAddJson() {
        val capabilities =
            importerJson.parse(getResourceAsText("/newEngine/input/json/ueCapAdd.json")!!)

        // LTE Category
        assertEquals(18, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapAdd.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(1, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(2, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(3, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(4, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(5, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(7, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(8, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(12, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(13, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(17, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(20, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(28, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(32, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(38, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(40, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(41, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(66, 'A', '0', 4, "256qam", "64qam"),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                ComponentNr(1),
                ComponentNr(3),
                ComponentNr(5),
                ComponentNr(7),
                ComponentNr(8),
                ComponentNr(20),
                ComponentNr(28),
                ComponentNr(38),
                ComponentNr(40),
                ComponentNr(41),
                ComponentNr(66),
                ComponentNr(77),
                ComponentNr(78)
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
    }

    @Test
    fun ueCapReducedJson() {
        val capabilities =
            importerJson.parse(getResourceAsText("/newEngine/input/json/ueCapReduced.json")!!)

        // LTE Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapReduced.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(2, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(4, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(5, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(7, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(12, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(13, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(26, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(41, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(46, 'A', '0', 2, "256qam", "64qam"),
                ComponentLte(48, 'A', '0', 4, "256qam", "64qam"),
                ComponentLte(66, 'A', '0', 4, "256qam", "64qam"),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands = listOf(ComponentNr(260), ComponentNr(261))
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
    }

    @Test
    fun ueCap1024qam() {
        val capabilities =
            importerJson.parse(getResourceAsText("/newEngine/input/json/ueCap1024qam.json")!!)

        // LTE Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCap1024qam.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(1, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(3, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(7, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(8, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(20, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(38, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(40, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(41, 'A', '0', 4, "1024qam", "256qam"),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(ComponentNr(1), ComponentNr(3), ComponentNr(7), ComponentNr(28), ComponentNr(78))
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
    }

    @Test
    fun ueCapReduced1024qam() {
        val capabilities =
            importerJson.parse(
                getResourceAsText("/newEngine/input/json/ueCapReduced1024qam.json")!!
            )

        // LTE Category
        assertEquals(22, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapReduced1024qam.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(1, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(2, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(3, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(4, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(5, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(7, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(8, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(12, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(13, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(14, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(17, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(18, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(19, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(20, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(25, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(26, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(28, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(29, 'A', '0', 2, "1024qam", "64qam"),
                ComponentLte(30, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(32, 'A', '0', 2, "1024qam", "64qam"),
                ComponentLte(34, 'A', '0', 2, "1024qam", "256qam"),
                ComponentLte(38, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(39, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(40, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(41, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(42, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(46, 'A', '0', 2, "1024qam", "64qam"),
                ComponentLte(48, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(66, 'A', '0', 4, "1024qam", "256qam"),
                ComponentLte(71, 'A', '0', 2, "1024qam", "256qam")
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                ComponentNr(1),
                ComponentNr(2),
                ComponentNr(3),
                ComponentNr(5),
                ComponentNr(7),
                ComponentNr(8),
                ComponentNr(12),
                ComponentNr(20),
                ComponentNr(25),
                ComponentNr(28),
                ComponentNr(38),
                ComponentNr(40),
                ComponentNr(41),
                ComponentNr(66),
                ComponentNr(71),
                ComponentNr(77),
                ComponentNr(78),
                ComponentNr(79)
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
    }

    companion object {
        val importerJson = ImportCapabilityInformationJson()
    }
}
