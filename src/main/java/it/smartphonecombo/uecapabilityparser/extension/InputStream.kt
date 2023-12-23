package it.smartphonecombo.uecapabilityparser.extension

import java.io.EOFException
import java.io.IOException
import java.io.InputStream

/**
 * Read an unsigned byte from the stream. Throw [EOFException] if end of stream has been reached.
 */
fun InputStream.readUByte(): Int {
    val res = read()
    if (res == -1) throw EOFException()
    return res
}

/**
 * Read a low endian unsigned short from the stream. Throw [EOFException] if end of stream has been
 * reached.
 */
fun InputStream.readUShortLE(): Int {
    val bytes = readNBytes(2)
    if (bytes.size != 2) throw EOFException()
    val down = bytes[0].toUnsignedInt()
    val up = bytes[1].toUnsignedInt() shl 8
    return up or down
}

/**
 * Invokes [InputStream.skip] repeatedly with its parameter equal to the remaining number of bytes
 * to skip until the requested number of bytes has been skipped or an error condition occurs.
 *
 * Inspired by [InputStream.skipNBytes]
 */
fun InputStream.skipBytes(n: Long) {
    var bytesLeft = n
    while (bytesLeft > 0) {
        when (val skipped = skip(bytesLeft)) {
            0L -> {
                readUByte()
                // one byte read so decrement bytesLeft
                bytesLeft--
            }
            in 1..bytesLeft -> bytesLeft -= skipped
            else -> throw IOException("Unable to skip exactly")
        }
    }
}
