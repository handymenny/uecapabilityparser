package it.smartphonecombo.uecapabilityparser.importer.multi

import it.smartphonecombo.uecapabilityparser.extension.capitalize
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.nsg.NsgJsonLog
import it.smartphonecombo.uecapabilityparser.model.nsg.NsgMessage
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.serialization.json.Json

object ImportNsgJson : ImportMultiCapabilities {

    override fun parse(input: InputSource): MultiParsing? {
        var result: MultiParsing? = null
        try {
            val nsgLog = Json.custom().decodeFromInputSource<NsgJsonLog>(input)

            // get relevant pcap packets
            val pcapPkts =
                nsgLog.data.flatMap {
                    it.messages.filter(NsgMessage::isUeCap).mapNotNull(NsgMessage::pcapPacket)
                }
            val byteArray = writeToByteArray(nsgLog.pcapHeader, pcapPkts)

            val pcapInput = byteArray.toInputSource()
            result = ImportPcap.parse(pcapInput, LogType.NSG.name)

            // add metadata to all capabilities
            addMetadata(nsgLog, result)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    private fun writeToByteArray(
        header: ByteArrayDeepEquals,
        pkts: List<ByteArrayDeepEquals>
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()

        // write header
        outputStream.write(header.byteArray)

        // write pcap pkts
        for (pkt in pkts) {
            outputStream.write(pkt.byteArray)
        }

        return outputStream.toByteArray()
    }

    private fun addMetadata(nsgLog: NsgJsonLog, result: MultiParsing?) {
        // convert timestamp to date
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC"))
        val instant = Instant.ofEpochMilli(nsgLog.startTime)
        val date = formatter.format(instant)

        // add metadata to all results,
        result?.parsingList?.forEach {
            it.capabilities.addMetadata("deviceName", nsgLog.device.name.capitalize())
            it.capabilities.addMetadata("deviceType", nsgLog.device.type)
            it.capabilities.addMetadata("simId", nsgLog.device.subscription)
            it.capabilities.addMetadata("captureDate", date)
        }
    }
}
