package it.smartphonecombo.uecapabilityparser.extension

import java.nio.ByteBuffer

fun ByteBuffer.skipBytes(n: Int): ByteBuffer {
    return this.position(this.position() + n)
}

fun ByteBuffer.readUnsignedByte(): Int {
    return this.get().toUByte().toInt()
}

fun ByteBuffer.readUnsignedShort(): Int {
    return this.short.toUShort().toInt()
}
