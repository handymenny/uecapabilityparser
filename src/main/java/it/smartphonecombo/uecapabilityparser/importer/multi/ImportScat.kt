package it.smartphonecombo.uecapabilityparser.importer.multi

import it.smartphonecombo.uecapabilityparser.extension.deleteIgnoreException
import it.smartphonecombo.uecapabilityparser.io.FileInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.io.toInputSource
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImportScat : ImportMultiCapabilities {

    override fun parse(input: InputSource): MultiParsing? = parse(input, LogType.QMDL)

    fun parse(input: InputSource, type: LogType): MultiParsing? {
        var result: MultiParsing? = null
        var tempLogFile: File? = null
        var tempPcapFile: File? = null
        try {
            val extension = type.name.lowercase()
            val scatVendor = if (type == LogType.SDM) "sec" else "qc"

            val logFilePath =
                if (input is FileInputSource) {
                    // Don't need to store it in temp
                    input.file.path
                } else {
                    tempLogFile = File.createTempFile("SCAT-", ".$extension")
                    input.inputStream().use {
                        Files.copy(it, tempLogFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    tempLogFile.path
                }

            tempPcapFile = File.createTempFile("PCAP-", ".pcap")

            val args =
                mutableListOf(
                    "scat",
                    "-t",
                    scatVendor,
                    "-d",
                    logFilePath,
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
            val pcapInput = tempPcapFile.toInputSource()
            result = ImportPcap.parse(pcapInput, type.name)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        cleanup(tempLogFile, tempPcapFile)

        return result
    }

    private fun cleanup(vararg files: File?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                files.forEach { it?.deleteIgnoreException() }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun isScatAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("scat", "-h"))
            process.waitFor()
            process.exitValue() == 0
        } catch (ignored: Exception) {
            false
        }
    }
}
