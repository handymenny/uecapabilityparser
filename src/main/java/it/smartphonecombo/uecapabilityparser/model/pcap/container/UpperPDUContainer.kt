package it.smartphonecombo.uecapabilityparser.model.pcap.container

import io.pkts.packet.Packet
import io.pkts.packet.upperpdu.UpperPDUPacket
import io.pkts.protocol.Protocol
import it.smartphonecombo.uecapabilityparser.extension.getIPv4Dst
import it.smartphonecombo.uecapabilityparser.extension.getIPv4Src

data class UpperPDUContainer(private val pkt: UpperPDUPacket) : PduContainer(pkt) {
    override fun getArfcn() = 0 // not available

    override fun getIp() = pkt.getIPv4Dst() ?: pkt.getIPv4Src()

    override fun isOsmoCoreLog() = false

    override fun isLteULDCCH() = pkt.dissector == "lte-rrc.ul.dcch"

    override fun isNrULDCCH() = pkt.dissector == "nr-rrc.ul.dcch"

    override fun getRadioCap() = null

    override fun defragment(prevFragments: List<PduContainer>) = prevFragments

    companion object {
        fun from(pkt: Packet): UpperPDUContainer? =
            pkt.getPacket(Protocol.UPPPER_PDU)?.let { UpperPDUContainer(it as UpperPDUPacket) }
    }
}
