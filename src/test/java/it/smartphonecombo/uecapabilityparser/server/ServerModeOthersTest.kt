package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.util.Config
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ServerModeOthersTest {

    private val parserVersion = Config.getOrDefault("project.version", "")
    private val openapi =
        {}.javaClass.getResourceAsStream("/swagger/openapi.json")?.reader()?.readText() ?: ""

    private val endpoints =
        listOf(
            "/swagger",
            "custom.js",
            "custom.css",
            "/parse",
            "/parse/multiPart",
            "/csv",
            "/openapi",
            "/swagger/openapi.json",
            "/store/status",
            "/store/list",
            "/store/getItem",
            "/store/getMultiItem",
            "/store/getOutput",
            "/store/getMultiOutput",
            "/store/getInput",
            "/version",
            "/status",
        )

    private val scatTypes = arrayOf("HDF", "SDM", "DLF", "QMDL")
    private val logTypes =
        listOf(
                "H",
                "W",
                "N",
                "C",
                "CNR",
                "E",
                "Q",
                "QLTE",
                "QNR",
                "M",
                "O",
                "QC",
                "RF",
                "SHNR",
                "P",
                "DLF",
                "QMDL",
                "HDF",
                "SDM"
            )
            .filter { scatAvailable || it !in scatTypes }
            .map(LogType::of)

    @AfterEach
    fun tearDown() {
        Config.clear()
    }

    @Test
    fun testVersion() {

        getTest("/version", buildJsonObject { put("version", parserVersion) })
    }

    @Test
    fun testStoreEnabled() {
        Config["store"] = "/store"
        val endpoint = arrayOf("/store/status/", "/store/status").random()
        getTest(endpoint, buildJsonObject { put("enabled", true) })
    }

    @Test
    fun testStoreOpenApi() {
        val endpoint = arrayOf("/openapi", "/openapi/").random()
        getTest(endpoint, Json.parseToJsonElement(openapi))
    }

    @Test
    fun testStoreSwaggerOpenApi() {
        getTest("/swagger/openapi.json", Json.parseToJsonElement(openapi))
    }

    @Test
    fun testStatusStoreEnable() {
        Config["store"] = "/store"
        val status = ServerStatus(parserVersion, endpoints, logTypes, 256000000)
        getTest("/status", Json.encodeToJsonElement(status))
    }

    @Test
    fun testStatusStoreOff() {
        val endpointsNoStore =
            endpoints.filterNot { it.startsWith("/store") && !it.endsWith("status") }
        val status = ServerStatus(parserVersion, endpointsNoStore, logTypes, 256000000)
        getTest("/status", Json.encodeToJsonElement(status))
    }

    private fun getTest(url: String, oracle: JsonElement) {
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.get(url)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val actualText = response.body?.string() ?: ""
            val actual = Json.parseToJsonElement(actualText)
            Assertions.assertEquals(oracle.jsonObject, actual.jsonObject)
        }
    }
}
