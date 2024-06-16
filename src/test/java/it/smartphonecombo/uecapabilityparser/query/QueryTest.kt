package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.NullInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.model.toBwClass
import it.smartphonecombo.uecapabilityparser.query.Comparator.*
import it.smartphonecombo.uecapabilityparser.query.FieldBandsDetails.*
import it.smartphonecombo.uecapabilityparser.query.FieldCombos.*
import it.smartphonecombo.uecapabilityparser.query.FieldNumber.LTE_CATEGORY_DL
import it.smartphonecombo.uecapabilityparser.query.FieldNumber.LTE_CATEGORY_UL
import it.smartphonecombo.uecapabilityparser.query.FieldNumber.TIMESTAMP
import it.smartphonecombo.uecapabilityparser.query.FieldString.DESCRIPTION
import it.smartphonecombo.uecapabilityparser.query.FieldString.LOG_TYPE
import it.smartphonecombo.uecapabilityparser.query.FieldStrings.LTE_ALT_TBS_IND
import it.smartphonecombo.uecapabilityparser.query.FieldStrings.NSA_BANDS
import it.smartphonecombo.uecapabilityparser.query.FieldStrings.SA_BANDS
import java.text.ParseException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class QueryTest {

    companion object {
        val capabilities = mutableListOf<Capabilities>()
        private val resourcesPath = "src/test/resources/server/inputForQuery"

        @JvmStatic
        @BeforeAll
        fun loadLibrary() {
            val store = resourcesPath
            val index = LibraryIndex.buildIndex(store, outputCacheSize = 10000)
            val capabilities =
                index.getAll().mapNotNull { indexLine ->
                    val id = indexLine.id
                    val compressed = indexLine.compressed
                    val filePath = "$store/output/$id.json"

                    try {
                        val text = IOUtils.getInputSource(filePath, compressed) ?: NullInputSource
                        val capabilities = Json.custom().decodeFromInputSource<Capabilities>(text)
                        capabilities
                    } catch (ex: Exception) {
                        null
                    }
                }
            this.capabilities.addAll(capabilities)
        }
    }

    @Test
    fun testLteCategory() {
        val criteriaList =
            listOf(
                CriteriaNumber(LTE_CATEGORY_DL, comparator = NOT_EQUALS, value = 4),
                CriteriaNumber(LTE_CATEGORY_UL, comparator = GREATER, value = 4),
                CriteriaNumber(LTE_CATEGORY_UL, comparator = LESS, value = 13),
                CriteriaNumber(LTE_CATEGORY_UL, comparator = NOT_EQUALS, value = 5)
            )

        val oracleList =
            listOf("0c6791b8-e233-42e6-97dc-ebab000a9f4e", "012eb08f-5ca6-4675-ae7f-c22300b5b7cf")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCategory2() {
        val criteriaList =
            listOf(
                CriteriaNumber(LTE_CATEGORY_UL, comparator = GREATER, value = 13),
                CriteriaNumber(LTE_CATEGORY_DL, comparator = EQUALS, value = 19)
            )

        val oracleList =
            listOf(
                "d34d1c8b-c311-49e9-9cd5-3c9324e083f0",
                "674f8112-562f-43f0-ba8e-58a29742d0d6",
                "3a43fb83-1198-43de-a11f-8a8314c94ab4",
                "5e16e1db-2437-4f04-b1c0-903f5ad2e647",
                "f33ebd49-ed94-4fef-a26f-6586d6e51c59"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNsaBands() {
        val criteriaList =
            listOf(
                CriteriaStrings(NSA_BANDS, comparator = HAS_ALL, value = listOf("78", "75")),
                CriteriaStrings(NSA_BANDS, comparator = HAS_NONE, value = listOf("5")),
                CriteriaStrings(NSA_BANDS, comparator = HAS_ANY, value = listOf("257", "258"))
            )

        val oracleList = listOf("674f8112-562f-43f0-ba8e-58a29742d0d6")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNsaBands2() {
        val criteriaList =
            listOf(
                CriteriaStrings(NSA_BANDS, comparator = HAS_ANY, value = listOf("260")),
                CriteriaStrings(SA_BANDS, comparator = IS_EMPTY)
            )

        val oracleList =
            listOf("d5eafd82-153d-4301-a1a2-fa93c9c1de12", "8d22d41c-629d-4a41-99e1-0a729fbe5687")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testSaBands() {
        val criteriaList =
            listOf(
                CriteriaStrings(SA_BANDS, comparator = HAS_NONE, value = listOf("78", "48")),
                CriteriaStrings(SA_BANDS, comparator = IS_NOT_EMPTY)
            )

        val oracleList =
            listOf(
                "e49c4348-ff75-49a5-a888-0c187ba9a7c3",
                "d6cf9021-8a5e-4a2e-a09f-b9e26a55aff3",
                "1e1c0a44-7b3e-4b9b-90dc-dfc9d3dec7b8"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteAltTbs() {
        val criteriaList =
            listOf(
                CriteriaStrings(
                    LTE_ALT_TBS_IND,
                    comparator = HAS_ANY,
                    value = listOf("26a", "33b")
                ),
                CriteriaStrings(LTE_ALT_TBS_IND, comparator = HAS_NONE, value = listOf("33a"))
            )

        val oracleList =
            listOf(
                "b9df0ae4-332b-4538-a22c-497d3010d257",
                "d34d1c8b-c311-49e9-9cd5-3c9324e083f0",
                "e5625ba2-89dc-4161-8c8f-079f5440b65e",
                "d7ef2145-5fb2-4f7a-8d71-9c3946f365a6",
                "250e2701-1b9e-4831-bd5e-b9f0efe649a4"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLogType() {
        val criteriaList = listOf(CriteriaString(LOG_TYPE, comparator = EQUALS, value = "CNR"))

        val oracleList =
            listOf(
                "4c26b26b-ecf0-4682-aa4a-67df6109902f",
                "e5c5a975-759c-40ae-8186-01ad09d0f1a8",
                "ab28003e-dbfa-4c2b-90fa-5260155d2bcf"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testDescription() {
        val criteriaList =
            listOf(
                CriteriaString(DESCRIPTION, comparator = CONTAINS, value = "Test"),
                CriteriaString(DESCRIPTION, comparator = CONTAINS, value = "nsa"),
                CriteriaString(DESCRIPTION, comparator = NOT_CONTAINS, value = "xxx"),
                CriteriaString(DESCRIPTION, comparator = NOT_EQUALS, value = "Test")
            )

        val oracleList = listOf("16ec752f-1cdf-4d44-bc48-af97c419a01e")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testTimestamp() {
        val criteriaList =
            listOf(
                CriteriaNumber(
                    TIMESTAMP,
                    comparator = GREATER,
                    value = dateStringToUnixTimestamp("2024-02-06 08:41:00")
                ),
                CriteriaNumber(
                    TIMESTAMP,
                    comparator = LESS,
                    value = dateStringToUnixTimestamp("2024-02-06 08:41:12")
                ),
            )

        val oracleList =
            listOf(
                "f241d157-2ac9-4e12-b166-fb23236a2e42",
                "b9df0ae4-332b-4538-a22c-497d3010d257",
                "0c6791b8-e233-42e6-97dc-ebab000a9f4e",
                "4cff7aa2-0d6e-497c-ba90-b2c95b15deb1",
                "744f5463-39d0-48ad-a47d-0a03803f28b4",
                "a3be1aca-f7be-42b9-9e0b-a1a0908b62a4",
                "d5eafd82-153d-4301-a1a2-fa93c9c1de12",
                "d34d1c8b-c311-49e9-9cd5-3c9324e083f0",
                "878e42c8-30cb-4c3b-b9de-dde90dc2f30c",
                "674f8112-562f-43f0-ba8e-58a29742d0d6",
                "56a0ec4b-7d7c-4fe2-ae22-3c67353b50d0",
                "cbb0d14f-8c7f-4f3a-8254-4295da7c39f1"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteBands() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    LTE_BANDS,
                    comparator = HAS_ANY,
                    value = listOf(BandLteDetailsValue(32))
                ),
                CriteriaBands(
                    LTE_BANDS,
                    comparator = HAS_NONE,
                    value = listOf(BandLteDetailsValue(20))
                )
            )

        val oracleList =
            listOf(
                "0c6791b8-e233-42e6-97dc-ebab000a9f4e",
                "d7ef2145-5fb2-4f7a-8d71-9c3946f365a6",
                "45f4a32b-1fc4-4f04-a707-2d311871e979",
                "2fd1b9df-2df4-40c8-8551-6390fa2a6513",
                "410a8c65-1f83-49b6-8b07-6831d195d712"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteBands2() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    LTE_BANDS,
                    comparator = IS_NOT_EMPTY,
                ),
                CriteriaBands(
                    LTE_BANDS,
                    comparator = HAS_ANY,
                    value = listOf(BandLteDetailsValue(32, minMimoDl = 4))
                ),
                CriteriaBands(
                    LTE_BANDS,
                    comparator = HAS_ANY,
                    value = listOf(BandLteDetailsValue(41, minPowerClass = PowerClass.PC2))
                )
            )

        val oracleList =
            listOf(
                "04572a2f-7dff-4d13-9c49-47bdeff68e1f",
                "d7a270f0-c591-4df7-87d3-4314a9d1af19",
                "84e455b5-59f7-427b-ab5f-407db31ebdfd",
                "4c87b537-01bc-4594-93bb-41ffda4b3b49",
                "fdf932fb-cb0f-4403-a81f-8271f85379e3",
                "af211d96-be1a-4d4f-867c-e8070d796385"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteBands3() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    LTE_BANDS,
                    comparator = IS_EMPTY,
                )
            )

        val oracleList =
            listOf(
                "12899a32-767c-4061-9499-312c842b16c9",
                "4cff7aa2-0d6e-497c-ba90-b2c95b15deb1",
                "744f5463-39d0-48ad-a47d-0a03803f28b4",
                "56a0ec4b-7d7c-4fe2-ae22-3c67353b50d0",
                "cbb0d14f-8c7f-4f3a-8254-4295da7c39f1",
                "c4763659-5907-4512-b91d-437472207f9c",
                "16ec752f-1cdf-4d44-bc48-af97c419a01e",
                "4ecba397-9ec5-4805-a93c-e7df4230f0d8",
                "a51e264f-4092-486f-bba6-f41e3e6ac82f",
                "2788dea2-b15e-4adb-9548-2efbb83322f9",
                "9da68ecd-de06-4938-b158-ad43add0f1b7",
                "b57207cb-03d5-4306-abee-8ce79727a483",
                "c65d642a-83c3-4df8-9138-4a969577ce05",
                "452022bf-002f-4e89-9b17-2e0f6956c3e3",
                "8f4d5044-0920-4a62-82ce-d7b36f906627",
                "4c26b26b-ecf0-4682-aa4a-67df6109902f",
                "ef2a3675-3e28-4ee9-aee9-85e427edcbc5",
                "82e1bb73-a1cb-4158-bd60-4fe19b0d37a1",
                "959c63f3-e7b0-4412-b6d4-7c8010947b49",
                "003e3f5b-fbaf-4620-bc34-3fbfdb1ac5fb",
                "e6fd3523-a562-4e04-bf2d-8f1cfa6f2f78",
                "e5c5a975-759c-40ae-8186-01ad09d0f1a8",
                "e09400d3-1d8a-47b6-9f73-515aeb668b23",
                "14b63d26-e357-460a-bcf7-d8aa24a160c8",
                "6d25dc85-9afe-4bda-bda4-d67515817112",
                "5c033eef-9f00-42ff-a01f-a67f497e3d59",
                "7945e80e-2401-4881-baec-ab59e976749c",
                "bc6df2e5-28c3-421d-b14b-1c585223bcad",
                "7a2f1ee1-1a8e-4023-a276-7ef2148ceb9e",
                "ab28003e-dbfa-4c2b-90fa-5260155d2bcf",
                "3da2cb67-e263-4f3f-b9b0-d30abbb2bd4e"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrBands() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            BandNrDetailsValue(257),
                            BandNrDetailsValue(258),
                            BandNrDetailsValue(260),
                            BandNrDetailsValue(261)
                        )
                ),
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_NONE,
                    value = listOf(BandNrDetailsValue(28), BandNrDetailsValue(71))
                )
            )

        val oracleList =
            listOf(
                "a3be1aca-f7be-42b9-9e0b-a1a0908b62a4",
                "2788dea2-b15e-4adb-9548-2efbb83322f9",
                "8d22d41c-629d-4a41-99e1-0a729fbe5687"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrBands2() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ALL,
                    value = listOf(BandNrDetailsValue(28, minMimoDl = 4))
                ),
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            BandNrDetailsValue(78, minMimoUl = 2),
                            BandNrDetailsValue(79, minMimoUl = 2)
                        )
                )
            )

        val oracleList =
            listOf(
                "878e42c8-30cb-4c3b-b9de-dde90dc2f30c",
                "e49c4348-ff75-49a5-a888-0c187ba9a7c3",
                "bc6df2e5-28c3-421d-b14b-1c585223bcad",
                "7a2f1ee1-1a8e-4023-a276-7ef2148ceb9e"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrBands3() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ALL,
                    value =
                        listOf(
                            BandNrDetailsValue(78, supportedBw = 70),
                            BandNrDetailsValue(78, supportedBw = 90)
                        )
                ),
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ALL,
                    value =
                        listOf(
                            BandNrDetailsValue(257, supportedBw = 200),
                            BandNrDetailsValue(258, supportedBw = 200)
                        )
                )
            )

        val oracleList = listOf("d5eafd82-153d-4301-a1a2-fa93c9c1de12")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrBands4() {
        val criteriaList =
            listOf(
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ALL,
                    value =
                        listOf(
                            BandNrDetailsValue(78, minPowerClass = PowerClass.PC2),
                            BandNrDetailsValue(78, minPowerClass = PowerClass.PC3)
                        )
                ),
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ALL,
                    value = listOf(BandNrDetailsValue(257, minPowerClass = PowerClass.PC1))
                )
            )

        val oracleList =
            listOf(
                "cbb0d14f-8c7f-4f3a-8254-4295da7c39f1",
                "c4763659-5907-4512-b91d-437472207f9c",
                "16ec752f-1cdf-4d44-bc48-af97c419a01e"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrBands5() {
        val criteriaList =
            listOf(
                CriteriaBands(NR_BANDS, comparator = IS_NOT_EMPTY),
                CriteriaBands(
                    NR_BANDS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            BandNrDetailsValue(86, supportedBw = 10),
                            BandNrDetailsValue(84, supportedBw = 15),
                            BandNrDetailsValue(80, supportedBw = 25)
                        )
                )
            )

        val oracleList =
            listOf("00faed12-5b01-a515-9bea-f71b8c97d6d8", "12899a32-767c-4061-9499-312c842b16c9")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombos() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    LTE_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents = listOf(LteComponentDlValue(32, minMimo = 2)),
                                ulComponents =
                                    listOf(LteComponentUlValue(28), LteComponentUlValue(3))
                            ),
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        LteComponentDlValue(32, minMimo = 4),
                                        LteComponentDlValue(20, minMimo = 2)
                                    ),
                                ulComponents = listOf(LteComponentUlValue(28))
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "56a0ec4b-7d7c-4fe2-ae22-3c67353b50d0",
                "452022bf-002f-4e89-9b17-2e0f6956c3e3",
                "f33ebd49-ed94-4fef-a26f-6586d6e51c59"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombo2() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    LTE_COMBOS,
                    comparator = HAS_ALL,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        LteComponentDlValue(3, minMimo = 4),
                                        LteComponentDlValue(3, minMimo = 4),
                                        LteComponentDlValue(7, minMimo = 4),
                                        LteComponentDlValue(7, minMimo = 4),
                                        LteComponentDlValue(1, minMimo = 4)
                                    ),
                                ulComponents = listOf(LteComponentUlValue(3))
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "f241d157-2ac9-4e12-b166-fb23236a2e42",
                "4cff7aa2-0d6e-497c-ba90-b2c95b15deb1",
                "744f5463-39d0-48ad-a47d-0a03803f28b4",
                "a51e264f-4092-486f-bba6-f41e3e6ac82f",
                "9da68ecd-de06-4938-b158-ad43add0f1b7",
                "452022bf-002f-4e89-9b17-2e0f6956c3e3",
                "4c87b537-01bc-4594-93bb-41ffda4b3b49",
                "6d25dc85-9afe-4bda-bda4-d67515817112",
                "fdf932fb-cb0f-4403-a81f-8271f85379e3",
                "7945e80e-2401-4881-baec-ab59e976749c",
                "f9d6d3e8-a654-43e7-879c-3ba1ad99d3f3"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombos3() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    LTE_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        LteComponentDlValue(3, minMimo = 2),
                                        LteComponentDlValue(
                                            3,
                                            minBwClass = "C".toBwClass(),
                                            minMimo = 2
                                        ),
                                    ),
                                ulComponents = listOf(LteComponentUlValue(3))
                            )
                        )
                )
            )

        val oracleList = listOf("ef2a3675-3e28-4ee9-aee9-85e427edcbc5")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombos4() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    LTE_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        LteComponentDlValue(40),
                                        LteComponentDlValue(42),
                                        LteComponentDlValue(42, minBwClass = "C".toBwClass()),
                                    ),
                                ulComponents = listOf()
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "4cff7aa2-0d6e-497c-ba90-b2c95b15deb1",
                "744f5463-39d0-48ad-a47d-0a03803f28b4",
                "003e3f5b-fbaf-4620-bc34-3fbfdb1ac5fb"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombos5() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    LTE_COMBOS,
                    comparator = HAS_ALL,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        LteComponentDlValue(32, minMimo = 4),
                                        LteComponentDlValue(28),
                                        LteComponentDlValue(20),
                                    ),
                                ulComponents = listOf()
                            )
                        )
                )
            )

        val oracleList =
            listOf("56a0ec4b-7d7c-4fe2-ae22-3c67353b50d0", "f33ebd49-ed94-4fef-a26f-6586d6e51c59")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testLteCombos6() {
        val criteriaList = listOf(CriteriaCombos(LTE_COMBOS, comparator = IS_EMPTY))

        val oracleList =
            listOf(
                "12899a32-767c-4061-9499-312c842b16c9",
                "0c6791b8-e233-42e6-97dc-ebab000a9f4e",
                "cbb0d14f-8c7f-4f3a-8254-4295da7c39f1",
                "c4763659-5907-4512-b91d-437472207f9c",
                "16ec752f-1cdf-4d44-bc48-af97c419a01e",
                "2788dea2-b15e-4adb-9548-2efbb83322f9",
                "b57207cb-03d5-4306-abee-8ce79727a483",
                "c65d642a-83c3-4df8-9138-4a969577ce05",
                "8f4d5044-0920-4a62-82ce-d7b36f906627",
                "4c26b26b-ecf0-4682-aa4a-67df6109902f",
                "82e1bb73-a1cb-4158-bd60-4fe19b0d37a1",
                "e5c5a975-759c-40ae-8186-01ad09d0f1a8",
                "e09400d3-1d8a-47b6-9f73-515aeb668b23",
                "14b63d26-e357-460a-bcf7-d8aa24a160c8",
                "5c033eef-9f00-42ff-a01f-a67f497e3d59",
                "bc6df2e5-28c3-421d-b14b-1c585223bcad",
                "7a2f1ee1-1a8e-4023-a276-7ef2148ceb9e",
                "ab28003e-dbfa-4c2b-90fa-5260155d2bcf",
                "3da2cb67-e263-4f3f-b9b0-d30abbb2bd4e"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrCombos() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    NR_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents = listOf(NrComponentDlValue(75, minMimo = 4)),
                                ulComponents = listOf()
                            ),
                            ComboValue(
                                dlComponents = listOf(NrComponentDlValue(28, minMimo = 4)),
                                ulComponents = listOf()
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "b57207cb-03d5-4306-abee-8ce79727a483",
                "8f4d5044-0920-4a62-82ce-d7b36f906627",
                "4c26b26b-ecf0-4682-aa4a-67df6109902f",
                "e49c4348-ff75-49a5-a888-0c187ba9a7c3",
                "bc6df2e5-28c3-421d-b14b-1c585223bcad",
                "7a2f1ee1-1a8e-4023-a276-7ef2148ceb9e"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrCombos2() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    NR_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        NrComponentDlValue(
                                            257,
                                            minMimo = 2,
                                            minMaxBwPerCC = 100,
                                            minBwClass = "I".toBwClass()
                                        )
                                    ),
                                ulComponents =
                                    listOf(
                                        NrComponentUlValue(
                                            257,
                                            minMimo = 2,
                                            minBwClass = "I".toBwClass()
                                        )
                                    )
                            )
                        )
                )
            )

        val oracleList = listOf("c65d642a-83c3-4df8-9138-4a969577ce05")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrCombos3() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    NR_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents = listOf(),
                                ulComponents =
                                    listOf(NrComponentUlValue(78, minMimo = 2, minMaxBwPerCC = 200))
                            ),
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        NrComponentDlValue(
                                            7,
                                            minMimo = 2,
                                            minBwClass = "B".toBwClass(),
                                            minMaxBwPerCC = 40
                                        )
                                    ),
                                ulComponents = listOf()
                            )
                        )
                )
            )

        val oracleList = listOf("3da2cb67-e263-4f3f-b9b0-d30abbb2bd4e")

        test(Query(criteriaList), oracleList)
    }

    @Test
    // testNrCombos3 with bw class lowercase
    fun testNrCombos4() {
        var criteriaList =
            listOf(
                CriteriaCombos(
                    NR_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboValue(
                                dlComponents = listOf(),
                                ulComponents =
                                    listOf(NrComponentUlValue(78, minMimo = 2, minMaxBwPerCC = 200))
                            ),
                            ComboValue(
                                dlComponents =
                                    listOf(
                                        NrComponentDlValue(
                                            7,
                                            minMimo = 2,
                                            minBwClass = "B".toBwClass(),
                                            minMaxBwPerCC = 40
                                        )
                                    ),
                                ulComponents = listOf()
                            )
                        )
                )
            )

        // Replace bw class B with b
        val criteriaJson = Json.encodeToString(criteriaList).replace("\"B\"", "\"b\"")

        criteriaList = Json.decodeFromString<List<CriteriaCombos>>(criteriaJson)

        val oracleList = listOf("3da2cb67-e263-4f3f-b9b0-d30abbb2bd4e")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testNrDcCombos() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    NRDC_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboMrDcValue(
                                dlMasterComponents = listOf(),
                                ulMasterComponents = listOf(),
                                dlSecondaryComponents =
                                    listOf(
                                        NrComponentDlValue(257, minMimo = 2, minMaxBwPerCC = 100)
                                    ),
                                ulSecondaryComponents =
                                    listOf(
                                        NrComponentUlValue(
                                            257,
                                            minMimo = 2,
                                            minBwClass = "I".toBwClass()
                                        )
                                    )
                            )
                        )
                )
            )

        val oracleList =
            listOf("e09400d3-1d8a-47b6-9f73-515aeb668b23", "3da2cb67-e263-4f3f-b9b0-d30abbb2bd4e")

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testEnDcCombos() {
        val criteriaList =
            listOf(
                CriteriaCombos(
                    ENDC_COMBOS,
                    comparator = HAS_ANY,
                    value =
                        listOf(
                            ComboMrDcValue(
                                dlMasterComponents = listOf(LteComponentDlValue(3, minMimo = 4)),
                                ulMasterComponents = listOf(),
                                dlSecondaryComponents =
                                    listOf(
                                        NrComponentDlValue(257, minMimo = 2, minMaxBwPerCC = 100)
                                    ),
                                ulSecondaryComponents =
                                    listOf(
                                        NrComponentUlValue(
                                            257,
                                            minMimo = 2,
                                            minBwClass = "I".toBwClass()
                                        )
                                    )
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "e09400d3-1d8a-47b6-9f73-515aeb668b23",
                "de2d5aac-2d59-471f-8056-b769da6d345d",
                "03fde4cb-98be-4b97-85d1-467fdf786756"
            )

        test(Query(criteriaList), oracleList)
    }

    @Test
    fun testEnDcCombos2() {
        val criteriaList =
            listOf(
                CriteriaCombos(ENDC_COMBOS, comparator = IS_NOT_EMPTY),
                CriteriaCombos(
                    ENDC_COMBOS,
                    comparator = HAS_NONE,
                    value =
                        listOf(
                            ComboMrDcValue(
                                dlMasterComponents = listOf(LteComponentDlValue(3)),
                                ulMasterComponents = listOf(),
                                dlSecondaryComponents = listOf(NrComponentDlValue(78)),
                                ulSecondaryComponents = listOf()
                            ),
                            ComboMrDcValue(
                                dlMasterComponents = listOf(LteComponentDlValue(1)),
                                ulMasterComponents = listOf(),
                                dlSecondaryComponents = listOf(NrComponentDlValue(77)),
                                ulSecondaryComponents = listOf()
                            )
                        )
                )
            )

        val oracleList =
            listOf(
                "16ec752f-1cdf-4d44-bc48-af97c419a01e",
                "2788dea2-b15e-4adb-9548-2efbb83322f9",
                "82e1bb73-a1cb-4158-bd60-4fe19b0d37a1",
                "ab28003e-dbfa-4c2b-90fa-5260155d2bcf"
            )

        test(Query(criteriaList), oracleList)
    }

    private fun test(query: Query, oracle: List<String>) {
        val filtered = capabilities.filter { query.evaluateQuery(it) }.map { it.id }
        println(filtered.joinToString(", ") { "\"$it\"" })
        Assertions.assertArrayEquals(oracle.toTypedArray(), filtered.toTypedArray())
    }

    private fun dateStringToUnixTimestamp(dateString: String): Long {
        val pattern = "yyyy-MM-dd HH:mm:ss"

        val formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"))

        try {
            val instant = Instant.from(formatter.parse(dateString))
            return instant.toEpochMilli()
        } catch (e: ParseException) {
            e.printStackTrace()
            return -1L
        }
    }
}
