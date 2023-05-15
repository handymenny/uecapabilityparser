package it.smartphonecombo.uecapabilityparser.extension

import java.io.ByteArrayOutputStream
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.util.zip.Inflater
import kotlin.jvm.Throws

@Throws(BufferUnderflowException::class)
internal fun ByteBuffer.skipBytes(n: Int): ByteBuffer {
    try {
        return this.position(this.position() + n)
    } catch (ignored: IllegalArgumentException) {
        throw BufferUnderflowException()
    }
}

@Throws(BufferUnderflowException::class)
internal fun ByteBuffer.readUnsignedByte(): Int {
    return this.get().toUByte().toInt()
}

@Throws(BufferUnderflowException::class)
internal fun ByteBuffer.readUnsignedShort(): Int {
    return this.short.toUShort().toInt()
}

/** Decompress a byte buffer using ZLIB. */
fun ByteBuffer.zlibDecompress(): ByteBuffer {
    val outputStream = ByteArrayOutputStream()

    outputStream.use {
        val inflater = Inflater()
        val buffer = ByteArray(100 * 1024)
        var byteCount: Int
        var iterationCount = 0

        inflater.setInput(this)

        do {
            byteCount = inflater.inflate(buffer)
            outputStream.write(buffer, 0, byteCount)
            iterationCount++
        } while (byteCount != 0 && iterationCount < 100) // Don't decompress more than 10MB

        inflater.end()
    }

    return ByteBuffer.wrap(outputStream.toByteArray())
}
