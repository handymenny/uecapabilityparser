package it.smartphonecombo.uecapabilityparser.extension

import java.io.File

/** Remove all extensions * */
internal fun File.nameWithoutAnyExtension() = this.name.substringBefore(".")
