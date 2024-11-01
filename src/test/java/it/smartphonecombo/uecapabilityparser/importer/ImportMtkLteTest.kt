package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportMtkLteTest :
    AbstractImportCapabilities(ImportMTKLte, "src/test/resources/mtkLte/") {
    @Test
    fun parsePreCaCombInfo() {
        parse("PRE_CA_COMB_INFO.txt", "PRE_CA_COMB_INFO.json")
    }

    @Test
    fun parseUeCaCombInfo() {
        parse("UE_CA_COMB_INFO.txt", "UE_CA_COMB_INFO.json")
    }

    @Test
    fun parseReversed() {
        parse("REVERSED.txt", "REVERSED.json")
    }
}
