package it.smartphonecombo.uecapabilityparser.extension

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

/** Remove all extensions * */
internal fun File.nameWithoutAnyExtension() = this.name.substringBefore(".")

/** Move file to the given path, replacing and existing file if it exists */
internal fun File.moveTo(path: String) {
    Files.move(this.toPath(), Path(path), StandardCopyOption.REPLACE_EXISTING)
}

internal fun File.deleteIgnoreException() {
    try {
        Files.delete(toPath())
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
