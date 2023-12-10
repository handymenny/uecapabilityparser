package it.smartphonecombo.uecapabilityparser.importer.multi

import it.smartphonecombo.uecapabilityparser.model.scat.ScatLogType
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import java.io.File
import java.io.InputStream

object ImportScat : ImportMultiCapabilities {

    override fun parse(input: InputStream): MultiParsing? = parse(input, ScatLogType.QMDL)

    fun parse(input: InputStream, type: ScatLogType): MultiParsing? {
        var result: MultiParsing? = null
        var tempLogFile: File? = null
        var tempPcapFile: File? = null
        try {
            val extension = type.name.lowercase()
            val scatVendor = if (type == ScatLogType.SDM) "sec" else "qc"
            val combined = if (type != ScatLogType.SDM) "-C" else ""

            tempLogFile = File.createTempFile("SCAT-", ".$extension")
            tempLogFile.writeBytes(input.readAllBytes())
            tempPcapFile = File.createTempFile("PCAP-", ".pcap")

            val builder =
                ProcessBuilder(
                    "scat",
                    "-t",
                    scatVendor,
                    "-d",
                    tempLogFile.path,
                    "-F",
                    tempPcapFile.path,
                    combined,
                    "--cacombos",
                    "--disable-crc-check"
                )

            val redirectIO =
                if (debug) ProcessBuilder.Redirect.INHERIT else ProcessBuilder.Redirect.DISCARD

            builder.redirectError(redirectIO)
            builder.redirectOutput(redirectIO)
            builder.start().waitFor()

            result = ImportPcap.parse(tempPcapFile.inputStream(), type.name)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        try {
            tempLogFile?.delete()
            tempPcapFile?.delete()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }
}
