package it.smartphonecombo.uecapabilityparser.server

import io.javalin.testtools.JavalinTest
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ServerModeCsvTest {
    private val path = "src/test/resources/server"
    private val app = JavalinApp().app
    private val endpoint = arrayOf("/csv/0.1.0/", "/csv/", "/csv/0.1.0", "/csv").random()

    @Test
    fun lteCaCsvConversion() {
        javalinCsvTest(
            inputPath = "$path/inputForCsv/lteca.json",
            oraclePath = "$path/oracleForCsv/lteca.csv"
        )
    }

    @Test
    fun enDcCsvConversion() {
        javalinCsvTest(
            inputPath = "$path/inputForCsv/endc.json",
            oraclePath = "$path/oracleForCsv/endc.csv"
        )
    }

    @Test
    fun nrCaCsvConversion() {
        javalinCsvTest(
            inputPath = "$path/inputForCsv/nrca.json",
            oraclePath = "$path/oracleForCsv/nrca.csv"
        )
    }

    @Test
    fun nrDcCsvConversion() {
        javalinCsvTest(
            inputPath = "$path/inputForCsv/nrdc.json",
            oraclePath = "$path/oracleForCsv/nrdc.csv"
        )
    }

    private fun javalinCsvTest(inputPath: String, oraclePath: String) =
        JavalinTest.test(app) { _, client ->
            val request = Json.parseToJsonElement(File(inputPath).readText())
            val response = client.post(endpoint, request)
            Assertions.assertEquals(HttpStatus.OK_200, response.code)

            val actual =
                response.body?.string()?.lines()?.dropLastWhile { it.isBlank() } ?: emptyList()
            val expected = File(oraclePath).readLines().dropLastWhile { it.isBlank() }

            // check input combos size = output combos size
            Assertions.assertEquals(request.jsonObject["input"]?.jsonArray?.size, actual.size - 1)
            Assertions.assertLinesMatch(expected, actual)
        }
}
