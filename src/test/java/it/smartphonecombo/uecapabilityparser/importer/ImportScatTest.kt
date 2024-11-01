package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.scatAvailable
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.model.LogType
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ImportScatTest :
    AbstractImportMultiCapabilities(ImportScat, "src/test/resources/scat/") {
    @Test
    fun testDLF() {
        parse("uecap.dlf", "uecap.dlf.json", LogType.DLF)
    }

    @Test
    fun testQMDL() {
        parse("hwcombos.qmdl", "hwcombos.qmdl.json", LogType.QMDL)
    }

    @Test
    fun testHDF() {
        parse("hwcombos.hdf", "hwcombos.hdf.json", LogType.HDF)
    }

    @Test
    fun testSDM() {
        parse("uecap.sdm", "uecap.sdm.json", LogType.SDM)
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
