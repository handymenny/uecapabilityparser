package it.smartphonecombo.uecapabilityparser.extension

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/** Read utf-8 text from gzipInputStream. Automatically closes the stream. * */
internal fun GZIPInputStream.readText(): String = reader().use { it.readText() }

/** Create gzipInputStream from file. Remember to close the stream. */
internal fun File.gzipDecompress(): GZIPInputStream = GZIPInputStream(this.inputStream(), 4096)

/** Compress bytearray to gzip * */
internal fun ByteArray.gzipCompress(): ByteArray {
    val outputStream = ByteArrayOutputStream(this.size)
    GZIPOutputStream(outputStream, 4096).use { it.write(this) }
    return outputStream.use { it.toByteArray() }
}
