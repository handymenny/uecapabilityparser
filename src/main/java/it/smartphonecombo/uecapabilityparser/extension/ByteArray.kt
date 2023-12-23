package it.smartphonecombo.uecapabilityparser.extension

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

internal fun ByteArray.isLteUeCapInfoPayload() = isNotEmpty() && this[0] in 0x38..0x3E

internal fun ByteArray.isNrUeCapInfoPayload() = isNotEmpty() && this[0] in 0x48..0x4E

@OptIn(ExperimentalStdlibApi::class) internal fun ByteArray.toHex() = this.toHexString()

/** Compress bytearray to gzip * */
internal fun ByteArray.gzipCompress(): ByteArray {
    val outputStream = ByteArrayOutputStream(this.size)
    GZIPOutputStream(outputStream, 4096).use { it.write(this) }
    return outputStream.use { it.toByteArray() }
}
