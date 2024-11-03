package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import io.mockk.every
import io.mockk.mockkStatic
import it.smartphonecombo.uecapabilityparser.UtilityForTests.capabilitiesAssertEquals
import it.smartphonecombo.uecapabilityparser.UtilityForTests.deleteDirectory
import it.smartphonecombo.uecapabilityparser.UtilityForTests.multiPartRequest
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.Custom
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeCompressionTest {
    private val resourcesPath = "src/test/resources/server"
    private val endpointParse = arrayOf("/parse/multiPart/", "/parse/multiPart").random()
    private val endpointStore = "/store/"
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val storedId = "12a9cc86-e5d8-4d26-afd5-7d4d53e88b66"

    companion object {
        private var pushedCap: Capabilities? = null
        private val dispatcher = StandardTestDispatcher()

        @JvmStatic
        @BeforeAll
        fun mockDispatchers() {
            mockkStatic(Dispatchers::Custom)
            every { Dispatchers.Custom } returns dispatcher
        }
    }

    @BeforeEach
    fun setup() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        Config["compression"] = "true"
        Config.remove("store")
    }

    @AfterEach
    fun teardown() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        Config.remove("store")
        Config.remove("compression")
    }

    @Test
    fun storeElement() {
        Config["store"] = tmpStorePath
        val oracle = "$resourcesPath/oracleForCompression/output/$storedId.json.gz"
        val input0 = "$resourcesPath/oracleForCompression/input/$storedId-0.gz"

        storeTest(
            url = endpointParse,
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "W")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf(input0),
            oraclePath = oracle,
        )

        assumeTrue(pushedCap != null)
        val id = pushedCap?.id ?: ""
        capabilitiesAssertEquals(
            oracle,
            File("$tmpStorePath/output/$id.json.gz").toInputSource(true).readText(),
        )
        Assertions.assertLinesMatch(
            File(input0).toInputSource(true).readText().lines(),
            File("$tmpStorePath/input/$id-0.gz").toInputSource(true).readText().lines(),
        )
    }

    @Test
    fun listWithOneElement() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(endpointStore + "list", "$resourcesPath/oracleForCompression/list1Elem.json")
    }

    @Test
    fun getItem() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(
            "${endpointStore}getItem?id=$storedId",
            "$resourcesPath/oracleForCompression/item.json",
        )
    }

    @Test
    fun getOutput() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(
            "${endpointStore}getOutput?id=$storedId",
            "$resourcesPath/oracleForCompression/output/$storedId.json.gz",
            json = true,
            gzip = true,
        )
    }

    @Test
    fun getInput() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(
            "${endpointStore}getInput?id=$storedId-0",
            "$resourcesPath/oracleForCompression/input/$storedId-0.gz",
            json = false,
            gzip = true,
        )
    }

    private fun getTest(
        url: String,
        oraclePath: String,
        json: Boolean = true,
        gzip: Boolean = false,
    ) =
        JavalinTest.test(JavalinApp().newServer()) { _, client ->
            dispatcher.scheduler.advanceUntilIdle()
            val response = client.get(url)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val actualText = response.body?.string() ?: ""
            val expectedText =
                if (gzip) File(oraclePath).toInputSource(true).readText()
                else File(oraclePath).readText()

            if (json) {
                val actual = Json.parseToJsonElement(actualText)
                val expected = Json.parseToJsonElement(expectedText)
                Assertions.assertEquals(expected.jsonObject, actual.jsonObject)
            } else {
                Assertions.assertEquals(expectedText, actualText)
            }
        }

    private fun storeTest(
        url: String,
        request: JsonArray,
        files: List<String>,
        oraclePath: String,
    ) =
        JavalinTest.test(JavalinApp().newServer()) { _, client ->
            dispatcher.scheduler.advanceUntilIdle()
            val response =
                client.request(multiPartRequest(client.origin + url, request, files, true))
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            pushedCap = capabilitiesAssertEquals(oraclePath, response.body?.string() ?: "", true)
        }
}
