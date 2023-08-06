package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsStream
import it.smartphonecombo.uecapabilityparser.UtilityForTests.toBandFilterLte
import it.smartphonecombo.uecapabilityparser.UtilityForTests.toBandFilterNr
import it.smartphonecombo.uecapabilityparser.UtilityForTests.toPowerClass
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.band.BandLteDetails
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.bandwidth.BwsNr
import it.smartphonecombo.uecapabilityparser.model.filter.BandFilterNr
import it.smartphonecombo.uecapabilityparser.model.filter.UeCapabilityFilterLte
import it.smartphonecombo.uecapabilityparser.model.filter.UeCapabilityFilterNr
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.modulation.toModulation
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
                getResourceAsStream("/newEngine/input/json/ueCapEutraCombinationAdd.json")!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    2,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    17,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        32.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutraCombinationReduced() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream("/newEngine/input/json/ueCapEutraCombinationReduced.json")!!
                    .readBytes()
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
                BandLteDetails(
                    2,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    46,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    48,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                reducedFormat = true,
                lteBands =
                    listOf(
                        66.toBandFilterLte(),
                        4.toBandFilterLte(),
                        2.toBandFilterLte(),
                        13.toBandFilterLte(),
                        5.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutra1024qam() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream("/newEngine/input/json/ueCapEutra1024qam.json")!!.readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 2.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        20.toBandFilterLte(),
                        38.toBandFilterLte(),
                        41.toBandFilterLte(),
                        1.toBandFilterLte(),
                        7.toBandFilterLte(),
                        3.toBandFilterLte(),
                        8.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutraCombinationReduced1024qam() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutraCombinationReduced1024qam.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    2,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    4,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    12,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    13,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    14,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    17,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    18,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    19,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    25,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    29,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    30,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    34,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    39,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    46,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    48,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    66,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    71,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM1024.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                reducedFormat = true,
                skipFallbackCombRequested = true,
                maxCCsDl = 6,
                maxCCsUl = 2,
                lteBands =
                    listOf(
                        3.toBandFilterLte(),
                        20.toBandFilterLte(),
                        1.toBandFilterLte(),
                        8.toBandFilterLte(),
                        7.toBandFilterLte(),
                        38.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutra64qamDLMimoUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutra64qamDLMimoUL.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM64.toModulation(),
                    modUL = ModulationOrder.QAM16.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters = UeCapabilityFilterLte()
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutra256qamDLMimoUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutra256qamDLMimoUL.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    38,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        28.toBandFilterLte(),
                        3.toBandFilterLte(),
                        20.toBandFilterLte(),
                        8.toBandFilterLte(),
                        1.toBandFilterLte(),
                        7.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutraCombinationReducedMimoPerCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutraCombinationReducedMimoPerCC.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 4.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 2.toPowerClass()
                ),
                BandLteDetails(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(reducedFormat = true, skipFallbackCombRequested = true)
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapEutraOmitEnDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutraOmitEnDc.json",
                    )!!
                    .readBytes()
            )

        // LTE Category
        assertEquals(20, capabilities.lteCategoryDL)
        assertEquals(18, capabilities.lteCategoryUL)

        // LTE Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapEutraOmitEnDc.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Output.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedCsv, actualCsv)

        // LTE bands
        val expectedLteBands =
            listOf(
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                )
            )
        val actualLteBands = capabilities.lteBands
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands = listOf(BandNrDetails(3), BandNrDetails(28), BandNrDetails(78))
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        val expectedNrSaBands = listOf(BandNrDetails(3), BandNrDetails(28), BandNrDetails(78))
        assertArrayEquals(expectedNrSaBands.toTypedArray(), actualNrSaBands.toTypedArray())

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                omitEnDc = true,
                lteBands = listOf(1.toBandFilterLte(), 20.toBandFilterLte(), 28.toBandFilterLte())
            )
        assertEquals(expectedLteFilters, actualLteFilters)
    }

    @Test
    fun ueCapNrOneCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapNrOneCC.json",
                    )!!
                    .readBytes()
            )

        // NR bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(80, 40)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 40, 20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualNrFilters = capabilities.ueCapFilters[0]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        1.toBandFilterNr(),
                        3.toBandFilterNr(),
                        7.toBandFilterNr(),
                        28.toBandFilterNr(),
                        38.toBandFilterNr(),
                        40.toBandFilterNr(),
                        78.toBandFilterNr(),
                        79.toBandFilterNr(),
                        257.toBandFilterNr(),
                        258.toBandFilterNr()
                    )
            )
        assertEquals(expectedNrFilters, actualNrFilters)
    }

    @Test
    fun ueCapNrThreeCC() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapNrThreeCC.json",
                    )!!
                    .readBytes()
            )

        // NR bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(30).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(40, 30, 20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20), intArrayOf(100, 40, 20))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 40, 30, 20)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualNrFilters = capabilities.ueCapFilters[0]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                lteBands =
                    listOf(
                        2.toBandFilterLte(),
                        4.toBandFilterLte(),
                        5.toBandFilterLte(),
                        13.toBandFilterLte(),
                        46.toBandFilterLte(),
                        48.toBandFilterLte(),
                        66.toBandFilterLte()
                    ),
                nrBands = listOf(5.toBandFilterNr(), 261.toBandFilterNr(), 77.toBandFilterNr())
            )
        assertEquals(expectedNrFilters, actualNrFilters)
    }

    @Test
    fun ueCapMrdcDefaultBws() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapMrdcDefaultBws.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 4.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 2.toPowerClass()
                ),
                BandLteDetails(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    43,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
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
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(50, 40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(30, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                        )
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                        )
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(3, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        3.toBandFilterLte(),
                        20.toBandFilterLte(),
                        7.toBandFilterLte(),
                        1.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)

        val actualNrFilters = capabilities.ueCapFilters[1]
        val expectedNrFilters = UeCapabilityFilterNr(rat = Rat.NR, eutraNrOnly = true)
        assertEquals(expectedNrFilters, actualNrFilters)

        val actualEutraNrFilters = capabilities.ueCapFilters[2]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        20.toBandFilterLte()
                    ),
                nrBands = listOf(38.toBandFilterNr(), 28.toBandFilterNr(), 78.toBandFilterNr())
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapMrdcFR2() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapMrdcFR2.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    18,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    19,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    21,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    26,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    42,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
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
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = false
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM64.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(60, intArrayOf(200, 100, 50)),
                            BwsNr(120, intArrayOf(200, 100, 50))
                        )
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 2.toMimo()
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(3, capabilities.ueCapFilters.size)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte(),
                        46.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)

        val actualNrFilters = capabilities.ueCapFilters[1]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte(),
                        46.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        1.toBandFilterNr(),
                        3.toBandFilterNr(),
                        5.toBandFilterNr(),
                        7.toBandFilterNr(),
                        8.toBandFilterNr(),
                        20.toBandFilterNr(),
                        28.toBandFilterNr(),
                        38.toBandFilterNr(),
                        40.toBandFilterNr(),
                        41.toBandFilterNr(),
                        77.toBandFilterNr(),
                        78.toBandFilterNr(),
                        79.toBandFilterNr(),
                        80.toBandFilterNr(),
                        84.toBandFilterNr(),
                        257.toBandFilterNr(),
                        258.toBandFilterNr(),
                        260.toBandFilterNr(),
                        261.toBandFilterNr()
                    )
            )
        assertEquals(expectedNrFilters, actualNrFilters)

        val actualEutraNrFilters = capabilities.ueCapFilters[2]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte(),
                        46.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        1.toBandFilterNr(),
                        3.toBandFilterNr(),
                        5.toBandFilterNr(),
                        7.toBandFilterNr(),
                        8.toBandFilterNr(),
                        20.toBandFilterNr(),
                        28.toBandFilterNr(),
                        38.toBandFilterNr(),
                        40.toBandFilterNr(),
                        41.toBandFilterNr(),
                        77.toBandFilterNr(),
                        78.toBandFilterNr(),
                        79.toBandFilterNr(),
                        80.toBandFilterNr(),
                        84.toBandFilterNr(),
                        257.toBandFilterNr(),
                        258.toBandFilterNr(),
                        260.toBandFilterNr(),
                        261.toBandFilterNr()
                    )
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapMrdcExynos() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapMrdcExynos.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
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
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(20, 15, 10)),
                            BwsNr(60, intArrayOf(20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                            BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10)),
                            BwsNr(60, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(20, 15, 10, 5), intArrayOf()),
                            BwsNr(30, intArrayOf(20, 15, 10), intArrayOf()),
                            BwsNr(60, intArrayOf(20, 15, 10), intArrayOf())
                        )
                    powerClass = PowerClass.NONE
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(50, 40, 20, 15, 10)),
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
                            BwsNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10))
                        )
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(capabilities.ueCapFilters.size, 3)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        20.toBandFilterLte(),
                        3.toBandFilterLte(),
                        1.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        7.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)

        val actualNrFilters = capabilities.ueCapFilters[1]
        val expectedNrFilters = UeCapabilityFilterNr(rat = Rat.NR, eutraNrOnly = true)
        assertEquals(expectedNrFilters, actualNrFilters)

        val actualEutraNrFilters = capabilities.ueCapFilters[2]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        20.toBandFilterLte(),
                        3.toBandFilterLte(),
                        1.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        7.toBandFilterLte()
                    ),
                nrBands = listOf(3.toBandFilterNr(), 7.toBandFilterNr(), 78.toBandFilterNr())
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapMrdcIntraEnDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapMrdcIntraEnDc.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    5,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    8,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    34,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    39,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    40,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    41,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    42,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    43,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM64.toModulation(),
                    powerClass = 3.toPowerClass()
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
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths = listOf(BwsNr(30, intArrayOf(20, 15, 10)))
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(90, 80, 60, 50, 40, 20)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20)),
                        )
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20)),
                        )
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20)),
                        )
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 80, 60, 50, 40)),
                        )
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(80).apply {
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)),
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(84).apply {
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(
                            BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)),
                        )
                    powerClass = 3.toPowerClass()
                }
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(capabilities.ueCapFilters.size, 3)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters = UeCapabilityFilterLte()
        assertEquals(expectedLteFilters, actualLteFilters)

        val actualNrFilters = capabilities.ueCapFilters[1]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte(),
                        46.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        1.toBandFilterNr(),
                        3.toBandFilterNr(),
                        5.toBandFilterNr(),
                        7.toBandFilterNr(),
                        8.toBandFilterNr(),
                        20.toBandFilterNr(),
                        28.toBandFilterNr(),
                        38.toBandFilterNr(),
                        40.toBandFilterNr(),
                        41.toBandFilterNr(),
                        77.toBandFilterNr(),
                        78.toBandFilterNr(),
                        79.toBandFilterNr(),
                        80.toBandFilterNr(),
                        84.toBandFilterNr(),
                        257.toBandFilterNr(),
                        258.toBandFilterNr(),
                        260.toBandFilterNr(),
                        261.toBandFilterNr()
                    )
            )
        assertEquals(expectedNrFilters, actualNrFilters)

        val actualEutraNrFilters = capabilities.ueCapFilters[2]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        8.toBandFilterLte(),
                        20.toBandFilterLte(),
                        28.toBandFilterLte(),
                        38.toBandFilterLte(),
                        40.toBandFilterLte(),
                        46.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        1.toBandFilterNr(),
                        3.toBandFilterNr(),
                        5.toBandFilterNr(),
                        7.toBandFilterNr(),
                        8.toBandFilterNr(),
                        20.toBandFilterNr(),
                        28.toBandFilterNr(),
                        38.toBandFilterNr(),
                        40.toBandFilterNr(),
                        41.toBandFilterNr(),
                        77.toBandFilterNr(),
                        78.toBandFilterNr(),
                        79.toBandFilterNr(),
                        80.toBandFilterNr(),
                        84.toBandFilterNr(),
                        257.toBandFilterNr(),
                        258.toBandFilterNr(),
                        260.toBandFilterNr(),
                        261.toBandFilterNr()
                    )
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapMrdcIntraEnDcV1590() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapMrdcIntraEnDcV1590.json",
                    )!!
                    .readBytes()
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
                BandLteDetails(
                    1,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    3,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    7,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    20,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    28,
                    mimoDL = 2.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
                ),
                BandLteDetails(
                    32,
                    mimoDL = 2.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    powerClass = PowerClass.NONE
                ),
                BandLteDetails(
                    38,
                    mimoDL = 4.toMimo(),
                    mimoUL = 1.toMimo(),
                    modDL = ModulationOrder.QAM256.toModulation(),
                    modUL = ModulationOrder.QAM256.toModulation(),
                    powerClass = 3.toPowerClass()
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
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(40, 30, 20, 15, 10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5), intArrayOf()))
                    powerClass = PowerClass.NONE
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                    bandwidths =
                        listOf(
                            BwsNr(30, intArrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 15, 10)),
                        )
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                }
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }

        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(capabilities.ueCapFilters.size, 3)
        val actualLteFilters = capabilities.ueCapFilters[0]
        val expectedLteFilters =
            UeCapabilityFilterLte(
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        20.toBandFilterLte(),
                        38.toBandFilterLte()
                    )
            )
        assertEquals(expectedLteFilters, actualLteFilters)

        val actualNrFilters = capabilities.ueCapFilters[1]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        20.toBandFilterLte(),
                        38.toBandFilterLte()
                    ),
                nrBands = listOf(3.toBandFilterNr(), 7.toBandFilterNr(), 78.toBandFilterNr())
            )
        assertEquals(expectedNrFilters, actualNrFilters)

        val actualEutraNrFilters = capabilities.ueCapFilters[2]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        1.toBandFilterLte(),
                        3.toBandFilterLte(),
                        7.toBandFilterLte(),
                        20.toBandFilterLte(),
                        38.toBandFilterLte()
                    ),
                nrBands = listOf(3.toBandFilterNr(), 7.toBandFilterNr(), 78.toBandFilterNr())
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapEutraNrOnlyIntraBcsAll() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapEutraNrOnlyIntraBcsAll.json",
                    )!!
                    .readBytes()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualEutraNrFilters = capabilities.ueCapFilters[0]
        val expectedEutraNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.EUTRA_NR,
                lteBands =
                    listOf(
                        28.toBandFilterLte(),
                        1.toBandFilterLte(),
                        8.toBandFilterLte(),
                        7.toBandFilterLte(),
                        3.toBandFilterLte()
                    ),
                nrBands =
                    listOf(
                        5.toBandFilterNr(),
                        7.toBandFilterNr(),
                        78.toBandFilterNr(),
                        258.toBandFilterNr()
                    )
            )
        assertEquals(expectedEutraNrFilters, actualEutraNrFilters)
    }

    @Test
    fun ueCapNrDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapNrdc.json",
                    )!!
                    .readBytes()
            )

        // NR
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(2).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(5).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(7).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(8).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(12).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(13).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(14).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(18).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(20).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(25).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(26).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(29).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5), intArrayOf()))
                    powerClass = PowerClass.NONE
                },
                BandNrDetails(30).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(40, 30, 20, 15, 10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(48).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(
                            BwsNr(
                                30,
                                intArrayOf(100, 80, 60, 50, 40, 30, 20, 10),
                                intArrayOf(100, 40, 30, 20, 10)
                            )
                        )
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(53).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(10)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(66).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(40, 30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(70).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths =
                        listOf(BwsNr(15, intArrayOf(25, 20, 15, 10, 5), intArrayOf(15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(71).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(75).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(BwsNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5), intArrayOf()))
                    powerClass = PowerClass.NONE
                },
                BandNrDetails(76).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(5), intArrayOf()))
                    powerClass = PowerClass.NONE
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths =
                        listOf(BwsNr(30, intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                },
                BandNrDetails(91).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(92).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(93).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(94).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    rateMatchingLteCrs = true
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(257).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    bandwidths = listOf(BwsNr(120, intArrayOf(200, 100, 50)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(258).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    bandwidths = listOf(BwsNr(120, intArrayOf(200, 100, 50)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(260).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(261).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM64.toModulation()
                    bandwidths = listOf(BwsNr(120, intArrayOf(100, 50)))
                    powerClass = 3.toPowerClass()
                    mimoDL = 2.toMimo()
                    mimoUL = 2.toMimo()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualNrFilters = capabilities.ueCapFilters[0]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                includeNrDc = true,
                nrBands = listOf(66.toBandFilterNr(), 261.toBandFilterNr())
            )
        assertEquals(expectedNrFilters, actualNrFilters)
    }

    @Test
    fun ueCapNrSUL() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapNrSUL.json",
                    )!!
                    .readBytes()
            )

        // NR
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(1).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(38).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(40).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(30, intArrayOf(80, 60, 50, 40, 30, 20)))
                    powerClass = 3.toPowerClass()
                },
                BandNrDetails(41).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)))
                },
                BandNrDetails(77).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20)))
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 90, 80, 60, 50, 40, 20, 10)))
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(79).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 100
                    bandwidths = listOf(BwsNr(30, intArrayOf(100, 80, 60, 50, 40)))
                },
                BandNrDetails(80).apply {
                    modDL = ModulationOrder.NONE.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(), intArrayOf(30, 25, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(84).apply {
                    modDL = ModulationOrder.NONE.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(), intArrayOf(20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    mimoUL = 1.toMimo()
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

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualNrFilters = capabilities.ueCapFilters[0]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                nrBands =
                    listOf(
                        BandFilterNr(78, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(77, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(80, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(81, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(82, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(83, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(84, maxCCsDl = 1, maxCCsUl = 1),
                        BandFilterNr(86, maxCCsDl = 1, maxCCsUl = 1)
                    )
            )
        assertEquals(expectedNrFilters, actualNrFilters)
    }

    @Test
    fun ueCapNrOmitEnDc() {
        val capabilities =
            ImportCapabilityInformation.parse(
                getResourceAsStream(
                        "/newEngine/input/json/ueCapNrOmitEnDc.json",
                    )!!
                    .readBytes()
            )

        // NR
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                BandNrDetails(3).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 3.toPowerClass()
                    bandwidths = listOf(BwsNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)))
                    rateMatchingLteCrs = true
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(28).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    bandwidths = listOf(BwsNr(15, intArrayOf(30, 20, 15, 10, 5)))
                    powerClass = 3.toPowerClass()
                    rateMatchingLteCrs = true
                    mimoDL = 2.toMimo()
                    mimoUL = 1.toMimo()
                },
                BandNrDetails(78).apply {
                    modDL = ModulationOrder.QAM256.toModulation()
                    modUL = ModulationOrder.QAM256.toModulation()
                    powerClass = 2.toPowerClass()
                    maxUplinkDutyCycle = 50
                    bandwidths =
                        listOf(BwsNr(30, intArrayOf(100, 90, 80, 70, 60, 50, 40, 30, 20, 15, 10)))
                    mimoDL = 4.toMimo()
                    mimoUL = 1.toMimo()
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands.toTypedArray())

        // NR Combos
        val expectedNrCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrOmitEnDc-NR.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }

        val actualNrCsv = Output.toCsv(capabilities.nrCombos).lines().dropLastWhile { it.isBlank() }
        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // Ue Cap filters
        assertEquals(1, capabilities.ueCapFilters.size)
        val actualNrFilters = capabilities.ueCapFilters[0]
        val expectedNrFilters =
            UeCapabilityFilterNr(
                rat = Rat.NR,
                omitEnDc = true,
                nrBands =
                    listOf(
                        3.toBandFilterNr(),
                        28.toBandFilterNr(),
                        77.toBandFilterNr(),
                        78.toBandFilterNr()
                    )
            )
        assertEquals(expectedNrFilters, actualNrFilters)
    }
}
