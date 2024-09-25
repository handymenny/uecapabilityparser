package it.smartphonecombo.uecapabilityparser.model.pcap.container

import io.pkts.packet.Packet
import io.pkts.packet.sctp.SctpDataChunk
import io.pkts.packet.sctp.SctpPacket
import io.pkts.protocol.Protocol
import it.smartphonecombo.uecapabilityparser.extension.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.extension.getInt
import it.smartphonecombo.uecapabilityparser.extension.getObject
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.ppid
import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.UeRadioCap
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers

data class SCTPContainer(private val pkt: SctpPacket) : PduContainer(pkt) {
    override var capabilityPayload: ByteArray = byteArrayOf()
    override var logText = ""
    override var needDefragmentation = true
    private val isNgAp: Boolean
    private val chunk: SctpDataChunk?

    init {
        val ppidSet = setOf(S1AP_PROTOCOL_IDENTIFIER, NGAP_PROTOCOL_IDENTIFIER)
        chunk = pkt.chunks.filterIsInstance<SctpDataChunk>().firstOrNull { it.ppid in ppidSet }
        isNgAp = chunk?.payloadProtocolIdentifier == NGAP_PROTOCOL_IDENTIFIER
    }

    override fun getArfcn() = 0

    override fun getIp() = null

    override fun isOsmoCoreLog() = false

    override fun isLteULDCCH() = false

    override fun isNrULDCCH() = false

    override fun defragment(prevFragments: List<PduContainer>): List<PduContainer> {
        needDefragmentation = false
        if (chunk == null) return prevFragments

        val (sctpFragments, otherFragments) = prevFragments.partition { it is SCTPContainer }
        val leftSctpFragments = mergeSctpFragments(chunk, sctpFragments.typedList())

        return otherFragments + leftSctpFragments
    }

    // Call defragmentSctp before calling this function
    override fun getRadioCap(): UeRadioCap? {
        raiseExceptionIfFragmented()

        if (capabilityPayload.isEmpty()) return null

        val rrc = if (isNgAp) Rat.NR else Rat.EUTRA

        // check if payload is valid and is an ue cap radio
        val radioCapProcedureCode = if (rrc == Rat.NR) NGAP_RADIOCAP_CODE else S1AP_RADIOCAP_CODE
        if (capabilityPayload.getOrNull(1) != radioCapProcedureCode.toByte()) return null

        val pdu =
            try {
                MtsAsn1Helpers.apPDUtoJson(rrc, capabilityPayload)
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
        val ueRadioId = if (rrc == Rat.NR) NGAP_RADIOCAP_ID else S1AP_RADIOCAP_ID
        val radioCap =
            pdu?.getArrayAtPath(infoIndPath) // info-indication
                ?.find { it.getInt("id") == ueRadioId }
                ?.getObject("value")
                ?.getString("UERadioCapability")
        val res = MtsAsn1Helpers.ratContainersFromRadioCapability(rrc, radioCap ?: "")

        return if (res == null) null else UeRadioCap(res, pkt.arrivalTime, rrc == Rat.NR)
    }

    /*
     Behaviour:
       - set payload = chunk payload for chunks not fragmented
       - set payload to empty bytearray for first and middle fragment
       - set payload to (prevSctpFragments + chunk payload) for last fragment
       Return the fragments left
    */
    private fun mergeSctpFragments(
        chunk: SctpDataChunk,
        prevSctpFragments: List<SCTPContainer>
    ): List<SCTPContainer> {
        val notFragmented = chunk.isEndingFragment && chunk.isBeginningFragment

        return when {
            // Not fragmented
            notFragmented -> {
                capabilityPayload = chunk.userData.array
                emptyList()
            }
            // 1st fragment
            chunk.isBeginningFragment -> listOf(this)
            // last fragment
            chunk.isEndingFragment -> {
                var tmpArr = ByteArray(0)
                // add prev chunks
                prevSctpFragments
                    .mapNotNull { it.chunk }
                    .filter { chunk.streamSequenceNumber == it.streamSequenceNumber }
                    .forEach { tmpArr += it.userData.array }
                capabilityPayload = tmpArr + chunk.userData.array // sum prev chunks and last chunk
                emptyList()
            }
            // middle fragment
            else -> prevSctpFragments + this
        }
    }

    companion object {
        private const val S1AP_PROTOCOL_IDENTIFIER = 18L
        private const val NGAP_PROTOCOL_IDENTIFIER = 60L
        private const val S1AP_RADIOCAP_CODE = 22
        private const val NGAP_RADIOCAP_CODE = 44
        private const val S1AP_RADIOCAP_ID = 74
        private const val NGAP_RADIOCAP_ID = 117

        fun from(pkt: Packet): SCTPContainer? =
            pkt.getPacket(Protocol.SCTP)?.let { SCTPContainer(it as SctpPacket) }
    }
}
