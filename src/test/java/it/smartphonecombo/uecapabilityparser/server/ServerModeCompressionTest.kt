package it.smartphonecombo.uecapabilityparser.server

import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.extension.gzipDecompress
import it.smartphonecombo.uecapabilityparser.extension.readText
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeCompressionTest {
    private val resourcesPath = "src/test/resources/server"
    private val base64 = Base64.getEncoder()
    private val endpointParse =
        arrayOf("/parse/0.1.0/", "/parse/", "/parse/0.1.0", "/parse").random()
    private val endpointStore = arrayOf("/store/0.2.0/", "/store/").random()
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val storedId = "12a9cc86-e5d8-4d26-afd5-7d4d53e88b66"

    companion object {
        private var pushedCap: Capabilities? = null
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
                buildJsonObject {
                    put("type", "W")
                    put("input", fileToBase64(input0))
                },
            oraclePath = oracle
        )

        assumeTrue(pushedCap != null)
        val id = pushedCap?.id ?: ""
        capabilitiesAssertEquals(
            File(oracle).gzipDecompress().readText(),
            File("$tmpStorePath/output/$id.json.gz").gzipDecompress().readText()
        )
        Assertions.assertLinesMatch(
            File(input0).gzipDecompress().readText().lines(),
            File("$tmpStorePath/input/$id-0.gz").gzipDecompress().readText().lines()
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
            "$resourcesPath/oracleForCompression/item.json"
        )
    }

    @Test
    fun getOutput() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(
            "${endpointStore}getOutput?id=$storedId",
            "$resourcesPath/oracleForCompression/output/$storedId.json.gz",
            json = true,
            gzip = true
        )
    }

    @Test
    fun getInput() {
        Config["store"] = "$resourcesPath/oracleForCompression"
        getTest(
            "${endpointStore}getInput?id=$storedId-0",
            "$resourcesPath/oracleForCompression/input/$storedId-0.gz",
            json = false,
            gzip = true
        )
    }

    private fun getTest(
        url: String,
        oraclePath: String,
        json: Boolean = true,
        gzip: Boolean = false
    ) =
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.get(url)
            Assertions.assertEquals(HttpStatus.OK_200, response.code)
            val actualText = response.body?.string() ?: ""
            val expectedText =
                if (gzip) File(oraclePath).gzipDecompress().readText()
                else File(oraclePath).readText()

            if (json) {
                val actual = Json.parseToJsonElement(actualText)
                val expected = Json.parseToJsonElement(expectedText)
                Assertions.assertEquals(expected.jsonObject, actual.jsonObject)
            } else {
                Assertions.assertEquals(expectedText, actualText)
            }
        }

    private fun storeTest(url: String, request: JsonObject, oraclePath: String) =
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.post(url, request)
            Assertions.assertEquals(HttpStatus.OK_200, response.code)
            capabilitiesAssertEquals(
                File(oraclePath).gzipDecompress().readText(),
                response.body?.string() ?: ""
            )
        }

    private fun capabilitiesAssertEquals(expected: String, actual: String) {
        val actualCap = Json.decodeFromString<Capabilities>(actual)
        pushedCap = actualCap
        val expectedCap = Json.decodeFromString<Capabilities>(expected)

        // Override dynamic properties
        expectedCap.parserVersion = actualCap.parserVersion
        expectedCap.timestamp = actualCap.timestamp
        expectedCap.id = actualCap.id
        expectedCap.setMetadata(
            "processingTime",
            actualCap.getStringMetadata("processingTime") ?: ""
        )

        Assertions.assertEquals(expectedCap, actualCap)
    }

    private fun fileToBase64(path: String): String {
        return base64.encodeToString(File(path).gzipDecompress().use { it.readBytes() })
    }

    private fun deleteDirectory(path: String) {
        return Files.walk(Path.of(path))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}
