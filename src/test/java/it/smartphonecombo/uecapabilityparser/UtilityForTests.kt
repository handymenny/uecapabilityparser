package it.smartphonecombo.uecapabilityparser

import it.smartphonecombo.uecapabilityparser.model.PowerClass
import java.io.InputStream

object UtilityForTests {

    internal fun getResourceAsStream(path: String): InputStream? =
        object {}.javaClass.getResourceAsStream(path)

    internal fun Number.toPowerClass(): PowerClass {
        return when (this) {
            1 -> PowerClass.PC1
            1.5 -> PowerClass.PC1dot5
            2 -> PowerClass.PC2
            3 -> PowerClass.PC3
            4 -> PowerClass.PC4
            5 -> PowerClass.PC5
            6 -> PowerClass.PC6
            7 -> PowerClass.PC7
            else -> PowerClass.NONE
        }
    }
}
