package it.smartphonecombo.uecapabilityparser.extension

import java.nio.ByteBuffer

internal fun ByteBuffer.skipBytes(n: Int): ByteBuffer {
    return this.position(this.position() + n)
}

internal fun ByteBuffer.readUnsignedByte(): Int {
    return this.get().toUByte().toInt()
}

internal fun ByteBuffer.readUnsignedShort(): Int {
    return this.short.toUShort().toInt()
}
