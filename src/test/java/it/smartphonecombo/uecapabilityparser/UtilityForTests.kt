package it.smartphonecombo.uecapabilityparser

import java.io.InputStream

object UtilityForTests {

    internal fun getResourceAsStream(path: String): InputStream? =
        object {}.javaClass.getResourceAsStream(path)

    internal fun getResourceAsText(path: String): String? =
        object {}.javaClass.getResource(path)?.readText()
}