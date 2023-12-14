package it.smartphonecombo.uecapabilityparser.importer.multi

import it.smartphonecombo.uecapabilityparser.extension.closeIgnoreException
import it.smartphonecombo.uecapabilityparser.extension.deleteIgnoreException
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImportScat : ImportMultiCapabilities {

    override fun parse(input: InputStream): MultiParsing? = parse(input, LogType.QMDL)

    fun parse(input: InputStream, type: LogType): MultiParsing? {
        var result: MultiParsing? = null
        var tempLogFile: File? = null
        var tempPcapFile: File? = null
        var pcapInputStream: InputStream? = null
        try {
            val extension = type.name.lowercase()
            val scatVendor = if (type == LogType.SDM) "sec" else "qc"

            tempLogFile = File.createTempFile("SCAT-", ".$extension")
            tempLogFile.writeBytes(input.readAllBytes())
            tempPcapFile = File.createTempFile("PCAP-", ".pcap")

            val args =
                mutableListOf(
                    "scat",
                    "-t",
                    scatVendor,
                    "-d",
                    tempLogFile.path,
                    "-F",
                    tempPcapFile.path,
                )

            if (type != LogType.SDM) {
                args.add("-C")
                args.add("--cacombos")
                args.add("--disable-crc-check")
            }
            val builder = ProcessBuilder(args)

            val redirectIO =
                if (debug) ProcessBuilder.Redirect.INHERIT else ProcessBuilder.Redirect.DISCARD

            builder.redirectError(redirectIO)
            builder.redirectOutput(redirectIO)
            builder.start().waitFor()
            pcapInputStream = tempPcapFile.inputStream()
            result = ImportPcap.parse(pcapInputStream, type.name)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        cleanup(arrayOf(input, pcapInputStream), arrayOf(tempLogFile, tempPcapFile))

        return result
    }

    private fun cleanup(inputs: Array<InputStream?>, files: Array<File?>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                inputs.forEach { it?.closeIgnoreException() }
                files.forEach { it?.deleteIgnoreException() }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
