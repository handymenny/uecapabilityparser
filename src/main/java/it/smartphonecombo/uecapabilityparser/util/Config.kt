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
