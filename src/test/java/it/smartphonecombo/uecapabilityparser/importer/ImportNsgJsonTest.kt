package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportNsgJson
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImportNsgJsonTest {
    private val path = "src/test/resources/nsgJson/"

    // Json from Airscreen 2.5.9, device type qualcomm
    @Test
    fun testAirscreenQcom() {
        testNsgJson("$path/input/airscreenQcom.json", "$path/oracle/airscreenQcom.json")
    }

    // Json from NSG 4.6.26, device type exynos
    @Test
    fun testNsgExy() {
        testNsgJson("$path/input/nsgExy.json", "$path/oracle/nsgExy.json")
    }

    // Json handcrafted without useful data
    @Test
    fun testEmpty() {
        testNsgJson("$path/input/empty.json", "$path/oracle/empty.json")
    }

    private fun testNsgJson(path: String, oracle: String) {
        val multi = ImportNsgJson.parse(File(path).toInputSource())

        val actual = multi?.parsingList?.map { it.capabilities }!!

        val expected = Json.decodeFromString<List<Capabilities>>(File(oracle).readText())

        // Check size
        Assertions.assertEquals(expected.size, actual.size)

        // override dynamic properties
        for (i in expected.indices) {
            val capA = actual[i]
            val capE = expected[i]

            capE.setMetadata("processingTime", capA.getStringMetadata("processingTime") ?: "")
        }

        Assertions.assertEquals(expected, actual)
    }
}
