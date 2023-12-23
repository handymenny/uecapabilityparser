package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.preformatHex
import it.smartphonecombo.uecapabilityparser.extension.readUShortLE
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CDBin
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.InputStream

object ImportQcHelpers {
    private val debug
        get() = Config.getOrDefault("debug", "false").toBoolean()

    private val regexEmptyLine = Regex("^\\s*$", RegexOption.MULTILINE)

    private val regexEmptyOrCommentedLine = Regex("^(#.*)?\\s*$", RegexOption.MULTILINE)

    private fun String.emptyLineIndex(): Int {
        return regexEmptyLine.find(this)?.range?.first ?: this.length
    }

    private fun String.notHexLineIndex(): Int {
        val res = indexOfFirst {
            if (it.isDigit() || it.isWhitespace()) {
                false
            } else if (it.isLetter()) {
                it != 'x' && it !in 'A'..'F' && it !in 'a'..'f'
            } else {
                it != ','
            }
        }
        return if (res == -1) length else res
    }

    private fun splitHex(input: String): List<String> {
        // Try payload Prefix
        var splitByPrefix = input.split("Payload:")
        if (splitByPrefix.size == 1) {
            // Try scat Prefix
            splitByPrefix = input.split("CA Combos Raw:")
        }
        if (splitByPrefix.size == 1) {
            // Try 0x9801 Prefix
            splitByPrefix = input.split("0x9801")
            if (splitByPrefix.size > 1) {
                // Re-add 0x9801
                splitByPrefix = splitByPrefix.map { "0x9801$it" }
            }
        }

        return if (splitByPrefix.size > 1) {
            splitByPrefix.drop(1).map {
                it.substring(0, minOf(it.emptyLineIndex(), it.notHexLineIndex()))
            }
        } else {
            input.split(regexEmptyOrCommentedLine)
        }
    }

    fun parseMultiple0xB826(input: String): Capabilities {
        return parseMultipleQcDiag(input, false)
    }

    fun parseMultiple0xBOCD(input: String): Capabilities {
        return parseMultipleQcDiag(input, true)
    }

    private fun parseMultipleQcDiag(input: String, lte: Boolean): Capabilities {
        val inputArray = splitHex(input)
        val list = mutableListWithCapacity<Capabilities>(inputArray.size)
        val importer = if (lte) Import0xB0CDBin else Import0xB826
        for (it in inputArray) {
            if (it.isBlank()) continue
            try {
                val inputStream = it.preformatHex().decodeHex().toInputSource()
                list.add(importer.parse(inputStream))
            } catch (err: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid hexdump", err)
            }
        }

        val lteCombos = if (lte) list.flatMap(Capabilities::lteCombos) else emptyList()
        val enDcCombos = if (!lte) list.flatMap(Capabilities::enDcCombos) else emptyList()
        val nrCombos = if (!lte) list.flatMap(Capabilities::nrCombos) else emptyList()
        val nrDcCombos = if (!lte) list.flatMap(Capabilities::nrDcCombos) else emptyList()

        val metadataList = list.map(Capabilities::metadata)

        val capabilities =
            Capabilities(
                lteCombos = lteCombos,
                enDcCombos = enDcCombos,
                nrCombos = nrCombos,
                nrDcCombos = nrDcCombos,
                metadata = metadataList.firstOrNull() ?: mutableMapOf()
            )

        if (metadataList.size > 1) {
            val keys = metadataList.flatMap { it.keys }.distinct()

            // Add missing keys to maps
            metadataList.forEach { map -> keys.filter { it !in map }.forEach { map[it] = "" } }

            // Add additional metadata
            metadataList.drop(1).forEach { map ->
                map.forEach { (key, value) -> capabilities.addMetadata(key, value) }
            }
        }

        return capabilities
    }

    /**
     * Return the content size of a Qualcomm diag packet. Also set logItem in [capabilities] if
     * available.
     *
     * It supports qualcomm diag packets with or without header.
     *
     * Note: it advances the [stream].
     */
    fun getQcDiagLogSize(stream: InputStream, streamLength: Int, capabilities: Capabilities): Int {
        // Set mark for future reset
        stream.mark(32)

        // Try to read fileSize from the header
        var fileSize = stream.readUShortLE()

        // if fileSize = stream length 0xB826/0xB0CD has a standard header
        if (fileSize != streamLength) {
            // check if there's an additional header (0x9801 + 8 bytes + file length + header)
            if (fileSize == 0x0198) {
                stream.skipBytes(10)
                fileSize = stream.readUShortLE()
            } else {
                // header missing, logSize is stream length
                stream.reset()
                return streamLength
            }
        }

        val logItem = stream.readUShortLE().toString(16).uppercase()
        capabilities.setMetadata("logItem", "0x$logItem")
        if (debug) {
            echoSafe("Log Item: 0x$logItem")
        }
        // Skip the rest of the header
        stream.skipBytes(8)
        return fileSize
    }
}
