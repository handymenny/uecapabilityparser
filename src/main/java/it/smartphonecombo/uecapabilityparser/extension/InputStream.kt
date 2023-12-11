package it.smartphonecombo.uecapabilityparser.extension

import java.io.InputStream

internal fun InputStream.closeIgnoreException() {
    try {
        this.close()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
