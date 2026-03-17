package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportMtkNrTest :
    AbstractImportCapabilities(ImportMtkNr, "src/test/resources/mtkNr/") {

    @Test
    fun parseMtkNrTrace() {
        parse("mtkNrTrace.txt", "mtkNrTrace.json")
    }

    @Test
    fun parseMtkNrTraceOld() {
        parse("mtkNrTraceOld.txt", "mtkNrTraceOld.json")
    }
}
