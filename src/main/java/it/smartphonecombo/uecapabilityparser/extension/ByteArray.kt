package it.smartphonecombo.uecapabilityparser.extension

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

internal fun ByteArray.isLteUeCapInfoPayload() = isNotEmpty() && this[0] in 0x38..0x3E

internal fun ByteArray.isNrUeCapInfoPayload() = isNotEmpty() && this[0] in 0x48..0x4E

internal fun ByteArray.isLteUlDcchSegment() = isNotEmpty() && this[0].toUnsignedInt() in 0xAC..0xAD

internal fun ByteArray.isNrUlDcchSegment() = isNotEmpty() && this[0].toUnsignedInt() in 0x80..0x81

@OptIn(ExperimentalStdlibApi::class) internal fun ByteArray.toHex() = this.toHexString()

/** Compress bytearray to gzip * */
internal fun ByteArray.gzipCompress(): ByteArray {
    val outputStream = ByteArrayOutputStream(this.size)
    GZIPOutputStream(outputStream, 4096).use { it.write(this) }
    return outputStream.use { it.toByteArray() }
}
