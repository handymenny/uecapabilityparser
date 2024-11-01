package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportShannonNrUeCapTest :
    AbstractImportCapabilities(
        ImportShannonNrUeCap,
        "src/test/resources/shannon/",
        "oracleForImport",
    ) {
    @Test
    fun parseEmpty() {
        parse("empty.binarypb", "empty.json")
    }

    @Test
    fun parseSub6() {
        parse("sub6.binarypb", "sub6.json")
    }

    @Test
    fun parseSub62() {
        parse("sub6_2.binarypb", "sub6_2.json")
    }

    @Test
    fun parseMmWave() {
        parse("mmWave.binarypb", "mmWave.json")
    }

    @Test
    fun parseMmWave2() {
        parse("mmWave_2.binarypb", "mmWave_2.json")
    }

    @Test
    fun parseMmWaveSA() {
        parse("mmWaveSA.binarypb", "mmWaveSA.json")
    }
}
