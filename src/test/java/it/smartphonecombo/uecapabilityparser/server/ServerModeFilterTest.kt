package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.io.Custom
import it.smartphonecombo.uecapabilityparser.query.BandLteDetailsValue
import it.smartphonecombo.uecapabilityparser.query.BandNrDetailsValue
import it.smartphonecombo.uecapabilityparser.query.ComboMrDcValue
import it.smartphonecombo.uecapabilityparser.query.ComboValue
import it.smartphonecombo.uecapabilityparser.query.Comparator
import it.smartphonecombo.uecapabilityparser.query.CriteriaBands
import it.smartphonecombo.uecapabilityparser.query.CriteriaCombos
import it.smartphonecombo.uecapabilityparser.query.CriteriaString
import it.smartphonecombo.uecapabilityparser.query.FieldBandsDetails
import it.smartphonecombo.uecapabilityparser.query.FieldCombos
import it.smartphonecombo.uecapabilityparser.query.FieldString
import it.smartphonecombo.uecapabilityparser.query.LteComponentDlValue
import it.smartphonecombo.uecapabilityparser.query.LteComponentUlValue
import it.smartphonecombo.uecapabilityparser.query.NrComponentDlValue
import it.smartphonecombo.uecapabilityparser.query.NrComponentUlValue
import it.smartphonecombo.uecapabilityparser.query.Query
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class ServerModeFilterTest {
    private val endpoint = arrayOf("/store/list/filtered/", "/store/list/filtered").random()

    @Test
    fun emptyResult() {
        javalinTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "number")
                            put("field", "LTE_CATEGORY_DL")
                            put("comparator", "EQUALS")
                            put("value", 1000)
                        }
                    }
                },
            oraclePath = "$path/oracleForQuery/empty.json",
        )
    }

    @Test
    fun singleResult() {
        javalinTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "string")
                            put("field", "DESCRIPTION")
                            put("comparator", "IS_NOT_EMPTY")
                        }
                        addJsonObject {
                            put("type", "string")
                            put("field", "DESCRIPTION")
                            put("comparator", "CONTAINS")
                            put("value", "Test")
                        }
                        addJsonObject {
                            put("type", "string")
                            put("field", "DESCRIPTION")
                            put("comparator", "NOT_CONTAINS")
                            put("value", "xxx")
                        }
                    }
                },
            oraclePath = "$path/oracleForQuery/singleResult.json",
        )
    }

    @Test
    fun multiResult() {
        val query =
            Query(
                listOf(
                    CriteriaString(FieldString.DESCRIPTION, Comparator.IS_EMPTY),
                    CriteriaBands(
                        FieldBandsDetails.NR_BANDS,
                        Comparator.HAS_ANY,
                        listOf(BandNrDetailsValue(78)),
                    ),
                )
            )
        javalinTest(
            request = Json.encodeToJsonElement(query).jsonObject,
            oraclePath = "$path/oracleForQuery/multiResult.json",
        )
    }

    @Test
    fun multiResult2() {
        val query =
            Query(
                listOf(
                    CriteriaCombos(
                        FieldCombos.ENDC_COMBOS,
                        Comparator.HAS_ANY,
                        listOf(
                            ComboMrDcValue(
                                dlMasterComponents = listOf(LteComponentDlValue(3)),
                                ulMasterComponents = listOf(LteComponentUlValue(1)),
                                dlSecondaryComponents = listOf(NrComponentDlValue(78)),
                                ulSecondaryComponents = listOf(NrComponentUlValue(78)),
                            )
                        ),
                    ),
                    CriteriaBands(
                        FieldBandsDetails.LTE_BANDS,
                        Comparator.HAS_NONE,
                        listOf(BandLteDetailsValue(20)),
                    ),
                )
            )

        javalinTest(
            request = Json.encodeToJsonElement(query).jsonObject,
            oraclePath = "$path/oracleForQuery/multiResult2.json",
        )
    }

    @Test
    fun multiResult3() {
        val query =
            Query(
                listOf(
                    CriteriaCombos(
                        FieldCombos.NRDC_COMBOS,
                        Comparator.HAS_ANY,
                        listOf(
                            ComboMrDcValue(
                                dlMasterComponents = listOf(LteComponentDlValue(78)),
                                ulMasterComponents = listOf(),
                                dlSecondaryComponents = listOf(NrComponentDlValue(257)),
                                ulSecondaryComponents = listOf(),
                            )
                        ),
                    ),
                    CriteriaCombos(
                        FieldCombos.NR_COMBOS,
                        Comparator.HAS_ANY,
                        listOf(
                            ComboValue(
                                dlComponents = listOf(NrComponentDlValue(78)),
                                ulComponents = listOf(),
                            )
                        ),
                    ),
                )
            )

        javalinTest(
            request = Json.encodeToJsonElement(query).jsonObject,
            oraclePath = "$path/oracleForQuery/multiResult3.json",
        )
    }

    @Test
    fun illegalFieldType() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "string")
                            put("field", "LTE_CATEGORY_DL")
                            put("comparator", "EQUALS")
                            put("value", 1000)
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun illegalComparatorNumber() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "number")
                            put("field", "LTE_CATEGORY_DL")
                            put("comparator", "CONTAINS")
                            put("value", 1000)
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun illegalComparatorString() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "string")
                            put("field", "DESCRIPTION")
                            put("comparator", "HAS_ANY")
                            put("value", "0000")
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun illegalComparatorStrings() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "strings")
                            put("field", "NSA_BANDS")
                            put("comparator", "LESS")
                            putJsonArray("value") {
                                add("78")
                                add("28")
                            }
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun illegalComparatorBands() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "bands")
                            put("field", "NR_BANDS")
                            put("comparator", "GREATER")
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun illegalComparatorCombos() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "combos")
                            put("field", "NR_COMBOS")
                            put("comparator", "NOT_EQUALS")
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun missingRequiredValue() {
        javalinErrorTest(
            request =
                buildJsonObject {
                    putJsonArray("criteriaList") {
                        addJsonObject {
                            put("type", "combos")
                            put("field", "NR_COMBOS")
                            put("comparator", "HAS_ANY")
                        }
                    }
                },
            errorCode = HttpStatus.BAD_REQUEST.code,
        )
    }

    private fun javalinTest(request: JsonObject, oraclePath: String) {
        JavalinTest.test(app.newServer()) { _, client ->
            val response = client.post(endpoint, request)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)

            val actual = Json.custom().parseToJsonElement(response.body?.string() ?: "")
            val expected = Json.custom().parseToJsonElement(File(oraclePath).readText())

            Assertions.assertEquals(expected, actual)
        }
    }

    private fun javalinErrorTest(request: JsonObject, errorCode: Int) {
        JavalinTest.test(app.newServer()) { _, client ->
            val response = client.post(endpoint, request)
            Assertions.assertEquals(errorCode, response.code)
        }
    }

    companion object {
        private val path = "src/test/resources/server/"
        private lateinit var app: JavalinApp
        private val dispatcher = StandardTestDispatcher()

        @JvmStatic
        @BeforeAll
        fun setup() {
            Config["store"] = "$path/inputForQuery/"
            Config["cache"] = "100"
            app = JavalinApp()

            // initialize library, library is initialized only after server.start()
            mockkStatic(Dispatchers::Custom)
            every { Dispatchers.Custom } returns dispatcher
            app.newServer().run {
                start()
                dispatcher.scheduler.advanceUntilIdle()
                stop()
            }
            unmockkAll()
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            Config.clear()
        }
    }
}
