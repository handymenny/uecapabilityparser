package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportQctModemCapTest :
    AbstractImportCapabilities(ImportQctModemCap, "src/test/resources/qctModemCap/") {
    @Test
    fun parseEfsQuery() {
        parse("efs-query.txt", "efs-query.json")
    }

    @Test
    fun parseFtmQuery() {
        parse("ftm-query.txt", "ftm-query.json")
    }

    @Test
    fun parseLowercase() {
        parse("lowercase.txt", "lowercase.json")
    }

    @Test
    fun parseTwoRow() {
        parse("two-row.txt", "two-row.json")
    }

    @Test
    fun parseNrRrc() {
        // empty result
        parse("nr-rrc.txt", "nr-rrc.json")
    }

    @Test
    fun parseLteRrc() {
        parse("lte-rrc.txt", "lte-rrc.json")
    }

    @Test
    fun parseLteRrcInvalid() {
        parse("lte-rrc-invalid.txt", "lte-rrc-invalid.json")
    }

    @Test
    fun parseMulti() {
        parse("multi.txt", "multi.json")
    }
}
