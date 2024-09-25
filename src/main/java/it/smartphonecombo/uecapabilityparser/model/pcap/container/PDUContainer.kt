package it.smartphonecombo.uecapabilityparser.model.pcap.container

import io.pkts.packet.Packet
import io.pkts.protocol.Protocol
import it.smartphonecombo.uecapabilityparser.extension.contains
import it.smartphonecombo.uecapabilityparser.extension.isLteUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.isNrUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.CaCombosSupported
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.UeCapInfo
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.UeRadioCap
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers

sealed class PduContainer(private val pkt: Packet) {
    /** If true this container must be defragmented before extracting any capabilities * */
    open val needDefragmentation = false
    /** The default implementation works for GSMTAPv2, GSMTAPv3, EXPORTED PDU * */
    protected open val capabilityPayload: ByteArray by
        lazy(LazyThreadSafetyMode.NONE) { pkt.payload.array }
    /** The default implementation works for GSMTAPv2, GSMTAPv3 while type = OSMOCORE_LOG * */
    protected open val logText by
        lazy(LazyThreadSafetyMode.NONE) {
            capabilityPayload.drop(84).toByteArray().decodeToString()
        }

    protected abstract fun getArfcn(): Int

    protected abstract fun getIp(): String?

    protected abstract fun isOsmoCoreLog(): Boolean

    protected abstract fun isLteULDCCH(): Boolean

    protected abstract fun isNrULDCCH(): Boolean

    protected fun raiseExceptionIfFragmented() {
        if (needDefragmentation) {
            throw RuntimeException("You must defragment this packet before calling this function")
        }
    }

    private fun hasHwCombos() = isOsmoCoreLog() && logText.contains("CA Combos Raw")

    private fun hasUeCap() =
        isLteULDCCH() && capabilityPayload.isLteUeCapInfoPayload() ||
            isNrULDCCH() && capabilityPayload.isNrUeCapInfoPayload()

    /** The default implementation works for GSMTAPv2, GSMTAPv3 * */
    fun getCaCombosSupported(): CaCombosSupported? {
        raiseExceptionIfFragmented()
        if (!hasHwCombos()) return null

        val isNr = logText.startsWith("NR")
        return CaCombosSupported(logText, isNr)
    }

    /** The default implementation works for all containers * */
    fun getUeCap(): UeCapInfo? {
        raiseExceptionIfFragmented()
        if (!hasUeCap()) return null

        val nr = isNrULDCCH()
        val rat = if (nr) Rat.NR else Rat.EUTRA

        val ratList = MtsAsn1Helpers.getRatListFromBytes(rat, capabilityPayload).toSet()
        val arfcn = getArfcn()
        val byteArray = ByteArrayDeepEquals(capabilityPayload)

        return UeCapInfo(byteArray, ratList, pkt.arrivalTime, nr, arfcn, getIp())
    }

    // No default implementation
    abstract fun getRadioCap(): UeRadioCap?

    // No default implementation
    // return left fragments
    abstract fun defragment(prevFragments: List<PduContainer>): List<PduContainer>

    companion object {
        fun from(pkt: Packet): PduContainer? {
            return when {
                Protocol.GSMTAP in pkt -> GsmTapV2Container.from(pkt)
                Protocol.GSMTAPV3 in pkt -> GsmTapV3Container.from(pkt)
                Protocol.UPPPER_PDU in pkt -> UpperPDUContainer.from(pkt)
                Protocol.SCTP in pkt -> SCTPContainer.from(pkt)
                else -> null
            }
        }
    }
}
