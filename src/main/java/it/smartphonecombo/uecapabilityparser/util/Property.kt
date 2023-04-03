package it.smartphonecombo.uecapabilityparser.util

import java.util.*

private const val CONFIG = "/application.properties"

object Property {
    private val properties = Properties()

    init {
        javaClass.getResourceAsStream(CONFIG)?.let { properties.load(it) }
    }

    fun getProperty(key: String): String? = properties.getProperty(key)
}
