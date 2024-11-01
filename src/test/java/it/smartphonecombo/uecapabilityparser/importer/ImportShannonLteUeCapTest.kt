package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportShannonLteUeCapTest :
    AbstractImportCapabilities(
        ImportShannonLteUeCap,
        "src/test/resources/shannon/",
        "oracleForImport",
    ) {
    @Test
    fun parseLte() {
        parse("lte.binarypb", "lte.json")
    }

    @Test
    fun parseLte2() {
        parse("lte2.binarypb", "lte2.json")
    }
}
