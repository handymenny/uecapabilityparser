package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Property
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ServerModeOthersTest {

    private val parserVersion = Property.getProperty("project.version") ?: ""
    private val openapi =
        {}.javaClass
            .getResourceAsStream("/swagger/openapi.json")
            ?.bufferedReader()
            ?.readText()
            ?.replace("http://localhost:8080", "/")
            ?: ""

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
        getTest("/store/0.2.0/status", buildJsonObject { put("enabled", true) })
    }

    @Test
    fun testStoreOpenApi() {
        getTest("/openapi", Json.parseToJsonElement(openapi))
    }

    @Test
    fun testStoreSwaggerOpenApi() {
        getTest("/swagger/openapi.json", Json.parseToJsonElement(openapi))
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
