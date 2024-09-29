package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.UtilityForTests.multiPartRequest
import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import java.io.File
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

internal class ServerModeMultiPartParseTest {
    private val inputPath = "src/test/resources/cli/input/"
    private val oraclePath = "src/test/resources/server/oracleForMultiParse/"
    private val app = JavalinApp().newServer()
    private val endpoint = arrayOf("/parse/multiPart", "/parse/multiPart/").random()

    @Test
    fun carrierPolicy() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "C")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/carrierPolicy.xml"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/0xB0CD.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/0xB0CDMultiHex.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/mtkLte.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/nvItem.bin"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/0xB826Multi.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/nrCapPrune.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/qctModemCap.txt"),
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
                        putJsonArray("inputIndexes") {
                            add(0)
                            add(1)
                        }
                    }
                },
            files =
                listOf(
                    "$inputPath/wiresharkMrdcSplit_0.txt",
                    "$inputPath/wiresharkMrdcSplit_1.txt"
                ),
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
                        putJsonArray("inputIndexes") {
                            add(0)
                            add(1)
                        }
                    }
                },
            files = listOf("$inputPath/nsgMrdcSplit_0.txt", "$inputPath/nsgMrdcSplit_1.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/osixMrdc.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                        putJsonArray("subTypes") { add("LTE") }
                    }
                },
            files = listOf("$inputPath/ueCapHexEutra.hex"),
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
                        putJsonArray("inputIndexes") { add(0) }
                        putJsonArray("subTypes") { add("NR") }
                    }
                },
            files = listOf("$inputPath/ueCapHexNr.hex"),
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
                        putJsonArray("inputIndexes") {
                            add(0)
                            add(1)
                            add(2)
                        }
                        putJsonArray("subTypes") {
                            add("ENDC")
                            add("NR")
                            add("LTE")
                        }
                    }
                },
            files =
                listOf(
                    "$inputPath/ueCapHexMrdcSplit_eutra-nr.hex",
                    "$inputPath/ueCapHexMrdcSplit_nr.hex",
                    "$inputPath/ueCapHexMrdcSplit_eutra.hex"
                ),
            oraclePath = "$oraclePath/ueCapHexMrdcSplit.json",
        )
    }

    @Test
    fun ueCapHexMrDcOnlyJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputIndexes") { add(0) }
                        putJsonArray("subTypes") { add("ENDC") }
                    }
                },
            files = listOf("$inputPath/ueCapHexMrdcSplit_eutra-nr.hex"),
            oraclePath = "$oraclePath/ueCapHexMrdcSplit_eutra_nr.json",
        )
    }

    @Test
    fun ueCapHexSegmentedJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "H")
                        putJsonArray("inputIndexes") { add(0) }
                        putJsonArray("subTypes") { add("LTE") }
                    }
                },
            files = listOf("$inputPath/ueCapHexSegmented.hex"),
            oraclePath = "$oraclePath/ueCapHexSegmented.json",
        )
    }

    @Test
    fun qcatNrdc() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "QC")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/qcatNrdc.txt"),
            oraclePath = "$oraclePath/qcatNrdc.json",
        )
    }

    @Test
    fun temsMrdcSplitJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "T")
                        putJsonArray("inputIndexes") {
                            add(0)
                            add(1)
                        }
                    }
                },
            files = listOf("$inputPath/temsMrdcSplit_0.txt", "$inputPath/temsMrdcSplit_1.txt"),
            oraclePath = "$oraclePath/temsMrdcSplit.json",
        )
    }

    @Test
    fun carrierPolicyAndNrCapPrune() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "C")
                        putJsonArray("inputIndexes") { add(1) }
                    }
                    addJsonObject {
                        put("type", "CNR")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/nrCapPrune.txt", "$inputPath/carrierPolicy.xml"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                    addJsonObject {
                        put("type", "QNR")
                        putJsonArray("inputIndexes") { add(1) }
                    }
                },
            files = listOf("$inputPath/0xB0CDMultiHex.txt", "$inputPath/0xB826Multi.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                    addJsonObject {
                        put("type", "RF")
                        putJsonArray("inputIndexes") { add(1) }
                    }
                },
            files = listOf("$inputPath/nvItem.bin", "$inputPath/qctModemCap.txt"),
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
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/pcap.pcap"),
            oraclePath = "$oraclePath/pcap.json",
        )
    }

    @Test
    fun pcapSegmented() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "P")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                },
            files = listOf("$inputPath/segmented.pcap"),
            oraclePath = "$oraclePath/segmented.json",
        )
    }

    @Test
    fun scat() {
        Assumptions.assumeTrue(scatAvailable)
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "DLF")
                        putJsonArray("inputIndexes") { add(1) }
                        put("type", "SDM")
                        putJsonArray("inputIndexes") { add(0) }
                        put("type", "HDF")
                        putJsonArray("inputIndexes") { add(3) }
                        put("type", "QMDL")
                        putJsonArray("inputIndexes") { add(2) }
                    }
                },
            files =
                listOf(
                    "$inputPath/scat.sdm",
                    "$inputPath/scat.dlf",
                    "$inputPath/scat.qmdl",
                    "$inputPath/scat.hdf"
                ),
            oraclePath = "$oraclePath/scat.json",
        )
    }

    @Test
    fun nsg() {
        javalinJsonTest(
            request =
                buildJsonArray {
                    addJsonObject {
                        put("type", "NSG")
                        putJsonArray("inputIndexes") { add(0) }
                    }
                    addJsonObject {
                        put("type", "NSG")
                        putJsonArray("inputIndexes") { add(1) }
                    }
                },
            files = listOf("$inputPath/nsgExy.json", "$inputPath/airscreenQcom.json"),
            oraclePath = "$oraclePath/nsgJson.json",
        )
    }

    private fun javalinJsonTest(request: JsonElement, files: List<String>, oraclePath: String) =
        JavalinTest.test(app) { _, client ->
            val response =
                client.request(multiPartRequest(client.origin + endpoint, request, files))
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
                    actualCapability.getStringMetadata("processingTime") ?: "",
                )
            }
            expected.id = actual.id

            Assertions.assertEquals(expected, actual)
        }
}
