package it.smartphonecombo.uecapabilityparser.model.pcap

import io.pkts.packet.gsmtap.GsmTapPacket
import io.pkts.packet.gsmtap.GsmTapV3Packet
import io.pkts.packet.gsmtap.GsmTapV3SubType
import io.pkts.packet.gsmtap.GsmTapV3Type
import io.pkts.packet.upperpdu.UpperPDUPacket

enum class PcapMessageType {
    LTE_RRC_UL_DCCH,
    NR_RRC_UL_DCCH,
    OSMOCORE_LOG,
    SCTPDATA_CHUNK
}

fun GsmTapPacket.getMessageTypeForParser(): PcapMessageType? {
    return when {
        type == GsmTapPacket.Type.OSMOCORE_LOG -> PcapMessageType.OSMOCORE_LOG
        type == GsmTapPacket.Type.LTE_RRC && subType == GsmTapPacket.LteRRCSubType.UL_DCCH ->
            PcapMessageType.LTE_RRC_UL_DCCH
        else -> null
    }
}

fun GsmTapV3Packet.getMessageTypeForParser(): PcapMessageType? {
    return when {
        type == GsmTapV3Type.OSMOCORE_LOG -> PcapMessageType.OSMOCORE_LOG
        type == GsmTapV3Type.LTE_RRC && subType == GsmTapV3SubType.LteRRCSubType.UL_DCCH ->
            PcapMessageType.LTE_RRC_UL_DCCH
        type == GsmTapV3Type.NR_RRC && subType == GsmTapV3SubType.NrRRCSubType.UL_DCCH ->
            PcapMessageType.NR_RRC_UL_DCCH
        else -> null
    }
}

fun UpperPDUPacket.getMessageTypeForParser(): PcapMessageType? {
    return when (dissector) {
        "lte-rrc.ul.dcch" -> PcapMessageType.LTE_RRC_UL_DCCH
        "nr-rrc.ul.dcch" -> PcapMessageType.NR_RRC_UL_DCCH
        else -> null
    }
}
