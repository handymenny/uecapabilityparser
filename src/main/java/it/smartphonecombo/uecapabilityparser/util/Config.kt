package it.smartphonecombo.uecapabilityparser.util

import java.util.Properties
import kotlin.collections.HashMap

private const val CONFIG_FILE = "/application.properties"

object Config : HashMap<String, String>() {
    init {
        importFromFile()
    }

    override fun clear() {
        super.clear()
        importFromFile()
    }

    // Serializable object must implement readResolve to preserve singleton identity during
    // deserialization
    @Suppress("unused") private fun readResolve(): Any = Config

    private fun importFromFile() {
        javaClass.getResourceAsStream(CONFIG_FILE)?.let {
            val properties = Properties()
            properties.load(it)
            properties.entries.filterIsInstance<Map.Entry<String, String>>().forEach { entry ->
                this[entry.key] = entry.value
            }
        }
    }
}
