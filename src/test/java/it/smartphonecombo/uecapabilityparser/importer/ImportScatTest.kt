package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.scat.ScatLogType
import it.smartphonecombo.uecapabilityparser.util.IO
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ImportScatTest {
    private val path = "src/test/resources/scat/"

    @Test
    fun testDLF() {
        testLog(ScatLogType.DLF, "$path/input/uecap.dlf", "$path/oracle/uecap.dlf.json")
    }

    @Test
    fun testQMDL() {
        testLog(ScatLogType.QMDL, "$path/input/hwcombos.qmdl", "$path/oracle/hwcombos.qmdl.json")
    }

    @Test
    fun testHDF() {
        testLog(ScatLogType.HDF, "$path/input/hwcombos.hdf", "$path/oracle/hwcombos.hdf.json")
    }

    @Test
    fun testSDM() {
        testLog(ScatLogType.SDM, "$path/input/uecap.sdm", "$path/oracle/uecap.sdm.json")
    }

    private fun testLog(scatLogType: ScatLogType, path: String, oracle: String) {
        val multi = ImportScat.parse(File(path).inputStream(), scatLogType)

        val actual = multi?.parsingList?.map { it.capabilities }!!
        val expected =
            Json.decodeFromString<List<Capabilities>>(IO.readTextFromFile(oracle, false)!!)

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

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // Skip tests if scat isn't available
            Assumptions.assumeTrue(UtilityForTests.scatIsAvailable())
        }
    }
}
