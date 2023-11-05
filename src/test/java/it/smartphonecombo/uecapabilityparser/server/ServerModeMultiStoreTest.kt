package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.index.MultiIndexLine
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeMultiStoreTest {
    private val resourcesPath = "src/test/resources/server"
    private val base64 = Base64.getEncoder()
    private val endpointParse = arrayOf("/parse/multi/", "/parse/multi").random()
    private val endpointStore = "/store/"
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val storedIds =
        arrayOf(
            "9ed9c3fa-9cec-4a44-a6e8-cef8341e8cc3",
            "08924126-d42f-4425-a4f7-3778ac9a2633",
            "e2418064-be1d-42ae-9249-7c7d5373b752",
        )
    private val storedMultiId = "5a41ca3f-3bd1-469b-930a-97ab7c227807"

    companion object {
        private var pushedCaps: MultiCapabilities? = null
    }

    @BeforeEach
    fun setup() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        Config.remove("store")
    }

    @AfterEach
    fun teardown() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        Config.remove("store")
    }

    @Test
    fun storeMultiElement() {
        Config["store"] = tmpStorePath
        val inputPrefix = "$resourcesPath/oracleForMultiStore/input/"

        val outputOracles =
            storedIds.map { storedId -> "$resourcesPath/oracleForMultiStore/output/$storedId.json" }
        val multiOracle = "$resourcesPath/oracleForMultiStore/multi/$storedMultiId.json"

        val oracleInputs =
            arrayOf(
                arrayOf(
                    "$inputPrefix${storedIds[0]}-0",
                    "$inputPrefix${storedIds[0]}-1",
                    "$inputPrefix${storedIds[0]}-2",
                ),
                arrayOf("$inputPrefix${storedIds[1]}-0"),
                arrayOf("$inputPrefix${storedIds[2]}-0"),
            )

        storeTest(
            url = endpointParse,
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputs") {
                            add(fileToBase64(oracleInputs[0][0]))
                            add(fileToBase64(oracleInputs[0][1]))
                            add(fileToBase64(oracleInputs[0][2]))
                        }
                        putJsonArray("subTypes") {
                            add("LTE")
                            add("NR")
                            add("ENDC")
                        }
                        put("description", "This is a multi-test")
                    }
                    addJsonObject {
                        put("type", "QLTE")
                        put(
                            "inputs",
                            buildJsonArray { add(fileToBase64(oracleInputs[1][0])) },
                        )
                        put("description", "This is a multi-test")
                    }
                    addJsonObject {
                        put("type", "QNR")
                        put(
                            "inputs",
                            buildJsonArray { add(fileToBase64(oracleInputs[2][0])) },
                        )
                        put("description", "This is a multi-test")
                    }
                },
            oraclePath = "$resourcesPath/oracleForMultiStore/multiParseOutput.json",
        )

        assumeTrue(pushedCaps != null)
        val caps = pushedCaps!!
        for (i in caps.capabilities.indices) {
            val cap = caps.capabilities[i]
            val id = cap.id
            val output = outputOracles[i]
            val inputs = oracleInputs[i]

            capabilitiesAssertEquals(
                File(output).readText(),
                File("$tmpStorePath/output/$id.json").readText(),
            )
            for (j in inputs.indices) {
                Assertions.assertLinesMatch(
                    File(inputs[j]).readLines(),
                    File("$tmpStorePath/input/$id-$j").readLines(),
                )
            }
        }
        multiIndexAssertEquals(
            File(multiOracle).readText(),
            File("$tmpStorePath/multi/${caps.id}.json").readText(),
        )
    }

    @Test
    fun listWithMultiElements() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(endpointStore + "list", "$resourcesPath/oracleForMultiStore/listMultiElem.json")
    }

    @Test
    fun getItem() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(
            "${endpointStore}getItem?id=${storedIds[1]}",
            "$resourcesPath/oracleForMultiStore/item.json",
        )
    }

    @Test
    fun getMultiElement() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(
            "${endpointStore}getMultiItem?id=${storedMultiId}",
            "$resourcesPath/oracleForMultiStore/multi/${storedMultiId}.json"
        )
    }

    @Test
    fun getOutput() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(
            "${endpointStore}getOutput?id=${storedIds[2]}",
            "$resourcesPath/oracleForMultiStore/output/${storedIds[2]}.json",
        )
    }

    @Test
    fun getInput() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(
            "${endpointStore}getInput?id=${storedIds[0]}-1",
            "$resourcesPath/oracleForMultiStore/input/${storedIds[0]}-1",
            false,
        )
    }

    @Test
    fun getMultiOutput() {
        Config["store"] = "$resourcesPath/oracleForMultiStore"
        getTest(
            "${endpointStore}getMultiOutput?id=${storedMultiId}",
            "$resourcesPath/oracleForMultiStore/multiParseOutput.json",
        )
    }

    private fun getTest(url: String, oraclePath: String, json: Boolean = true) =
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.get(url)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val actualText = response.body?.string() ?: ""
            // IO.outputFileOrStdout(actualText, oraclePath)
            val expectedText = File(oraclePath).readText()

            if (json) {
                val actual = Json.parseToJsonElement(actualText)
                val expected = Json.parseToJsonElement(expectedText)

                Assertions.assertEquals(expected.jsonObject, actual.jsonObject)
            } else {
                Assertions.assertEquals(expectedText, actualText)
            }
        }

    private fun storeTest(url: String, request: JsonElement, oraclePath: String) =
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.post(url, request)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val result = response.body?.string() ?: ""
            // IO.outputFileOrStdout(result, oraclePath)
            multiCapabilitiesAssertEquals(File(oraclePath).readText(), result)
        }

    private fun multiCapabilitiesAssertEquals(expected: String, actual: String) {
        val actualCaps = Json.decodeFromString<MultiCapabilities>(actual)
        pushedCaps = actualCaps
        val expectedCaps = Json.decodeFromString<MultiCapabilities>(expected)

        // size check
        Assertions.assertEquals(expectedCaps.capabilities.size, actualCaps.capabilities.size)

        for (i in expectedCaps.capabilities.indices) {
            val expectedCap = expectedCaps.capabilities[i]
            val actualCap = actualCaps.capabilities[i]

            // Override dynamic properties
            expectedCap.parserVersion = actualCap.parserVersion
            expectedCap.timestamp = actualCap.timestamp
            expectedCap.id = actualCap.id
            expectedCap.setMetadata(
                "processingTime",
                actualCap.getStringMetadata("processingTime") ?: "",
            )

            Assertions.assertEquals(expectedCap, actualCap)
        }
    }

    private fun capabilitiesAssertEquals(expected: String, actual: String) {
        val actualCap = Json.decodeFromString<Capabilities>(actual)
        val expectedCap = Json.decodeFromString<Capabilities>(expected)

        // Override dynamic properties
        expectedCap.parserVersion = actualCap.parserVersion
        expectedCap.timestamp = actualCap.timestamp
        expectedCap.id = actualCap.id
        expectedCap.setMetadata(
            "processingTime",
            actualCap.getStringMetadata("processingTime") ?: "",
        )

        Assertions.assertEquals(expectedCap, actualCap)
    }

    private fun multiIndexAssertEquals(expected: String, actual: String) {
        val actualObj = Json.decodeFromString<MultiIndexLine>(actual)
        val expectedObj = Json.decodeFromString<MultiIndexLine>(expected)

        Assertions.assertEquals(actualObj.compressed, expectedObj.compressed)
        Assertions.assertEquals(actualObj.description, expectedObj.description)
        Assertions.assertEquals(actualObj.indexLineIds.size, expectedObj.indexLineIds.size)
    }

    private fun fileToBase64(path: String): String {
        return base64.encodeToString(File(path).readBytes())
    }

    private fun deleteDirectory(path: String) {
        return Files.walk(Path.of(path))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}
