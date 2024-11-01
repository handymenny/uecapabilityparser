package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportLteCarrierPolicyTest :
    AbstractImportCapabilities(ImportLteCarrierPolicy, "src/test/resources/carrierPolicy/") {
    @Test
    fun parseNoMimo() {
        parse("noMimo.xml", "noMimo.json")
    }

    @Test
    fun parseMimoBcsAll() {
        parse("mimoBcsAll.xml", "mimoBcsAll.json")
    }

    @Test
    fun parseMimoMultiBcs() {
        parse("mimoMultiBcs.xml", "mimoMultiBcs.json")
    }

    @Test
    fun parseMimoMultiBcsAbove9() {
        parse("mimoMultiBcsAbove9.xml", "mimoMultiBcsAbove9.json")
    }

    @Test
    fun parseFullCarrierPolicy() {
        parse("fullCarrierPolicy.xml", "fullCarrierPolicy.json")
    }

    @Test
    fun parseLteCapPrune() {
        parse("lteCapPrune.txt", "lteCapPrune.json")
    }

    @Test
    fun parseLteCapPruneWithSpaces() {
        parse("lteCapPruneWithSpaces.txt", "lteCapPruneWithSpaces.json")
    }
}
