package it.smartphonecombo.uecapabilityparser.extension

import io.pkts.packet.sctp.SctpDataChunk

/** [SctpDataChunk.getPayloadProtocolIdentifier] with a shorter name */
@Suppress("NOTHING_TO_INLINE") inline fun SctpDataChunk.ppid() = payloadProtocolIdentifier
