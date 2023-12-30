package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import java.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// This uses the same inputs and oracles of CliJsonOutputTest
internal class ServerModeParseTest {
    private val path = "src/test/resources/cli"
    private val app = JavalinApp().app
    private val base64 = Base64.getEncoder()
    private val endpoint = arrayOf("/parse/", "/parse").random()

    @Test
    fun carrierPolicyJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "C")
                    put("input", fileToBase64("$path/input/carrierPolicy.xml"))
                },
            oraclePath = "$path/oracleJson/carrierPolicy.json"
        )
    }

    @Test
    fun b0CDJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "Q")
                    put("input", fileToBase64("$path/input/0xB0CD.txt"))
                },
            oraclePath = "$path/oracleJson/0xB0CD.json"
        )
    }

    @Test
    fun b0CDMultiHexJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QLTE")
                    put("input", fileToBase64("$path/input/0xB0CDMultiHex.txt"))
                },
            oraclePath = "$path/oracleJson/0xB0CDMultiHex.json"
        )
    }

    @Test
    fun mtkLteJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "M")
                    put("input", fileToBase64("$path/input/mtkLte.txt"))
                },
            oraclePath = "$path/oracleJson/mtkLte.json"
        )
    }

    @Test
    fun nvItemJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "E")
                    put("input", fileToBase64("$path/input/nvItem.bin"))
                },
            oraclePath = "$path/oracleJson/nvItem.json"
        )
    }

    @Test
    fun b826JsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QNR")
                    put("input", fileToBase64("$path/input/0xB826.hex"))
                },
            oraclePath = "$path/oracleJson/0xB826.json"
        )
    }

    @Test
    fun b826MultiJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QNR")
                    put("input", fileToBase64("$path/input/0xB826Multi.txt"))
                },
            oraclePath = "$path/oracleJson/0xB826Multi.json"
        )
    }

    @Test
    fun b826MultiV14JsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QNR")
                    put("input", fileToBase64("$path/input/0xB826MultiV14.txt"))
                },
            oraclePath = "$path/oracleJson/0xB826MultiV14.json"
        )
    }

    @Test
    fun b826MultiScatJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QNR")
                    put("input", fileToBase64("$path/input/0xB826MultiScat.txt"))
                },
            oraclePath = "$path/oracleJson/0xB826MultiScat.json"
        )
    }

    @Test
    fun b826Multi0x980JsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QNR")
                    put("input", fileToBase64("$path/input/0xB826Multi0x9801.txt"))
                },
            oraclePath = "$path/oracleJson/0xB826Multi0x9801.json"
        )
    }

    @Test
    fun nrCapPruneJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "CNR")
                    put("input", fileToBase64("$path/input/nrCapPrune.txt"))
                },
            oraclePath = "$path/oracleJson/nrCapPrune.json"
        )
    }

    @Test
    fun qctModemCapJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "RF")
                    put("input", fileToBase64("$path/input/qctModemCap.txt"))
                },
            oraclePath = "$path/oracleJson/qctModemCap.json"
        )
    }

    @Test
    fun shannonNrUeCapJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "SHNR")
                    put("input", fileToBase64("$path/input/shannonNrUeCap.binarypb"))
                },
            oraclePath = "$path/oracleJson/shannonNrUeCap.json"
        )
    }

    @Test
    fun wiresharkEutraJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "W")
                    put("input", fileToBase64("$path/input/wiresharkEutra.txt"))
                },
            oraclePath = "$path/oracleJson/wiresharkEutra.json"
        )
    }

    @Test
    fun wiresharkNrJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "W")
                    put("input", fileToBase64("$path/input/wiresharkNr.txt"))
                },
            oraclePath = "$path/oracleJson/wiresharkNr.json"
        )
    }

    @Test
    fun wiresharkMrdcJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "W")
                    put("input", fileToBase64("$path/input/wiresharkMrdc.txt"))
                },
            oraclePath = "$path/oracleJson/wiresharkMrdc.json"
        )
    }

    @Test
    fun wiresharkMrdcSplitJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "W")
                    put("input", fileToBase64("$path/input/wiresharkMrdcSplit_0.txt"))
                    put("inputENDC", fileToBase64("$path/input/wiresharkMrdcSplit_1.txt"))
                },
            oraclePath = "$path/oracleJson/wiresharkMrdcSplit.json"
        )
    }

    @Test
    fun nsgEutraJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "N")
                    put("input", fileToBase64("$path/input/nsgEutra.txt"))
                },
            oraclePath = "$path/oracleJson/nsgEutra.json"
        )
    }

    @Test
    fun nsgNrJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "N")
                    put("input", fileToBase64("$path/input/nsgNr.txt"))
                },
            oraclePath = "$path/oracleJson/nsgNr.json"
        )
    }

    @Test
    fun nsgMrdcJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "N")
                    put("input", fileToBase64("$path/input/nsgMrdc.txt"))
                },
            oraclePath = "$path/oracleJson/nsgMrdc.json"
        )
    }

    @Test
    fun nsgMrdcSplitJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "N")
                    put("input", fileToBase64("$path/input/nsgMrdcSplit_0.txt"))
                    put("inputNR", fileToBase64("$path/input/nsgMrdcSplit_1.txt"))
                },
            oraclePath = "$path/oracleJson/nsgMrdcSplit.json"
        )
    }

    @Test
    fun nsgSulJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "N")
                    put("input", fileToBase64("$path/input/nsgSul.txt"))
                },
            oraclePath = "$path/oracleJson/nsgSul.json"
        )
    }

    @Test
    fun osixMrdcJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "O")
                    put("input", fileToBase64("$path/input/osixMrdc.txt"))
                },
            oraclePath = "$path/oracleJson/osixMrdc.json"
        )
    }

    @Test
    fun ueCapHexEutraJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "H")
                    put("input", fileToBase64("$path/input/ueCapHexEutra.hex"))
                },
            oraclePath = "$path/oracleJson/ueCapHexEutra.json"
        )
    }

    @Test
    fun ueCapHexNrJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "H")
                    put("inputNR", fileToBase64("$path/input/ueCapHexNr.hex"))
                },
            oraclePath = "$path/oracleJson/ueCapHexNr.json"
        )
    }

    @Test
    fun ueCapHexDefaultNrJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "H")
                    put("input", fileToBase64("$path/input/ueCapHexNr.hex"))
                    put("defaultNR", true)
                },
            oraclePath = "$path/oracleJson/ueCapHexNr.json"
        )
    }

    @Test
    fun ueCapHexMrdcSplitJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "H")
                    put("input", fileToBase64("$path/input/ueCapHexMrdcSplit_eutra.hex"))
                    put("inputNR", fileToBase64("$path/input/ueCapHexMrdcSplit_nr.hex"))
                    put("inputENDC", fileToBase64("$path/input/ueCapHexMrdcSplit_eutra-nr.hex"))
                },
            oraclePath = "$path/oracleJson/ueCapHexMrdcSplit.json"
        )
    }

    @Test
    fun qcatMrdcJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QC")
                    put("input", fileToBase64("$path/input/qcatMrdc.txt"))
                },
            oraclePath = "$path/oracleJson/qcatMrdc.json"
        )
    }

    @Test
    fun qcatNrdcJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "QC")
                    put("input", fileToBase64("$path/input/qcatNrdc.txt"))
                },
            oraclePath = "$path/oracleJson/qcatNrdc.json"
        )
    }

    @Test
    fun temsEutraJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "T")
                    put("input", fileToBase64("$path/input/temsEutra.txt"))
                },
            oraclePath = "$path/oracleJson/temsEutra.json"
        )
    }

    @Test
    fun temsMrdcSplitJsonOutput() {
        javalinJsonTest(
            request =
                buildJsonObject {
                    put("type", "T")
                    put("input", fileToBase64("$path/input/temsMrdcSplit_0.txt"))
                    put("inputNR", fileToBase64("$path/input/temsMrdcSplit_1.txt"))
                },
            oraclePath = "$path/oracleJson/temsMrdcSplit.json"
        )
    }

    @Test
    fun amarisoftEutraJsonOutput() {
        javalinJsonTest(
            request =
            buildJsonObject {
                put("type", "A")
                put("input", fileToBase64("$path/input/amarisoftEutra.txt"))
            },
            oraclePath = "$path/oracleJson/amarisoftEutra.json"
        )
    }

    @Test
    fun amarisoftNrJsonOutput() {
        javalinJsonTest(
            request =
            buildJsonObject {
                put("type", "A")
                put("input", fileToBase64("$path/input/amarisoftNr.txt"))
            },
            oraclePath = "$path/oracleJson/amarisoftNr.json"
        )
    }

    private fun javalinJsonTest(request: JsonObject, oraclePath: String) =
        JavalinTest.test(app) { _, client ->
            val response = client.post(endpoint, request)
            Assertions.assertEquals(HttpStatus.OK.code, response.code)

            val actual = Json.custom().decodeFromString<Capabilities>(response.body?.string() ?: "")
            val expected = Json.custom().decodeFromString<Capabilities>(File(oraclePath).readText())

            // Override dynamic properties
            expected.setMetadata("processingTime", actual.getStringMetadata("processingTime") ?: "")

            Assertions.assertEquals(expected, actual)
        }

    private fun fileToBase64(path: String): String {
        return base64.encodeToString(File(path).readBytes())
    }
}
