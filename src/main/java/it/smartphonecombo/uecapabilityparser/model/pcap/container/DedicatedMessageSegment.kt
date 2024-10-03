package it.smartphonecombo.uecapabilityparser.model.pcap.container

import io.pkts.packet.Packet
import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.getInt
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers

data class DedicatedMessageSegment(
    private val pkt: Packet,
    private val isNrRrc: Boolean,
    private val arfcn: Int,
    private val ip: String?,
) : PduContainer(pkt) {
    override var capabilityPayload: ByteArray = byteArrayOf()
    override var needDefragmentation: Boolean = true

    private val messageSegmentPayload: ByteArray?
    private val segmentNumber: Int
    private val isLastSegment: Boolean

    init {
        val rat = if (isNrRrc) Rat.NR else Rat.EUTRA
        val payload = pkt.payload.array
        val messageSegment = MtsAsn1Helpers.getDedicatedMessageSegmentFromBytes(rat, payload)

        segmentNumber = messageSegment?.getInt("segmentNumber-r16") ?: 0
        isLastSegment = messageSegment?.getString("rrc-MessageSegmentType-r16") == "lastSegment"
        val messageSegmentContainer = messageSegment?.getString("rrc-MessageSegmentContainer-r16")
        messageSegmentPayload = messageSegmentContainer?.decodeHex()
    }

    override fun getArfcn() = arfcn

    override fun getIp() = ip

    override fun isOsmoCoreLog() = false

    override fun isLteULDCCH() = !isNrRrc

    override fun isNrULDCCH() = isNrRrc

    override fun getRadioCap() = null

    override fun defragment(prevFragments: List<PduContainer>): List<PduContainer> {
        needDefragmentation = false
        if (messageSegmentPayload == null) return prevFragments

        val (relevantFragments, otherFragments) =
            prevFragments.partition {
                it is DedicatedMessageSegment &&
                    it.isNrRrc == this.isNrRrc &&
                    it.ip == this.ip &&
                    it.arfcn == this.arfcn
            }

        val leftSctpFragments = mergeFragments(relevantFragments.typedList())

        return otherFragments + leftSctpFragments
    }

    private fun mergeFragments(
        prevSegments: List<DedicatedMessageSegment>
    ): List<DedicatedMessageSegment> {
        return when {
            // 1st segment
            segmentNumber == 0 -> listOf(this)
            // last segment
            isLastSegment -> {
                var tmpArr = ByteArray(0)
                // add prev segments
                prevSegments
                    .filter { it.segmentNumber <= this.segmentNumber }
                    .forEach { tmpArr += it.messageSegmentPayload!! }
                capabilityPayload =
                    tmpArr + messageSegmentPayload!! // sum prev segments and last segment
                emptyList()
            }
            // middle segment
            else -> prevSegments + this
        }
    }

    companion object {
        fun from(pkt: Packet, isNr: Boolean, arfcn: Int, ip: String): DedicatedMessageSegment {
            return DedicatedMessageSegment(pkt, isNr, arfcn, ip)
        }
    }
}
