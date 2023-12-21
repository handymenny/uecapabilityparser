package it.smartphonecombo.uecapabilityparser.importer.multi

import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.MultiParsing

sealed interface ImportMultiCapabilities {
    val debug
        get() = Config.getOrDefault("debug", "false").toBoolean()

    fun parse(input: InputSource): MultiParsing?
}
