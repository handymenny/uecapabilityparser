package it.smartphonecombo.uecapabilityparser.extension

import io.pkts.packet.sctp.SctpDataChunk

/** [SctpDataChunk.getPayloadProtocolIdentifier] with a shorter name */
internal inline val SctpDataChunk.ppid
    get() = payloadProtocolIdentifier
