package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class ImportNvItemTest :
    AbstractImportCapabilities(ImportNvItem, "src/test/resources/nvitem/") {
    @Test
    fun parseNvItem137() {
        parse("28874_137.bin", "28874_137.json")
    }

    @Test
    fun parseNvItem137201() {
        parse("28874_137_201.bin", "28874_137_201.json")
    }

    @Test
    fun parseNvItem201() {
        parse("28874_201.bin", "28874_201.json")
    }

    @Test
    fun parseNvItem333() {
        parse("28874_333.bin", "28874_333.json")
    }

    @Test
    fun parseNvItemZlib() {
        parse("28874_zlib.bin", "28874_zlib.json")
    }

    @Test
    fun parseNvItemDummy() {
        parse("28874_137_dummy.bin", "28874_137_dummy.json")
    }
}
