package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.importer.multi.ImportNsgJson
import org.junit.jupiter.api.Test

internal class ImportNsgJsonTest :
    AbstractImportMultiCapabilities(ImportNsgJson, "src/test/resources/nsgJson/") {
    // Json from Airscreen 2.5.9, device type qualcomm
    @Test
    fun testAirscreenQcom() {
        parse("airscreenQcom.json", "airscreenQcom.json")
    }

    // Json from NSG 4.6.26, device type exynos
    @Test
    fun testNsgExy() {
        parse("nsgExy.json", "nsgExy.json")
    }

    // Json handcrafted without useful data
    @Test
    fun testEmpty() {
        parse("empty.json", "empty.json")
    }
}
