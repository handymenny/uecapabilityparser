package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsStream
import it.smartphonecombo.uecapabilityparser.model.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.toMimo
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    2,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    17,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                )
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    46,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    48,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    2,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    14,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    17,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    18,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    19,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    25,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    29,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    30,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    34,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    39,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    46,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    48,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    71,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024,
                    modUL = ModulationOrder.QAM256
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
                ComponentLte(
                    1,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    3,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    7,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    32,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
                ),
                ComponentLte(
                    40,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64,
                    modUL = ModulationOrder.QAM16
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
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    3,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    7,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    40,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    41,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
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
    fun ueCapEutraCombinationReducedMimoPerCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapEutraCombinationReducedMimoPerCC.json",
                )!!,
            )

        // LTE Category
        assertEquals(19, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraCombinationReducedMimoPerCC.csv")!!
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    32,
                    mimoDL = 4.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                )
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                BandNrDetails(1),
                BandNrDetails(3),
                BandNrDetails(7),
                BandNrDetails(8),
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(40),
                BandNrDetails(41),
                BandNrDetails(78)
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        val expectedNrSaBands =
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
                BandNrDetails(78)
            )
        assertArrayEquals(expectedNrSaBands.toTypedArray(), actualNrSaBands.toTypedArray())
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(20)))
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 40)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 20)))
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 40, 20)))
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100)))
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100)))
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(30).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(40, 30, 20)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))

                    powerClass = 2
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20), intArrayOf(100, 40, 20))
                        )
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 30, 20)))

                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100)))
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    32,
                    mimoDL = 4.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    43,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 20, 15, 10))
                        )
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                        )

                    powerClass = 2
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
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
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    18,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    19,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    21,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    41,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    42,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))

                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))

                    powerClass = 2
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = false
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))

                    powerClass = 2
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM64
                    modUL = ModulationOrder.QAM64
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(32, mimoDL = 2.toMimo(), modDL = ModulationOrder.QAM256),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5), intArrayOf()),
                            BwsNr(30, intArrayOf(20, 15, 10), intArrayOf()),
                            BwsNr(60, intArrayOf(20, 15, 10), intArrayOf())
                        )
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
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
    fun ueCapMrdcIntraEnDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapMrdcIntraEnDc.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcIntraEnDc-EN-DC.csv")!!
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
            getResourceAsStream("/newEngine/oracle/ueCapMrdcIntraEnDc-LTE.csv")!!
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    34,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    39,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                ),
                ComponentLte(
                    43,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM64
                )
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
                BandNrDetails(28),
                BandNrDetails(38),
                BandNrDetails(40),
                BandNrDetails(41),
                BandNrDetails(77),
                BandNrDetails(78),
                BandNrDetails(79),
                BandNrDetails(80),
                BandNrDetails(84)
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(20, 15, 10)))
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 20)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)),
                        )
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)),
                        )
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)),
                        )
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40)),
                        )
                },
                BandNrDetails(80).apply {
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)),
                        )
                },
                BandNrDetails(84).apply {
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(
                            BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)),
                        )
                }
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }

    @Test
    fun ueCapMrdcIntraEnDcV1590() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapMrdcIntraEnDcV1590.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapMrdcIntraEnDcV1590-EN-DC.csv")!!
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
            getResourceAsStream("/newEngine/oracle/ueCapMrdcIntraEnDcV1590-LTE.csv")!!
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
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                ),
                ComponentLte(32, mimoDL = 2.toMimo(), modDL = ModulationOrder.QAM256),
                ComponentLte(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256,
                    modUL = ModulationOrder.QAM256
                )
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
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
                BandNrDetails(75),
                BandNrDetails(77),
                BandNrDetails(78)
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(40, 30, 20, 15, 10)))
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20, 15, 10)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5), intArrayOf()))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths =
                        arrayOf(
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                }
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }

    @Test
    fun ueCapEutraNrOnlyIntraBcsAll() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapEutraNrOnlyIntraBcsAll.json",
                )!!,
            )

        // MRDC
        // ENDC combos
        val expectedEndcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraNrOnlyIntraBcsAll-EN-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }

        val actualEndcCsv =
            Output.toCsv(capabilities.enDcCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedEndcCsv, actualEndcCsv)
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5)))
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(13).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(14).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(18).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(15, 10, 5)))
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(26).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(29).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5), intArrayOf()))
                },
                BandNrDetails(30).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(40, 30, 20, 15, 10)))
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20, 15, 10)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))

                    powerClass = 2
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
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
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(10)))
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(40, 30, 20, 15, 10, 5)))
                },
                BandNrDetails(70).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths =
                        arrayOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5), intArrayOf(15, 10, 5)))
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5), intArrayOf()))
                },
                BandNrDetails(76).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(5), intArrayOf()))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 2
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths =
                        arrayOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))

                    powerClass = 2
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))

                    powerClass = 2
                },
                BandNrDetails(91).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(92).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(93).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(10, 5)))
                },
                BandNrDetails(94).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    rateMatchingLteCrs = true
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(200, 100, 50)))
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(200, 100, 50)))
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM64
                    bandwidths = arrayOf(BwsNr(120, intArrayOf(100, 50)))
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM64
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

    @Test
    fun ueCapNrSUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                    "/newEngine/input/json/ueCapNrSUL.json",
                )!!,
            )

        // NR
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(20)))
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20)))
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 10)))
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256
                    modUL = ModulationOrder.QAM256
                    powerClass = 2
                    bandwidths = arrayOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))
                },
                BandNrDetails(80).apply {
                    modDL = ModulationOrder.NONE
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(), intArrayOf(30, 25, 20, 15, 10, 5)))
                },
                BandNrDetails(84).apply {
                    modDL = ModulationOrder.NONE
                    modUL = ModulationOrder.QAM256
                    bandwidths = arrayOf(BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)))
                }
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrSUL-NR.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }

        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)
    }
}
