package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.UtilityForTests.capabilitiesAssertEquals
import it.smartphonecombo.uecapabilityparser.UtilityForTests.multiPartRequest
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeStoreTest {
    private val resourcesPath = "src/test/resources/server"
    private val endpointParse = arrayOf("/parse/multiPart/", "/parse/multiPart").random()
    private val endpointStore = "/store/"
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val storedId = "65bafa64-2e00-4525-a277-5f1d71992efb"

    companion object {
        private var pushedCap: Capabilities? = null
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
    fun emptyList() {
        Config["store"] = tmpStorePath
        Config["cache"] = "0"
        getTest(endpointStore + "list", "$resourcesPath/oracleForStore/emptyList.json")
    }

    @Test
    fun storeElement() {
        Config["store"] = tmpStorePath
        Config["cache"] = "-1"
        val oracle = "$resourcesPath/oracleForStore/output/$storedId.json"
        val input0 = "$resourcesPath/oracleForStore/input/$storedId-0"
        val input1 = "$resourcesPath/oracleForStore/input/$storedId-1"
        val input2 = "$resourcesPath/oracleForStore/input/$storedId-2"

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
                        put("description", "This is a test")
                    }
                },
            files = listOf(input0, input1, input2),
            oraclePath = oracle,
        )

        assumeTrue(pushedCap != null)
        val id = pushedCap?.id ?: ""
        capabilitiesAssertEquals(oracle, File("$tmpStorePath/output/$id.json").readText())
        Assertions.assertLinesMatch(
            File(input0).readLines(),
            File("$tmpStorePath/input/$id-0").readLines(),
        )
        Assertions.assertLinesMatch(
            File(input1).readLines(),
            File("$tmpStorePath/input/$id-1").readLines(),
        )
        Assertions.assertLinesMatch(
            File(input2).readLines(),
            File("$tmpStorePath/input/$id-2").readLines(),
        )
    }

    @Test
    fun listWithOneElement() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTest(endpointStore + "list", "$resourcesPath/oracleForStore/list1Elem.json")
    }

    @Test
    fun getItem() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTest("${endpointStore}getItem?id=$storedId", "$resourcesPath/oracleForStore/item.json")
    }

    @Test
    fun getItemBadRequest() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError("${endpointStore}getItem", HttpStatus.BAD_REQUEST.code)
    }

    @Test
    fun getItem404() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError("${endpointStore}getItem?id=${UUID.randomUUID()}", HttpStatus.NOT_FOUND.code)
    }

    @Test
    fun getOutput() {
        Config["cache"] = "1000"
        Config["store"] = "$resourcesPath/oracleForStore"
        getTest(
            "${endpointStore}getOutput?id=$storedId",
            "$resourcesPath/oracleForStore/output/$storedId.json",
        )
    }

    @Test
    fun getOutputBadRequest() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError(
            "${endpointStore}getOutput?id=../output/$storedId",
            HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun getOutputBadRequest2() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError(
            "${endpointStore}getOutput?idd=../output/$storedId",
            HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun getOutput404() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError("${endpointStore}getOutput?id=${UUID.randomUUID()}", HttpStatus.NOT_FOUND.code)
    }

    @Test
    fun getInput() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTest(
            "${endpointStore}getInput?id=$storedId-0",
            "$resourcesPath/oracleForStore/input/$storedId-0",
            false,
        )
    }

    @Test
    fun getInputBadRequest() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError(
            "${endpointStore}getInput?id=../input/$storedId-0",
            HttpStatus.BAD_REQUEST.code,
        )
    }

    @Test
    fun getInputBadRequest2() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError("${endpointStore}getInput?", HttpStatus.BAD_REQUEST.code)
    }

    @Test
    fun getInput404() {
        Config["store"] = "$resourcesPath/oracleForStore"
        getTestError("${endpointStore}getInput?id=$storedId-4", HttpStatus.NOT_FOUND.code)
    }

    private fun getTest(url: String, oraclePath: String, json: Boolean = true) =
        JavalinTest.test(JavalinApp().newServer()) { _, client ->
            val response = client.get(url)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            val actualText = response.body?.string() ?: ""
            val expectedText = File(oraclePath).readText()

            if (json) {
                val actual = Json.parseToJsonElement(actualText)
                val expected = Json.parseToJsonElement(expectedText)
                Assertions.assertEquals(expected.jsonObject, actual.jsonObject)
            } else {
                Assertions.assertEquals(expectedText, actualText)
            }
        }

    private fun getTestError(url: String, statusCode: Int) =
        JavalinTest.test(JavalinApp().newServer()) { _, client ->
            val response = client.get(url)
            Assertions.assertEquals(statusCode, response.code)
        }

    private fun storeTest(
        url: String,
        request: JsonArray,
        files: List<String>,
        oraclePath: String,
    ) =
        JavalinTest.test(JavalinApp().newServer()) { _, client ->
            val response = client.request(multiPartRequest(client.origin + url, request, files))
            Assertions.assertEquals(HttpStatus.OK.code, response.code)
            pushedCap = capabilitiesAssertEquals(oraclePath, response.body?.string() ?: "", true)
        }

    private fun deleteDirectory(path: String) {
        return Files.walk(Paths.get(path))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}
