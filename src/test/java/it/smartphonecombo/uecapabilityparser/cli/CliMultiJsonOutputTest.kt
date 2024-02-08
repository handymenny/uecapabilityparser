package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.testing.test
import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test

internal class CliMultiJsonOutputTest {
    private val path = "src/test/resources/cli"

    @Test
    fun testPcap() {
        test(
            "-i",
            "$path/input/pcap.pcap",
            "-t",
            "P",
            "-j",
            "-",
            "--json-pretty-print",
            oracleFilename = "pcap.json"
        )
    }

    @Test
    fun testMultiInput() {
        test(
            "-i",
            "$path/input/0xB826.hex",
            "-t",
            "QNR",
            "-i",
            "$path/input/0xB0CD.txt",
            "-t",
            "Q",
            "-j",
            "-",
            oracleFilename = "0xB826-0xB0CD.json"
        )
    }

    @Test
    fun testMultiScat() {
        Assumptions.assumeTrue(scatAvailable)
        test(
            "-i",
            "$path/input/scat.dlf",
            "-t",
            "DLF",
            "-i",
            "$path/input/scat.sdm",
            "-t",
            "SDM",
            "-i",
            "$path/input/scat.hdf",
            "-t",
            "HDF",
            "-i",
            "$path/input/scat.qmdl",
            "-t",
            "QMDL",
            "-j",
            "-",
            oracleFilename = "scat.json"
        )
    }

    private fun test(vararg args: String, oracleFilename: String) {
        val oraclePath = "$path/oracleMultiJson/$oracleFilename"

        val result = Cli.test(*args)
        val actual = Json.decodeFromString<Array<Capabilities>>(result.stdout)
        val expected = Json.decodeFromString<Array<Capabilities>>(File(oraclePath).readText())

        // Check size
        Assertions.assertEquals(expected.size, actual.size)

        // Override dynamic properties

        for (i in expected.indices) {
            val capA = actual[i]
            val capE = expected[i]

            capE.setMetadata("processingTime", capA.getStringMetadata("processingTime") ?: "")
        }

        // check files
        Assertions.assertArrayEquals(expected, actual)
    }
}
