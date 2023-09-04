package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.preformatHex
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CDBin
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.nio.ByteBuffer

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
        val splitByPayload = input.split("Payload:")
        return if (splitByPayload.size > 1) {
            splitByPayload.drop(1).map {
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
                val inputStream = it.preformatHex().decodeHex()
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
        val metadata =
            if (list.size == 1) {
                metadataList.first()
            } else {
                metadataList
                    .flatMapIndexed { index, map ->
                        // add a suffix to each key
                        map.map { Pair("${it.key}-$index", it.value) }
                    }
                    .toMap()
                    .toMutableMap()
            }

        return Capabilities(
            lteCombos = lteCombos,
            enDcCombos = enDcCombos,
            nrCombos = nrCombos,
            nrDcCombos = nrDcCombos,
            metadata = metadata
        )
    }

    /**
     * Return the content size of a Qualcomm diag packet. Also set logItem in [capabilities] if
     * available.
     *
     * It supports qualcomm diag packets with or without header.
     *
     * Note: it advances the [byteBuffer].
     */
    fun getQcDiagLogSize(byteBuffer: ByteBuffer, capabilities: Capabilities): Int {
        // Try to read fileSize from the header
        val fileSize = byteBuffer.readUnsignedShort()

        // if fileSize = bufferSize 0xB826 has a header
        if (fileSize != byteBuffer.limit()) {
            // header missing, logSize is buffer size
            byteBuffer.rewind()
            return byteBuffer.limit()
        }

        val logItem = byteBuffer.readUnsignedShort().toString(16).uppercase()
        capabilities.setMetadata("logItem", "0x$logItem")
        if (debug) {
            println("Log Item: 0x$logItem")
        }
        // Skip the rest of the header
        byteBuffer.skipBytes(8)
        return fileSize
    }
}
