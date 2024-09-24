package it.smartphonecombo.uecapabilityparser.importer.multi

import io.pkts.Pcap
import io.pkts.packet.IPPacket
import io.pkts.packet.Packet
import io.pkts.packet.gsmtap.GsmTapPacket
import io.pkts.packet.gsmtap.GsmTapV3Packet
import io.pkts.packet.sctp.SctpDataChunk
import io.pkts.packet.sctp.SctpPacket
import io.pkts.packet.upperpdu.UpperPDUPacket
import io.pkts.protocol.Protocol
import it.smartphonecombo.uecapabilityparser.extension.contains
import it.smartphonecombo.uecapabilityparser.extension.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.extension.getIPv4Dst
import it.smartphonecombo.uecapabilityparser.extension.getIPv4Src
import it.smartphonecombo.uecapabilityparser.extension.getInt
import it.smartphonecombo.uecapabilityparser.extension.getObject
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.isLteUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.isNrUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.ppid
import it.smartphonecombo.uecapabilityparser.extension.toHex
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.pcap.OsmoCoreLog
import it.smartphonecombo.uecapabilityparser.model.pcap.PcapMessageType
import it.smartphonecombo.uecapabilityparser.model.pcap.UeCapInfo
import it.smartphonecombo.uecapabilityparser.model.pcap.UeCapRatContainers
import it.smartphonecombo.uecapabilityparser.model.pcap.getMessageTypeForParser
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import kotlin.math.absoluteValue

object ImportPcap : ImportMultiCapabilities {
    private val validRats = arrayOf(Rat.EUTRA, Rat.EUTRA_NR, Rat.NR)
    private const val S1AP_PROTOCOL_IDENTIFIER = 18L
    private const val NGAP_PROTOCOL_IDENTIFIER = 60L

    override fun parse(input: InputSource): MultiParsing? = parse(input, "PCAP")

