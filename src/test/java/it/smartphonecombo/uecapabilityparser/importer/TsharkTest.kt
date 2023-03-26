package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.bean.Rat
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Tshark
import it.smartphonecombo.uecapabilityparser.util.Utility
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
            if (Utility.osType == Utility.OsTypes.WINDOWS) {
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
        val ueCapability = tshark.startDecoder(hexData, "lte-rrc.ul.dcch", ratType)
        val expectedUeCapability = File(oraclePath).readText()

        Assertions.assertEquals(ueCapability, expectedUeCapability)
    }

    @Test
    fun parseUeCapabilityInformation() {
        parse("UeCapabilityInformation.hex", "UeCapabilityInformation.txt", Rat.eutra)
    }

    @Test
    fun parseUeEutraCapability() {
        parse("UeEutraCapability.hex", "UeEutraCapability.txt", Rat.eutra)
    }

    @Test
    fun parseUeMrdcCapability() {
        parse("UeMrdcCapability.hex", "UeMrdcCapability.txt", Rat.eutra_nr)
    }

    @Test
    fun parseUeNrCapability() {
        parse("UeNrCapability.hex", "UeNrCapability.txt", Rat.nr)
    }
}
