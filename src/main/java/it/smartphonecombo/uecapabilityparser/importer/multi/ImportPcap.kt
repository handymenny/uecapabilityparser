package it.smartphonecombo.uecapabilityparser.importer.multi

import io.pkts.Pcap
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.toHex
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.CaCombosSupported
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.UeCapInfo
import it.smartphonecombo.uecapabilityparser.model.pcap.capability.UeRadioCap
import it.smartphonecombo.uecapabilityparser.model.pcap.container.PduContainer
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import kotlin.math.absoluteValue

object ImportPcap : ImportMultiCapabilities {
    private val validRats = arrayOf(Rat.EUTRA, Rat.EUTRA_NR, Rat.NR)

    override fun parse(input: InputSource): MultiParsing? = parse(input, "PCAP")

    fun parse(input: InputSource, srcName: String): MultiParsing? {
        val inputStream = input.inputStream()
        val pcapStream = Pcap.openStream(inputStream)

        var result: MultiParsing? = null

        try {
            val ueCapabilities = mutableListOf<UeCapInfo>()
            val ueRadioCaps = mutableListOf<UeRadioCap>()
            val b0cd = mutableListOf<CaCombosSupported>()
            val b826 = mutableListOf<CaCombosSupported>()
            var prevFragments = listOf<PduContainer>()

            pcapStream.loop { pkt ->
                val container = PduContainer.from(pkt) ?: return@loop true

                // defragment if needed
                if (container.needDefragmentation) {
                    prevFragments = container.defragment(prevFragments)
                }

                // Collect ue cap info if available
                container.getUeCap()?.let { ueCapabilities.add(it) }

                // Collect 0xb0cd/0xb826 if available
                container.getCaCombosSupported()?.let {
                    if (it.isNr) b826.add(it) else b0cd.add(it)
                }

                // collect ue radio cap if available
                container.getRadioCap()?.let { ueRadioCaps.add(it) }

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

            ueRadioCaps
                .distinctBy { it.ratContainers }
                .forEach {
                    val (newInputs, newSubTypes) = processUeRadioCap(it)

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

    private fun processUeRadioCap(cap: UeRadioCap): Pair<List<InputSource>, List<String>> {
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
}
