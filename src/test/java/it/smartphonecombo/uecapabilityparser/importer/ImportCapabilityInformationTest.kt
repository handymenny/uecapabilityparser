package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.getResourceAsStream
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.util.Utility
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
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
                ComponentNr(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
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
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
                ComponentNr(79),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bans in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())
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
                ComponentNr(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(80, 40),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(258).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(260).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100, 50),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(261).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100, 50),
                        )
                    bandwidthsUL = bandwidthsDL
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrOneCC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
                ComponentNr(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(30, 20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(30).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(10),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(40, 30, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 40, 20),
                            60 to intArrayOf(),
                        )
                },
                ComponentNr(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(30, 20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 40, 30, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(257).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(258).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(260).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100, 50),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(261).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(),
                            120 to intArrayOf(100, 50),
                        )
                    bandwidthsUL = bandwidthsDL
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrThreeCC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualCsv = Utility.toCsv(capabilities).lines().dropLastWhile { it.isBlank() }
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
            capabilities.enDcCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
            capabilities.lteCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bands in eutra capability
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
                ComponentNr(41),
                ComponentNr(77),
                ComponentNr(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                ComponentNr(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(50, 40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(50, 40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(30, 20, 15, 10, 5),
                            30 to intArrayOf(30, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 20, 15, 10),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 20, 15, 10),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 20, 15, 10),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv =
            capabilities.nrCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
            capabilities.enDcCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
            capabilities.lteCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bands in eutra capability
        val actualNrNsaBands = capabilities.nrNSAbands
        val expectedNrNsaBands =
            listOf(
                ComponentNr(77),
                ComponentNr(78),
                ComponentNr(79),
                ComponentNr(257),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = false
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 60, 50, 40),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(257).apply {
                    modDL = Modulation.QAM64
                    modUL = Modulation.QAM64
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            60 to intArrayOf(200, 100, 50),
                            120 to intArrayOf(200, 100, 50),
                        )
                    bandwidthsUL = bandwidthsDL
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv =
            capabilities.nrCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
            capabilities.enDcCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
            capabilities.lteCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
        assertArrayEquals(expectedLteBands.toTypedArray(), actualLteBands?.toTypedArray())

        // NR NSA bands in eutra capability
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
                ComponentNr(75),
                ComponentNr(77),
                ComponentNr(78),
            )
        assertArrayEquals(expectedNrNsaBands.toTypedArray(), actualNrNsaBands?.toTypedArray())

        // NR SA bands in eutra capability
        val actualNrSaBands = capabilities.nrSAbands
        assertArrayEquals(emptyArray(), actualNrSaBands?.toTypedArray())

        // NR
        // bands in nr capability
        val actualNrBands = capabilities.nrBands
        val expectedNrBands =
            listOf(
                ComponentNr(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(30, 25, 20, 15, 10),
                            60 to intArrayOf(30, 25, 20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5),
                            30 to intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 20, 15, 10),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20, 15, 10),
                            60 to intArrayOf(100, 80, 60, 50, 40, 20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(75).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(20, 15, 10, 5),
                            30 to intArrayOf(20, 15, 10),
                            60 to intArrayOf(20, 15, 10),
                        )
                    bandwidthsUL = mutableMapOf()
                },
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(50, 40, 20, 15, 10),
                            30 to intArrayOf(100, 80, 60, 50, 40, 20, 15, 10),
                            60 to intArrayOf(100, 80, 60, 50, 40, 20, 15, 10),
                        )
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL =
                        mutableMapOf(
                            15 to intArrayOf(),
                            30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10),
                            60 to intArrayOf(),
                        )
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedNrCsv = emptyList<String>()
        val actualNrCsv =
            capabilities.nrCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
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
                ComponentNr(1).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(2).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(3).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(5).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(25, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(7).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(8).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(12).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(13).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(14).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(18).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(20).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(25).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(40, 30, 25, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(26).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(28).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(30, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(29).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = mutableMapOf()
                },
                ComponentNr(30).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(38).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(30 to intArrayOf(40, 30, 20, 15, 10))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(40).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(30 to intArrayOf(80, 60, 50, 40, 30, 20, 15, 10))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(41).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL =
                        mutableMapOf(30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(48).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(30 to intArrayOf(100, 80, 60, 50, 40, 30, 20, 10))
                    bandwidthsUL = mutableMapOf(30 to intArrayOf(100, 40, 30, 20, 10))
                },
                ComponentNr(53).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(30 to intArrayOf(10))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(66).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(40, 30, 20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(70).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(25, 20, 15, 10, 5))
                    bandwidthsUL = mutableMapOf(15 to intArrayOf(15, 10, 5))
                },
                ComponentNr(71).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(75).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(50, 40, 30, 25, 20, 15, 10, 5))
                    bandwidthsUL = mutableMapOf()
                },
                ComponentNr(76).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(5))
                    bandwidthsUL = mutableMapOf()
                },
                ComponentNr(77).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL =
                        mutableMapOf(30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(78).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL =
                        mutableMapOf(30 to intArrayOf(100, 80, 70, 60, 50, 40, 30, 20, 15, 10))
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(79).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    bandwidthsDL = mutableMapOf(30 to intArrayOf(100, 80, 60, 50, 40))
                    bandwidthsUL = bandwidthsDL
                    powerClass = 2
                },
                ComponentNr(91).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(92).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(93).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(94).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM256
                    rateMatchingLTEcrs = true
                    bandwidthsDL = mutableMapOf(15 to intArrayOf(20, 15, 10, 5))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(257).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidthsDL = mutableMapOf(120 to intArrayOf(200, 100, 50))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(258).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidthsDL = mutableMapOf(120 to intArrayOf(200, 100, 50))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(260).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidthsDL = mutableMapOf(120 to intArrayOf(100, 50))
                    bandwidthsUL = bandwidthsDL
                },
                ComponentNr(261).apply {
                    modDL = Modulation.QAM256
                    modUL = Modulation.QAM64
                    bandwidthsDL = mutableMapOf(120 to intArrayOf(100, 50))
                    bandwidthsUL = bandwidthsDL
                },
            )
        assertArrayEquals(expectedNrBands.toTypedArray(), actualNrBands?.toTypedArray())

        // NR Combos
        val expectedNrCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrdc-NR.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualNrCsv =
            capabilities.nrCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
        assertLinesMatch(expectedNrCsv, actualNrCsv)

        // NR-DC Combos
        val expectedNrDcCsv =
            getResourceAsStream("/newEngine/oracle/ueCapNrdc-NR-DC.csv")!!
                .bufferedReader()
                .readLines()
                .dropLastWhile { it.isBlank() }
        val actualNrDcCsv =
            capabilities.nrDcCombos?.let { combos ->
                Utility.toCsv(combos).lines().dropLastWhile { it.isBlank() }
            }
        assertLinesMatch(expectedNrDcCsv, actualNrDcCsv)
    }
}
