package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.OsType
import it.smartphonecombo.uecapabilityparser.util.Tshark
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class TsharkTest {
    companion object {
        private val config: Config = Config
        private val tshark = Tshark()

        private fun detectTsharkPath() {
            if (OsType.CURRENT == OsType.WINDOWS) {
                val binary = "tshark.exe"
                val binFolder = "Wireshark"
                val programFiles =
                    arrayOf<String?>(
                        System.getenv("ProgramFiles"),
                        System.getenv("ProgramFiles(x86)"),
                        System.getenv("ProgramW6432"),
                    )

                programFiles.filterNotNull().forEach { programFile ->
                    if (File("$programFile/$binFolder/$binary").exists()) {
                        config["TsharkPath"] = "$programFile/$binFolder/"
                        return@forEach
                    }
                }
            }
        }

        private fun tsharkIsAvailable(): Boolean {
            return try {
                val tsharkCmd = config.getOrDefault("TsharkPath", "") + "tshark"
                val process = Runtime.getRuntime().exec(arrayOf(tsharkCmd, "-v"))
                process.waitFor()
                process.exitValue() == 0
            } catch (ignored: Exception) {
                false
            }
        }

        @BeforeAll
        @JvmStatic
        fun setup() {
            detectTsharkPath()
            // Skip tests if tshark isn't available
            assumeTrue(tsharkIsAvailable())
        }
    }

    private fun parse(inputFilename: String, oracleFilename: String, ratType: Rat) {
        val path = "src/test/resources/tshark/"

        val inputPath = "$path/input/$inputFilename"
        val oraclePath = "$path/oracle/$oracleFilename"

        val hexData = File(inputPath).readText()
        val ueCapability = tshark.startDecoder(hexData, "lte-rrc.ul.dcch", ratType).lines()
        val expectedUeCapability = File(oraclePath).readLines()

        Assertions.assertLinesMatch(
            ueCapability.dropLastWhile { it.isBlank() },
            expectedUeCapability.dropLastWhile { it.isBlank() }
        )
    }

    @Test
    fun parseUeCapabilityInformation() {
        parse("UeCapabilityInformation.hex", "UeCapabilityInformation.txt", Rat.EUTRA)
    }

    @Test
    fun parseUeEutraCapability() {
        parse("UeEutraCapability.hex", "UeEutraCapability.txt", Rat.EUTRA)
    }

    @Test
    fun parseUeMrdcCapability() {
        parse("UeMrdcCapability.hex", "UeMrdcCapability.txt", Rat.EUTRA_NR)
    }

    @Test
    fun parseUeNrCapability() {
        parse("UeNrCapability.hex", "UeNrCapability.txt", Rat.NR)
    }
}
