package it.smartphonecombo.uecapabilityparser.extension

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
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
