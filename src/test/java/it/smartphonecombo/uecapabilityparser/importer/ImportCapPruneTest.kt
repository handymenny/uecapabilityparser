package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportCapPruneTest :
    AbstractImportCapabilities(ImportNrCapPrune, "src/test/resources/nrCapPrune/") {
    @Test
    fun parseNoMimoFR1() {
        parse("noMimoFR1.txt", "noMimoFR1.json")
    }

    @Test
    fun parseNoMimoFR2() {
        parse("noMimoFR2.txt", "noMimoFR2.json")
    }

    @Test
    fun parseMimoFR1() {
        parse("mimoFR1.txt", "mimoFR1.json")
    }

    @Test
    fun parseMimoFR2() {
        parse("mimoFR2.txt", "mimoFR2.json")
    }

    @Test
    fun parseMixedMimoFR1() {
        parse("mixedMimoFR1.txt", "mixedMimoFR1.json")
    }
}
