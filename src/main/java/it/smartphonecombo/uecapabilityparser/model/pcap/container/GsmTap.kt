package it.smartphonecombo.uecapabilityparser.model.pcap.container

import io.pkts.packet.IPPacket
import io.pkts.packet.Packet
import io.pkts.packet.gsmtap.GsmTapPacket
import io.pkts.packet.gsmtap.GsmTapV3Packet
import io.pkts.packet.gsmtap.GsmTapV3SubType
import io.pkts.packet.gsmtap.GsmTapV3Type
import io.pkts.protocol.Protocol

data class GsmTapV2Container(private val pkt: GsmTapPacket) : PduContainer(pkt) {
    override fun getArfcn() = pkt.arfcn

    override fun getIp() = (pkt.parentPacket?.parentPacket as? IPPacket)?.destinationIP

    override fun isOsmoCoreLog() = pkt.type == GsmTapPacket.Type.OSMOCORE_LOG

    override fun isLteULDCCH() =
        pkt.type == GsmTapPacket.Type.LTE_RRC && pkt.subType == GsmTapPacket.LteRRCSubType.UL_DCCH

    override fun isNrULDCCH() = false

    override fun getRadioCap() = null

    override fun defragment(prevFragments: List<PduContainer>) = prevFragments

    companion object {
        fun from(pkt: Packet): GsmTapV2Container? =
            pkt.getPacket(Protocol.GSMTAP)?.let { GsmTapV2Container(it as GsmTapPacket) }
    }
}

data class GsmTapV3Container(private val pkt: GsmTapV3Packet) : PduContainer(pkt) {
    override fun getArfcn() = 0 // not available yet

    override fun getIp() = (pkt.parentPacket?.parentPacket as? IPPacket)?.destinationIP

    override fun isOsmoCoreLog() = pkt.type == GsmTapV3Type.OSMOCORE_LOG

    override fun isLteULDCCH() =
        pkt.type == GsmTapV3Type.LTE_RRC && pkt.subType == GsmTapV3SubType.LteRRCSubType.UL_DCCH

    override fun isNrULDCCH() =
        pkt.type == GsmTapV3Type.NR_RRC && pkt.subType == GsmTapV3SubType.NrRRCSubType.UL_DCCH

    override fun getRadioCap() = null

    override fun defragment(prevFragments: List<PduContainer>) = prevFragments

    companion object {
        fun from(pkt: Packet): GsmTapV3Container? =
            pkt.getPacket(Protocol.GSMTAPV3)?.let { GsmTapV3Container(it as GsmTapV3Packet) }
    }
}
