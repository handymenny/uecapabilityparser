package it.smartphonecombo.uecapabilityparser.importer

import org.junit.jupiter.api.Test

internal class Import0xB0CDBinTest :
    AbstractImportCapabilities(Import0xB0CDBin, "src/test/resources/0xB0CDBin/") {
    @Test
    fun parseV24() {
        parse("0xB0CD-v24-headless.bin", "0xB0CD-v24-headless.json")
    }

    @Test
    fun parseV32() {
        parse("0xB0CD-v32.bin", "0xB0CD-v32.json")
    }

    @Test
    fun parseV324rx() {
        parse("0xB0CD-v32-4rx.bin", "0xB0CD-v32-4rx.json")
    }

    @Test
    fun parseV40() {
        parse("0xB0CD-v40.bin", "0xB0CD-v40.json")
    }

    @Test
    fun parseV403ULCA() {
        parse("0xB0CD-v40-3ULCA.bin", "0xB0CD-v40-3ULCA.json")
    }

    @Test
    fun parseV41() {
        parse("0xB0CD-v41.bin", "0xB0CD-v41.json")
    }

    @Test
    fun parseV41qam64UL() {
        parse("0xB0CD-v41-64qamUL.bin", "0xB0CD-v41-64qamUL.json")
    }

    // declared as v24 but real version below v24
    @Test
    fun parseV24Fake() {
        parse("0xB0CD-V24fake-headless.bin", "0xB0CD-V24fake-headless.json")
    }

    // declared as v32 but real version is v24
    @Test
    fun parseV32Fake() {
        parse("0xB0CD-V32fake.bin", "0xB0CD-v32fake.json")
    }

    // declared as v40 but real version is v32
    @Test
    fun parseV40Fake() {
        parse("0xB0CD-v40fake.bin", "0xB0CD-v40fake.json")
    }

    // declared as v41 but real version is v40
    @Test
    fun parseV41Fake() {
        parse("0xB0CD-v41fake.bin", "0xB0CD-v41fake.json")
    }
}
