package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.UtilityForTests
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import java.io.File
import java.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test

internal class ServerModeMultiParseTest {
    private val inputPath = "src/test/resources/mainCli/input/"
    private val oraclePath = "src/test/resources/server/oracleForMultiParse/"
    private val app = JavalinApp().app
    private val base64 = Base64.getEncoder()
    private val endpoint = arrayOf("/parse/multi", "/parse/multi/").random()

    @Test
    fun carrierPolicy() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "C")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/carrierPolicy.xml")) }
                    }
                },
            oraclePath = "$oraclePath/carrierPolicy.json",
        )
    }

    @Test
    fun b0CDText() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "Q")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/0xB0CD.txt")) }
                    }
                },
            oraclePath = "$oraclePath/0xB0CD.json",
        )
    }

    @Test
    fun b0CDMultiHex() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "QLTE")
                        putJsonArray("inputs") {
                            add(fileToBase64("$inputPath/0xB0CDMultiHex.txt"))
                        }
                    }
                },
            oraclePath = "$oraclePath/0xB0CDMultiHex.json",
        )
    }

    @Test
    fun mtkLte() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "M")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/mtkLte.txt")) }
                    }
                },
            oraclePath = "$oraclePath/mtkLte.json",
        )
    }

    @Test
    fun nvItem() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "E")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/nvItem.bin")) }
                    }
                },
            oraclePath = "$oraclePath/nvItem.json",
        )
    }

    @Test
    fun b826Multi() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "QNR")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/0xB826Multi.txt")) }
                    }
                },
            oraclePath = "$oraclePath/0xB826Multi.json",
        )
    }

    @Test
    fun nrCapPrune() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "CNR")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/nrCapPrune.txt")) }
                    }
                },
            oraclePath = "$oraclePath/nrCapPrune.json",
        )
    }

    @Test
    fun qctModemCap() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "RF")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/qctModemCap.txt")) }
                    }
                },
            oraclePath = "$oraclePath/qctModemCap.json",
        )
    }

    @Test
    fun wiresharkMrdcSplit() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "W")
                        putJsonArray("inputs") {
                            add(fileToBase64("$inputPath/wiresharkMrdcSplit_0.txt"))
                            add(fileToBase64("$inputPath/wiresharkMrdcSplit_1.txt"))
                        }
                    }
                },
            oraclePath = "$oraclePath/wiresharkMrdcSplit.json",
        )
    }

    @Test
    fun nsgMrdcSplit() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "N")
                        putJsonArray("inputs") {
                            add(fileToBase64("$inputPath/nsgMrdcSplit_0.txt"))
                            add(fileToBase64("$inputPath/nsgMrdcSplit_1.txt"))
                        }
                    }
                },
            oraclePath = "$oraclePath/nsgMrdcSplit.json",
        )
    }

    @Test
    fun osixMrdc() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "O")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/osixMrdc.txt")) }
                    }
                },
            oraclePath = "$oraclePath/osixMrdc.json",
        )
    }

    @Test
    fun ueCapHexEutra() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/ueCapHexEutra.hex")) }
                        putJsonArray("subTypes") { add("LTE") }
                    }
                },
            oraclePath = "$oraclePath/ueCapHexEutra.json",
        )
    }

    @Test
    fun ueCapHexNr() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/ueCapHexNr.hex")) }
                        putJsonArray("subTypes") { add("NR") }
                    }
                },
            oraclePath = "$oraclePath/ueCapHexNr.json",
        )
    }

    @Test
    fun ueCapHexMrdcSplit() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputs") {
                            add(fileToBase64("$inputPath/ueCapHexMrdcSplit_eutra-nr.hex"))
                            add(fileToBase64("$inputPath/ueCapHexMrdcSplit_nr.hex"))
                            add(fileToBase64("$inputPath/ueCapHexMrdcSplit_eutra.hex"))
                        }
                        putJsonArray("subTypes") {
                            add("ENDC")
                            add("NR")
                            add("LTE")
                        }
                    }
                },
            oraclePath = "$oraclePath/ueCapHexMrdcSplit.json",
        )
    }

    @Test
    fun qcatNrdc() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "QC")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/qcatNrdc.txt")) }
                    }
                },
            oraclePath = "$oraclePath/qcatNrdc.json",
        )
    }

    @Test
    fun carrierPolicyAndNrCapPrune() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "C")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/carrierPolicy.xml")) }
                    }
                    addJsonObject {
                        put("type", "CNR")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/nrCapPrune.txt")) }
                    }
                },
            oraclePath = "$oraclePath/carrierPolicyAndNrCapPrune.json",
        )
    }

    @Test
    fun b0CDMultiHexAndB826Multi() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "QLTE")
                        putJsonArray("inputs") {
                            add(fileToBase64("$inputPath/0xB0CDMultiHex.txt"))
                        }
                    }
                    addJsonObject {
                        put("type", "QNR")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/0xB826Multi.txt")) }
                    }
                },
            oraclePath = "$oraclePath/0xB0CDMultiHexAnd0xB826Multi.json",
        )
    }

    @Test
    fun nvItemQctModemCap() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "E")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/nvItem.bin")) }
                    }
                    addJsonObject {
                        put("type", "RF")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/qctModemCap.txt")) }
                    }
                },
            oraclePath = "$oraclePath/nvItemAndQctModemCap.json",
        )
    }

    @Test
    fun pcap() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "P")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/pcap.pcap")) }
                    }
                },
            oraclePath = "$oraclePath/pcap.json"
        )
    }

    @Test
    fun scat() {
        Assumptions.assumeTrue(UtilityForTests.scatIsAvailable())
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "DLF")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/scat.dlf")) }
                        put("type", "SDM")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/scat.sdm")) }
                        put("type", "HDF")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/scat.hdf")) }
                        put("type", "QMDL")
                        putJsonArray("inputs") { add(fileToBase64("$inputPath/scat.qmdl")) }
                    }
                },
            oraclePath = "$oraclePath/scat.json"
        )
    }

    private fun javalinJsonTest(request: JsonElement, oraclePath: String) =
        JavalinTest.test(app) { _, client ->
            val response = client.post(endpoint, request)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)

            val string = response.body?.string()
            val actual = Json.custom().decodeFromString<MultiCapabilities>(string ?: "")
            val expected =
                Json.custom().decodeFromString<MultiCapabilities>(File(oraclePath).readText())

            // Size check
            Assertions.assertEquals(expected.capabilities.size, actual.capabilities.size)

            // Override dynamic properties
            for (i in expected.capabilities.indices) {
                val actualCapability = actual.capabilities[i]
                val expectedCapability = expected.capabilities[i]

                expectedCapability.setMetadata(
                    "processingTime",
                    actualCapability.getStringMetadata("processingTime") ?: ""
                )
            }
            expected.id = actual.id

            Assertions.assertEquals(expected, actual)
        }

    private fun fileToBase64(path: String): String {
        return base64.encodeToString(File(path).readBytes())
    }
}
