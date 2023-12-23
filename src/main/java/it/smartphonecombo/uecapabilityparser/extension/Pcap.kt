package it.smartphonecombo.uecapabilityparser.extension

import io.pkts.buffer.Buffer
import io.pkts.packet.sctp.SctpDataChunk
import io.pkts.packet.upperpdu.PDUOption
import io.pkts.packet.upperpdu.UpperPDUPacket

/** [SctpDataChunk.getPayloadProtocolIdentifier] with a shorter name */
internal inline val SctpDataChunk.ppid
    get() = payloadProtocolIdentifier

internal fun UpperPDUPacket.getIPv4Dst(): String? {
    val tag = options.find { it.type == PDUOption.TagOption.IPV4_DST } ?: return null
    return ipv4BufferToStr(tag.value)
}

internal fun UpperPDUPacket.getIPv4Src(): String? {
    val tag = options.find { it.type == PDUOption.TagOption.IPV4_SRC } ?: return null
    return ipv4BufferToStr(tag.value)
}

private fun ipv4BufferToStr(buffer: Buffer): String {
    val a = buffer.getUnsignedByte(0)
    val b = buffer.getUnsignedByte(1)
    val c = buffer.getUnsignedByte(2)
    val d = buffer.getUnsignedByte(3)
    return "$a.$b.$c.$d"
}
