package it.smartphonecombo.uecapabilityparser.util

import io.javalin.http.UploadedFile
import it.smartphonecombo.uecapabilityparser.extension.commonPrefix
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.isEmpty
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportNsgJson
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportPcap
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.io.NullInputSource
import it.smartphonecombo.uecapabilityparser.io.SequenceInputSource
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.model.index.MultiIndexLine
import it.smartphonecombo.uecapabilityparser.server.RequestMultiPart
import java.time.Instant
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MultiParsing(
    private val inputsList: List<List<InputSource>>,
    private val typeList: List<LogType>,
    private val subTypesList: List<List<String>>,
    private val descriptionList: List<String> = emptyList(),
    private val jsonFormat: Json = Json,
    private var description: String = "",
    private var id: String = UUID.randomUUID().toString()
) {
    private val subMultiParsingList = mutableListOf<MultiParsing>()
    val parsingList = parseCapabilities()

    fun getMultiCapabilities(): MultiCapabilities {
        val capabilities = parsingList.map { it.capabilities }
        return MultiCapabilities(capabilities, description, id)
    }

    private fun parseCapabilities(): List<Parsing> {
        val parsedCapabilities = mutableListOf<Parsing>()
        val subTypeIterator = subTypesList.iterator()

        for (i in inputsList.indices) {
            val inputs = inputsList[i]
            val type = typeList[i]
            var inputSource: InputSource = inputs.first()
            var inputENDCSource: InputSource? = null
            var inputNRSource: InputSource? = null
            var defaultRat = Rat.EUTRA
            val description = descriptionList.getOrElse(i) { "" }

            if (type in LogType.multiImporter) {
                val subMultiParsing =
                    when (type) {
                        LogType.P -> ImportPcap.parse(inputs.first())
                        LogType.NSG -> ImportNsgJson.parse(inputs.first())
                        else -> ImportScat.parse(inputs.first(), type)
                    }
                if (subMultiParsing != null) {
                    subMultiParsing.description = description
                    subMultiParsingList.add(subMultiParsing)
                }
                continue
            }

            if (type == LogType.H) {
                val subTypes = subTypeIterator.next()
                inputSource = NullInputSource
                for (j in inputs.indices) {
                    val subType = subTypes[j]
                    val input = inputs[j]
                    when (subType) {
                        "LTE" -> inputSource = input
                        "ENDC" -> inputENDCSource = input
                        "NR" -> inputNRSource = input
                    }
                }

                if (inputSource.isEmpty()) {
                    if (inputNRSource?.isEmpty() == false) {
                        inputSource = inputNRSource
                        inputNRSource = null
                        defaultRat = Rat.NR
                    } else if (inputENDCSource?.isEmpty() == false) {
                        inputSource = inputENDCSource
                        inputENDCSource = null
                        defaultRat = Rat.EUTRA_NR
                    }
                }
            } else if (inputs.size > 1) {
                inputSource = SequenceInputSource(inputs)
            }

            val parsing =
                Parsing(
                    inputSource,
                    inputNRSource,
                    inputENDCSource,
                    defaultRat,
                    type,
                    description,
                    jsonFormat
                )

            parsedCapabilities.add(parsing)
        }
        parsedCapabilities.addAll(subMultiParsingList.flatMap { it.parsingList })
        return parsedCapabilities
    }

    fun store(libraryIndex: LibraryIndex, path: String, compression: Boolean): MultiIndexLine {
        val multiDir = "$path/multi"
        val outputPath = "$multiDir/$id.json"

        val indexLines = parsingList.map { it.store(libraryIndex, path, compression) }
        val indexLineIds = indexLines.map(IndexLine::id)
        val multiIndexLine =
            MultiIndexLine(
                id,
                timestamp = indexLines.lastOrNull()?.timestamp ?: Instant.now().toEpochMilli(),
                description,
                indexLineIds,
                compression
            )

        val encodedString = Json.custom().encodeToString(multiIndexLine)
        IOUtils.outputFile(encodedString.toByteArray(), outputPath, compression)
        libraryIndex.addMultiLine(multiIndexLine)

        return multiIndexLine
    }

    companion object {
        fun fromRequest(reqList: List<RequestMultiPart>, files: List<UploadedFile>): MultiParsing? {
            val inputsList: MutableList<List<InputSource>> = mutableListWithCapacity(reqList.size)
            val typeList: MutableList<LogType> = mutableListWithCapacity(reqList.size)
            val subTypesList: MutableList<List<String>> = mutableListWithCapacity(reqList.size)
            val descriptionList: MutableList<String> = mutableListWithCapacity(reqList.size)

            reqList.forEach { req ->
                val inputs = req.inputIndexes.map { index -> files[index].toInputSource() }
                val type = req.type
                val subTypes = req.subTypes
                val description = req.description

                if (inputs.isEmpty() || type == LogType.INVALID) return@forEach

                inputsList.add(inputs)
                typeList.add(type)
                if (type == LogType.H) subTypesList.add(subTypes)
                descriptionList.add(description.trim())
            }

            if (inputsList.isEmpty()) return null

            val uniqueDesc = descriptionList.distinct()
            val commonPrefix = uniqueDesc.commonPrefix(true).dropLastWhile { !it.isWhitespace() }
            val description =
                if (commonPrefix.length < 3) {
                    uniqueDesc.joinToString(" ")
                } else {
                    val trunc = uniqueDesc.joinToString(" ") { it.substring(commonPrefix.length) }
                    commonPrefix + trunc
                }

            return MultiParsing(
                inputsList,
                typeList,
                subTypesList,
                descriptionList,
                description = description
            )
        }
    }
}
