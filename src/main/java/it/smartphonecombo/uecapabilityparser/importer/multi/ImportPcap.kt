package it.smartphonecombo.uecapabilityparser.importer.multi

import getIPv4Dst
import getIPv4Src
import io.pkts.Pcap
import io.pkts.packet.IPPacket
import io.pkts.packet.Packet
import io.pkts.packet.gsmtap.GsmTapPacket
import io.pkts.packet.upperpdu.UpperPDUPacket
import io.pkts.protocol.Protocol
import it.smartphonecombo.uecapabilityparser.extension.isLteUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.isNrUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.toHex
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.pcap.OsmoCoreLog
import it.smartphonecombo.uecapabilityparser.model.pcap.UeCapInfo
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import java.io.InputStream
import kotlin.math.absoluteValue

object ImportPcap : ImportMultiCapabilities {
    private val validRats = arrayOf(Rat.EUTRA, Rat.EUTRA_NR, Rat.NR)

    override fun parse(input: InputStream): MultiParsing? {
        val pcapStream = Pcap.openStream(input)

        var result: MultiParsing? = null

        try {
            val ueCapabilities = mutableListOf<UeCapInfo>()
            val b0cd = mutableListOf<OsmoCoreLog>()
            val b826 = mutableListOf<OsmoCoreLog>()

            pcapStream.loop { pkt ->
                processPacket(pkt, b826, b0cd, ueCapabilities)
                true
            }

            val groupedCapabilities =
                ueCapabilities.groupBy { "${it.isNrRrc}-${it.ip}-${it.arfcn}" }.values
            val inputs = mutableListOf<UeCapInfo>()
            for (group in groupedCapabilities) {
                inputs += mergeRats(group)
            }
            val distinctInputs = inputs.distinctBy { it.data }.sortedBy { it.timestamps.first() }

            val inputsList =
                distinctInputs
                    .map { it.data.map { packet -> packet.byteArray.toHex().toByteArray() } }
                    .toMutableList()
            val typeList = List(inputsList.size) { "H" }.toMutableList()
            val subTypesList =
                inputsList
                    .map { List(it.size) { index -> arrayOf("LTE", "ENDC", "NR")[index] } }
                    .toMutableList()
            val descriptions =
                distinctInputs
                    .map {
                        it.timestamps.joinToString(", ", prefix = "UE Cap from PCAP, Timestamps: ")
                    }
                    .toMutableList()

            val distinct0xB0Cd = b0cd.map { it.text }.distinct().joinToString("\n")
            val distinct0xB826 = b826.map { it.text }.distinct().joinToString("\n")
            if (distinct0xB826.isNotEmpty()) {
                inputsList += listOf(distinct0xB826.toByteArray())
                typeList += "QNR"
                subTypesList += emptyList<List<String>>()
                descriptions += "0xB826 packets from PCAP"
            }
            if (distinct0xB0Cd.isNotEmpty()) {
                inputsList += listOf(distinct0xB0Cd.toByteArray())
                typeList += "QLTE"
                subTypesList += emptyList<List<String>>()
                descriptions += "0xB0CD packets from PCAP"
            }

            result = MultiParsing(inputsList, typeList, subTypesList, descriptions)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    private fun processPacket(
        pkt: Packet,
        b826: MutableList<OsmoCoreLog>,
        b0cd: MutableList<OsmoCoreLog>,
        ueCapabilities: MutableList<UeCapInfo>
    ) {
        val data = getGsmTapOrNull(pkt) ?: getUpperPduOrNull(pkt) ?: return

        when {
            data is UpperPDUPacket -> {
                processExportedPDU(data)?.let { ueCapabilities.add(it) }
            }
            (data as GsmTapPacket).type == GsmTapPacket.Type.OSMOCORE_LOG -> {
                processGSMTAPLog(data)?.let { if (it.isNr) b826.add(it) else b0cd.add(it) }
            }
            else -> {
                processGSMTAP(data)?.let { ueCapabilities.add(it) }
            }
        }
    }

    private fun getGsmTapOrNull(pkt: Packet): GsmTapPacket? {
        return if (pkt.hasProtocol(Protocol.GSMTAP)) {
            pkt.getPacket(Protocol.GSMTAP) as GsmTapPacket
        } else null
    }

    private fun getUpperPduOrNull(pkt: Packet): UpperPDUPacket? {
        return if (pkt.hasProtocol(Protocol.UPPPER_PDU)) {
            pkt.getPacket(Protocol.UPPPER_PDU) as UpperPDUPacket
        } else null
    }

    private fun processExportedPDU(pdu: UpperPDUPacket): UeCapInfo? {
        val isLteUlDcch = pdu.dissector == "lte-rrc.ul.dcch"
        val isNrUlDcch = pdu.dissector == "nr-rrc.ul.dcch"

        if (!isLteUlDcch && !isNrUlDcch) return null

        return pktToUeCapMetadata(pdu, isNrUlDcch)
    }

    private fun processGSMTAP(gsmTap: GsmTapPacket): UeCapInfo? {
        val isLteRrc = gsmTap.type == GsmTapPacket.Type.LTE_RRC
        val isUlDcch = gsmTap.subType == GsmTapPacket.LteRRCSubType.UL_DCCH

        if (!isLteRrc || !isUlDcch) return null

        return pktToUeCapMetadata(gsmTap, false)
    }

    private fun processGSMTAPLog(gsmTap: GsmTapPacket): OsmoCoreLog? {
        val text = String(gsmTap.payload.array.drop(84).toByteArray())
        if (!text.contains("CA Combos Raw")) return null
        val isNr = text.startsWith("NR")

        return OsmoCoreLog(text, isNr)
    }

    private fun mergeRats(capabilities: List<UeCapInfo>): List<UeCapInfo> {
        val inputs = mutableListOf<UeCapInfo>()

        var prev: UeCapInfo? = null

        for (i in capabilities.indices) {
            val cur = capabilities[i]

            val noValidRats = cur.ratTypes.none { validRats.contains(it) }
            if (noValidRats) continue

            if (prev != null && shouldMerge(cur, prev)) {
                prev.addData(cur.data, cur.ratTypes, cur.timestamps.last())
            } else {
                inputs.add(cur)
                prev = cur
            }
        }

        return inputs
    }

    // This only checks timestamp and ratTypes. arfcn, rrc and IP aren't checked
    private fun shouldMerge(a: UeCapInfo, b: UeCapInfo) =
        a.timestamps.last().minus(b.timestamps.last()).absoluteValue < 10_000_000 &&
            a.ratTypes.intersect(b.ratTypes).isEmpty()

    private fun pktToUeCapMetadata(pkt: Packet, nr: Boolean): UeCapInfo? {
        val data = pkt.payload.array

        val isUeCap = if (nr) data.isNrUeCapInfoPayload() else data.isLteUeCapInfoPayload()
        if (!isUeCap) return null

        val rrc = if (nr) Rat.NR else Rat.EUTRA
        val ratList = MtsAsn1Helpers.getRatListFromBytes(rrc, data).toSet()
        val arfcn = if (pkt is GsmTapPacket) pkt.arfcn else 0
        val byteArray = ByteArrayDeepEquals(data)
        val ip =
            when (pkt) {
                is UpperPDUPacket -> pkt.getIPv4Dst() ?: pkt.getIPv4Src()
                is GsmTapPacket -> (pkt.parentPacket?.parentPacket as? IPPacket)?.destinationIP
                else -> null
            }

        return UeCapInfo(byteArray, ratList, pkt.arrivalTime, nr, arfcn, ip)
    }
}
