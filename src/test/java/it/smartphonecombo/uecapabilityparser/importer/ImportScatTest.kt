package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.io.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
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
        testLog(LogType.DLF, "$path/input/uecap.dlf", "$path/oracle/uecap.dlf.json")
    }

    @Test
    fun testQMDL() {
        testLog(LogType.QMDL, "$path/input/hwcombos.qmdl", "$path/oracle/hwcombos.qmdl.json")
    }

    @Test
    fun testHDF() {
        testLog(LogType.HDF, "$path/input/hwcombos.hdf", "$path/oracle/hwcombos.hdf.json")
    }

    @Test
    fun testSDM() {
        testLog(LogType.SDM, "$path/input/uecap.sdm", "$path/oracle/uecap.sdm.json")
    }

    private fun testLog(scatLogType: LogType, path: String, oracle: String) {
        val multi = ImportScat.parse(File(path).toInputSource(), scatLogType)

        val actual = multi?.parsingList?.map { it.capabilities }!!
        val expected = Json.decodeFromString<List<Capabilities>>(File(oracle).readText())

        // Check size
        Assertions.assertEquals(expected.size, actual.size)

        // override dynamic properties
        for (i in expected.indices) {
            val capA = actual[i]
            val capE = expected[i]

            capE.setMetadata("processingTime", capA.getStringMetadata("processingTime") ?: "")
            capE.setMetadata("description", capA.getStringMetadata("description") ?: "")
        }

        Assertions.assertEquals(expected, actual)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // Skip tests if scat isn't available
            Assumptions.assumeTrue(scatAvailable)
        }
    }
}
