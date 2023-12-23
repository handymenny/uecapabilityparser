package it.smartphonecombo.uecapabilityparser.extension

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/** Compress bytearray to gzip * */
internal fun ByteArray.gzipCompress(): ByteArray {
    val outputStream = ByteArrayOutputStream(this.size)
    GZIPOutputStream(outputStream, 4096).use { it.write(this) }
    return outputStream.use { it.toByteArray() }
}
