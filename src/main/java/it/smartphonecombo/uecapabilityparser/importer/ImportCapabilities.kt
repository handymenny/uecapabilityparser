package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.InputStream

sealed interface ImportCapabilities {
    val debug
        get() = Config.getOrDefault("debug", "false").toBoolean()

    fun parse(input: InputStream): Capabilities
}
