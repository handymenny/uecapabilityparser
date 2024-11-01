package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class Import0xB0CDTest :
    AbstractImportCapabilities(Import0xB0CD, "src/test/resources/0xB0CD/") {
    @Test
    fun parse0xB0CDv32() {
        parse("v32.txt", "v32.json")
    }

    @Test
    fun parse0xB0CDv40() {
        parse("v40.txt", "v40.json")
    }

    @Test
    fun parse0xB0CDv41() {
        parse("v41.txt", "v41.json")
    }

    @Test
    fun parse0xB0CDv32Multi() {
        parse("v32multi.txt", "v32multi.json")
    }

    @Test
    fun parse0xB0CDv40Multi() {
        parse("v40multi.txt", "v40multi.json")
    }

    @Test
    fun parse0xB0CDv41Multi() {
        parse("v41multi.txt", "v41multi.json")
    }

    @Test
    fun parse0xB0CDv40MixedMimo() {
        parse("v40mixedMimo.txt", "v40mixedMimo.json")
    }

    @Test
    fun parse0xB0CDv41MixedMimo() {
        parse("v41mixedMimo.txt", "v41mixedMimo.json")
    }
}
