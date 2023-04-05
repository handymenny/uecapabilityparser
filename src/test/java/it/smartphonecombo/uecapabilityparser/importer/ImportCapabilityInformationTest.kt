package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsStream
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.util.Output
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test

internal class ImportCapabilityInformationTest {

    @Test
    fun ueCapEutraCombinationAdd() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream("/newEngine/input/json/ueCapEutraCombinationAdd.json")!!,
            )

        // LTE Category
        assertEquals(18, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraCombinationAdd.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    2,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    4,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    5,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    8,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    12,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    13,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    17,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    32,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    38,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    40,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    66,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(3),
                BandNrDetails(5),
                BandNrDetails(7),
                BandNrDetails(8),
                BandNrDetails(20),
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(40),
                BandNrDetails(41),
                BandNrDetails(66),
                BandNrDetails(77),
                BandNrDetails(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapEutraCombinationReduced() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream("/newEngine/input/json/ueCapEutraCombinationReduced.json")!!,
            )

        // LTE Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraCombinationReduced.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    2,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    4,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    5,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    12,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    13,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    26,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    46,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    48,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    66,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands = listOf(BandNrDetails(260), BandNrDetails(261))
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapEutra1024qam() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream("/newEngine/input/json/ueCapEutra1024qam.json")!!
            )

        // LTE Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutra1024qam.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    8,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    38,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    40,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(3),
                BandNrDetails(7),
                BandNrDetails(28),
                BandNrDetails(78)
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapEutraCombinationReduced1024qam() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapEutraCombinationReduced1024qam.json",
                )!!,
            )

        // LTE Category
        assertEquals(22, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraCombinationReduced1024qam.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    2,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    4,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    5,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    8,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    12,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    13,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    14,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    17,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    18,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    19,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    25,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    26,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    29,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM64
                ),
                ComponentLte(
                    30,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    32,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM64
                ),
                ComponentLte(
                    34,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    38,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    39,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    40,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    42,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    46,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM64
                ),
                ComponentLte(
                    48,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    66,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
                ComponentLte(
                    71,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM1024,
                    Modulation.QAM256
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(2),
                BandNrDetails(3),
                BandNrDetails(5),
                BandNrDetails(7),
                BandNrDetails(8),
                BandNrDetails(12),
                BandNrDetails(20),
                BandNrDetails(25),
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(40),
                BandNrDetails(41),
                BandNrDetails(66),
                BandNrDetails(71),
                BandNrDetails(77),
                BandNrDetails(78),
                BandNrDetails(79),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapEutra64qamDLMimoUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapEutra64qamDLMimoUL.json",
                )!!,
            )

        // LTE Category
        assertEquals(7, capabilities.lteCategoryDL)
        assertEquals(7, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutra64qamDLMimoUL.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(1, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(3, BwClass('A'), mimoDL = 2, mimoUL = 2),
                ComponentLte(7, BwClass('A'), mimoDL = 2, mimoUL = 2),
                ComponentLte(8, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(20, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(28, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(32, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(38, BwClass('A'), mimoDL = 2, mimoUL = 1),
                ComponentLte(40, BwClass('A'), mimoDL = 2, mimoUL = 1)
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        assertArrayEquals(emptyArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapEutra256qamDLMimoUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapEutra256qamDLMimoUL.json",
                )!!,
            )

        // LTE Category
        assertEquals(12, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutra256qamDLMimoUL.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 2,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 2,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 2,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 2,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 2,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    38,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 1,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    40,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 1,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    mimoDL = 2,
                    mimoUL = 1,
                    modDL = Modulation.QAM256,
                    modUL = Modulation.QAM64
                )
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bans in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        assertArrayEquals(emptyArray(), actualNrNsaBands.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())
    }

    @Test
    fun ueCapNrOneCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapNrOneCC.json",
                )!!,
            )

        // NR bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(20)))
                },
                BandNrDetails(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 40)))
                },
                BandNrDetails(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 20)))
                },
                BandNrDetails(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 40, 20)))
                },
                BandNrDetails(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100)))
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))
                },
                BandNrDetails(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100)))
                },
                BandNrDetails(258).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(260).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrOneCC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)
    }

    @Test
    fun ueCapNrThreeCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapNrThreeCC.json",
                )!!,
            )

        // NR bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(30).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10)))
                },
                BandNrDetails(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(40, 30, 20)))
                },
                BandNrDetails(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))

                    powerClass = 2
                },
                BandNrDetails(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20), intArrayOf(100, 40, 20))
                        )
                },
                BandNrDetails(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 30, 20)))

                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))
                },
                BandNrDetails(257).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(258).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(260).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrThreeCC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)
    }

    @Test
    fun ueCapMrdcDefaultBws() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapMrdcDefaultBws.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcDefaultBws-EN-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualEndcCsv =
            Output.toCsv(capabilities.enDcCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedEndcCsv, actualEndcCsv)

        // EUTRA
        // Category
        assertEquals(19, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // Combos
        val expectedLteCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcDefaultBws-LTE.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualLteCsv =
            Output.toCsv(capabilities.lteCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedLteCsv, actualLteCsv)

        // Bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    5,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    8,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    32,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    38,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    40,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    42,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    43,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(3),
                BandNrDetails(5),
                BandNrDetails(7),
                BandNrDetails(8),
                BandNrDetails(20),
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(41),
                BandNrDetails(77),
                BandNrDetails(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 20, 15, 10))
                        )
                },
                BandNrDetails(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                        )

                    powerClass = 2
                },
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                        )

                    powerClass = 2
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }

    @Test
    fun ueCapMrdcFR2() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapMrdcFR2.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcFR2-EN-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualEndcCsv =
            Output.toCsv(capabilities.enDcCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedEndcCsv, actualEndcCsv)

        // EUTRA
        // Category
        assertEquals(19, capabilities.lteCategoryDL)
        assertEquals(13, capabilities.lteCategoryUL)

        // Combos
        val expectedLteCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcFR2-LTE.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualLteCsv =
            Output.toCsv(capabilities.lteCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedLteCsv, actualLteCsv)

        // Bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    8,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    18,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    19,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    21,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    26,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    41,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
                ComponentLte(
                    42,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM64
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(77),
                BandNrDetails(78),
                BandNrDetails(79),
                BandNrDetails(257),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))

                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))

                    powerClass = 2
                },
                BandNrDetails(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))

                    powerClass = 2
                },
                BandNrDetails(257).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(60, intArrayOf(200, 100, 50)),
                            BwsNr(120, intArrayOf(200, 100, 50))
                        )
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }

    @Test
    fun ueCapMrdcExynos() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapMrdcExynos.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcExynos-EN-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualEndcCsv =
            Output.toCsv(capabilities.enDcCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedEndcCsv, actualEndcCsv)

        // EUTRA
        // Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // Combos
        val expectedLteCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcExynos-LTE.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualLteCsv =
            Output.toCsv(capabilities.lteCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedLteCsv, actualLteCsv)

        // Bands
        val expectedLteBands =
            listOf(
                ComponentLte(
                    1,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    3,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    7,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    20,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(
                    28,
                    BwClass('A'),
                    BwClass.NONE,
                    2,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
                ComponentLte(32, BwClass('A'), BwClass.NONE, 2, 1, Modulation.QAM256),
                ComponentLte(
                    38,
                    BwClass('A'),
                    BwClass.NONE,
                    4,
                    1,
                    Modulation.QAM256,
                    Modulation.QAM256
                ),
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(3),
                BandNrDetails(5),
                BandNrDetails(7),
                BandNrDetails(8),
                BandNrDetails(20),
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(40),
                BandNrDetails(41),
                BandNrDetails(75),
                BandNrDetails(77),
                BandNrDetails(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(75).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5), intArrayOf()),
                            BwsNr(30, intArrayOf(20, 15, 10), intArrayOf()),
                            BwsNr(60, intArrayOf(20, 15, 10), intArrayOf())
                        )
                },
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10)))

                    powerClass = 2
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }

    @Test
    fun ueCapNrDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapNrdc.json",
                )!!,
            )

        // NR
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(13).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(14).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(18).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(26).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(29).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5), intArrayOf()))
                },
                BandNrDetails(30).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(40, 30, 20, 15, 10)))
                },
                BandNrDetails(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20, 15, 10)))
                },
                BandNrDetails(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))

                    powerClass = 2
                },
                BandNrDetails(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths =
                        arrayOf(
                            BwsNr(
                                30,
                                intArrayOf(100, 80, 60, 50, 40, 30, 20, 10),
                                intArrayOf(100, 40, 30, 20, 10)
                            )
                        )
                },
                BandNrDetails(53).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(10)))
                },
                BandNrDetails(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 20, 15, 10, 5)))
                },
                BandNrDetails(70).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5), intArrayOf(15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(75).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths =
                        arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5), intArrayOf()))
                },
                BandNrDetails(76).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(5), intArrayOf()))
                },
                BandNrDetails(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))

                    powerClass = 2
                },
                BandNrDetails(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))

                    powerClass = 2
                },
                BandNrDetails(91).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(92).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(93).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(94).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(257).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(200, 100, 50)))
                },
                BandNrDetails(258).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(200, 100, 50)))
                },
                BandNrDetails(260).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrdc-NR.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // NR-DC Combos
        val expectedNrDcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrdc-NR-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualNrDcCsv =
            Output.toCsv(capabilities.nrDcCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrDcCsv, actualNrDcCsv)
    }
}
