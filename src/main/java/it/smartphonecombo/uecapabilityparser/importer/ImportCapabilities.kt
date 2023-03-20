package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.Config
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import java.io.InputStream

interface ImportCapabilities {
    val debug
        get() = Config.getOrDefault("debug", "false").toBoolean()

    fun parse(input: InputStream): Capabilities
}
