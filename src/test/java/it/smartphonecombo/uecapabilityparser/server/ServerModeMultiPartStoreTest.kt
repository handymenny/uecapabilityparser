package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.UtilityForTests.multiPartRequest
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.index.MultiIndexLine
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeMultiPartStoreTest {
    private val resourcesPath = "src/test/resources/server"
    private val endpointParse = arrayOf("/parse/multiPart/", "/parse/multiPart").random()
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
                        putJsonArray("inputIndexes") {
                            add(0)
                            add(1)
                            add(2)
                        }
                        putJsonArray("subTypes") {
                            add("LTE")
                            add("NR")
                            add("ENDC")
                        }
                        put("description", "This is a multi")
                    }
                    addJsonObject {
                        put("type", "QLTE")
                        put(
                            "inputIndexes",
                            buildJsonArray { add(3) },
                        )
                        put("description", "This is a multi test")
                    }
                    addJsonObject {
                        put("type", "QNR")
                        put(
                            "inputIndexes",
                            buildJsonArray { add(4) },
                        )
                        put("description", "This is a multi-test")
                    }
                },
            files = oracleInputs.flatten(),
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

    private fun storeTest(
        url: String,
        request: JsonElement,
        files: List<String>,
        oraclePath: String
    ) =
        JavalinTest.test(JavalinApp().app) { _, client ->
            val response = client.request(multiPartRequest(client.origin + url, request, files))
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val result = response.body?.string() ?: ""
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
        expectedCap.setMetadata(
            "processingTime",
            actualCap.getStringMetadata("processingTime") ?: "",
        )

        Assertions.assertEquals(expectedCap, actualCap)
    }

    private fun multiIndexAssertEquals(expected: String, actual: String) {
        val actualObj = Json.decodeFromString<MultiIndexLine>(actual)
        val expectedObj = Json.decodeFromString<MultiIndexLine>(expected)

        Assertions.assertEquals(expectedObj.compressed, actualObj.compressed)
        Assertions.assertEquals(expectedObj.description, actualObj.description)
        Assertions.assertEquals(expectedObj.indexLineIds.size, actualObj.indexLineIds.size)
    }

    private fun deleteDirectory(path: String) {
        return Files.walk(Paths.get(path))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}