    fun parse(input: InputSource, srcName: String): MultiParsing? {
        val inputStream = input.inputStream()
        val pcapStream = Pcap.openStream(inputStream)

        var result: MultiParsing? = null

        try {
            val ueCapabilities = mutableListOf<UeCapInfo>()
            val b0cd = mutableListOf<OsmoCoreLog>()
            val b826 = mutableListOf<OsmoCoreLog>()
            val ueRatContainersList = mutableListOf<UeCapRatContainers>()
            val prevSctpPackets = mutableListOf<SctpDataChunk>()

            pcapStream.loop { pkt ->
                val container = getContainer(pkt) ?: return@loop true
                when (val type = getMessageType(container)) {
                    PcapMessageType.LTE_RRC_UL_DCCH,
                    PcapMessageType.NR_RRC_UL_DCCH -> {
                        val isNr = type == PcapMessageType.NR_RRC_UL_DCCH
                        pktToUeCapMetadata(container, isNr)?.let { ueCapabilities.add(it) }
                    }
                    PcapMessageType.OSMOCORE_LOG ->
                        processGSMTAPLog(container)?.let {
                            if (it.isNr) b826.add(it) else b0cd.add(it)
                        }
                    PcapMessageType.SCTPDATA_CHUNK ->
                        processSCTP(container as SctpPacket, prevSctpPackets)?.let {
                            ueRatContainersList.add(it)
                        }
                    null -> {
                        // nothing
                    }
                }
                true
            }

            val groupedCapabilities =
                ueCapabilities.groupBy { "${it.isNrRrc}-${it.ip}-${it.arfcn}" }.values
            val inputs = mutableListOf<UeCapInfo>()
            for (group in groupedCapabilities) {
                inputs += mergeRats(group)
            }
            val distinctInputs = inputs.distinctBy { it.data }.sortedBy { it.timestamps.first() }

            val inputsList: MutableList<List<InputSource>> =
                distinctInputs
                    .map { it.data.map { packet -> packet.byteArray.toHex().toInputSource() } }
                    .toMutableList()
            val typeList = List(inputsList.size) { LogType.H }.toMutableList()
            val subTypesList =
                inputsList
                    .map { List(it.size) { index -> arrayOf("LTE", "ENDC", "NR")[index] } }
                    .toMutableList()
            val descriptions =
                distinctInputs
                    .map {
                        val rat = if (it.isNrRrc) "NR" else "LTE"
                        it.timestamps.joinToString(", ", "UE $rat Cap from $srcName, Timestamps: ")
                    }
                    .toMutableList()

            val distinct0xB0Cd = b0cd.map { it.text }.distinct().joinToString("\n")
            val distinct0xB826 = b826.map { it.text }.distinct().joinToString("\n")
            if (distinct0xB826.isNotEmpty()) {
                inputsList += listOf(distinct0xB826.toInputSource())
                typeList += LogType.QNR
                subTypesList += emptyList<List<String>>()
                descriptions += "0xB826 packets from $srcName"
            }
            if (distinct0xB0Cd.isNotEmpty()) {
                inputsList += listOf(distinct0xB0Cd.toInputSource())
                typeList += LogType.QLTE
                subTypesList += emptyList<List<String>>()
                descriptions += "0xB0CD packets from $srcName"
            }

            ueRatContainersList
                .distinctBy { it.ratContainers }
                .forEach {
                    val (newInputs, newSubTypes) = processUeRatContainers(it)

                    if (newInputs.isNotEmpty()) {
                        typeList += LogType.H
                        inputsList += newInputs
                        subTypesList += newSubTypes
                        descriptions +=
                            "${it.messageName} from $srcName, Timestamp: ${it.timestamp}"
                    }
                }

            result = MultiParsing(inputsList, typeList, subTypesList, descriptions)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        inputStream.close()
        return result
    }

    private fun processUeRatContainers(
        cap: UeCapRatContainers
    ): Pair<List<InputSource>, List<String>> {
        val octetString =
            if (cap.isNrRrc) "ue-CapabilityRAT-Container" else "ueCapabilityRAT-Container"

        val inputs = mutableListOf<InputSource>()
        val subTypes = mutableListOf<String>()

        for (container in cap.ratContainers) {
            val payload = container.getString(octetString) ?: continue
            val ratType = Rat.of(container.getString("rat-Type"))
            val subType =
                when (ratType) {
                    Rat.EUTRA -> "LTE"
                    Rat.EUTRA_NR -> "ENDC"
                    Rat.NR -> "NR"
                    else -> continue
                }

            inputs += payload.toInputSource()
            subTypes += subType
        }
        return Pair(inputs, subTypes)
    }

    private fun processSCTP(
        pkt: SctpPacket,
        prevSctpFragments: MutableList<SctpDataChunk>
    ): UeCapRatContainers? {
        val ppidSet = setOf(S1AP_PROTOCOL_IDENTIFIER, NGAP_PROTOCOL_IDENTIFIER)
        val chunk = pkt.chunks.filterIsInstance<SctpDataChunk>().firstOrNull { it.ppid in ppidSet }
        if (chunk == null) return null

        val rrc =
            if (chunk.payloadProtocolIdentifier == NGAP_PROTOCOL_IDENTIFIER) Rat.NR else Rat.EUTRA

        val payload = mergeSctpFragments(chunk, prevSctpFragments)

        // check if payload is valid and is an ue cap radio
        val radioCapProcedureCode = if (rrc == Rat.NR) 44 else 22
        if (payload?.getOrNull(1) != radioCapProcedureCode.toByte()) return null

        val pdu =
            try {
                MtsAsn1Helpers.apPDUtoJson(rrc, payload)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }

        val infoIndPath =
            if (rrc == Rat.NR) {
                "initiatingMessage.value.UERadioCapabilityInfoIndication.protocolIEs"
            } else {
                "initiatingMessage.value.UECapabilityInfoIndication.protocolIEs"
            }
        // id in protocol-ie
        val ueRadioId = if (rrc == Rat.NR) 117 else 74
        val radioCap =
            pdu?.getArrayAtPath(infoIndPath) // info-indication
                ?.find { it.getInt("id") == ueRadioId }
                ?.getObject("value")
                ?.getString("UERadioCapability")
        val res = MtsAsn1Helpers.ratContainersFromRadioCapability(rrc, radioCap ?: "")

        return if (res == null) null else UeCapRatContainers(res, pkt.arrivalTime, rrc == Rat.NR)
    }

    /*
     Behaviour:
       - return payload for unfragmented chunk
       - update prevSctpFragments and return null for first and middle fragment
       - return prevSctpFragments + payload for last fragment

       prevSctpFragments is automatically cleared when needed
    */
    private fun mergeSctpFragments(
        chunk: SctpDataChunk,
        prevSctpFragments: MutableList<SctpDataChunk>
    ): ByteArray? {
        val payload: ByteArray?
        when {
            // Not fragmented
            chunk.isEndingFragment && chunk.isBeginningFragment -> {
                payload = chunk.userData.array
                prevSctpFragments.clear()
            }
            // last fragment
            chunk.isEndingFragment -> {
                var tmpArr = ByteArray(0)
                // add prev chunks
                prevSctpFragments
                    .filter { chunk.streamSequenceNumber == it.streamSequenceNumber }
                    .forEach { tmpArr += it.userData.array }
                payload = tmpArr + chunk.userData.array // sum prev chunks and last chunk

                prevSctpFragments.clear()
            }
            // 1st fragment or middle fragment
            else -> {
                if (chunk.isBeginningFragment) prevSctpFragments.clear() // 1st fragment
                prevSctpFragments.add(chunk)
                payload = null
            }
        }
        return payload
    }

    private fun processGSMTAPLog(gsmTap: Packet): OsmoCoreLog? {
        val text = gsmTap.payload.array.drop(84).toByteArray().decodeToString()
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
                is GsmTapPacket,
                is GsmTapV3Packet -> (pkt.parentPacket?.parentPacket as? IPPacket)?.destinationIP
                else -> null
            }

        return UeCapInfo(byteArray, ratList, pkt.arrivalTime, nr, arfcn, ip)
    }

    private fun getContainerProtocol(pkt: Packet): Protocol? {
        return when {
            Protocol.GSMTAP in pkt -> Protocol.GSMTAP
            Protocol.GSMTAPV3 in pkt -> Protocol.GSMTAPV3
            Protocol.UPPPER_PDU in pkt -> Protocol.UPPPER_PDU
            Protocol.SCTP in pkt -> Protocol.SCTP
            else -> null
        }
    }

    private fun getContainer(pkt: Packet): Packet? =
        getContainerProtocol(pkt)?.let { pkt.getPacket(it) }

    private fun getMessageType(pkt: Packet): PcapMessageType? {
        if (pkt is SctpPacket) return PcapMessageType.SCTPDATA_CHUNK
        if (pkt is GsmTapPacket) return pkt.getMessageTypeForParser()
        if (pkt is GsmTapV3Packet) return pkt.getMessageTypeForParser()
        if (pkt is UpperPDUPacket) return pkt.getMessageTypeForParser()
        return null
    }
}
