package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.model.index.MultiIndexLine
import java.time.Instant
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MultiParsing(
    private val inputsList: List<List<ByteArray>>,
    private val typeList: List<String>,
    private val subTypesList: List<List<String>>,
    private val descriptionList: List<String> = emptyList(),
    private val jsonFormat: Json = Json,
    private val description: String = "",
    private var id: String = UUID.randomUUID().toString()
) {
    val parsingList = parseCapabilities()

    private fun parseCapabilities(): List<Parsing> {
        val parsedCapabilities = mutableListOf<Parsing>()
        val subTypeIterator = subTypesList.iterator()

        for (i in inputsList.indices) {
            val inputs = inputsList[i]
            val type = typeList[i]
            var inputArray = ByteArray(0)
            var inputENDCArray: ByteArray? = null
            var inputNRArray: ByteArray? = null
            var defaultNr = false

            if (type != "H") {
                inputArray = inputs.fold(inputArray) { acc, it -> acc + it }
            } else {
                val subTypes = subTypeIterator.next()
                for (j in inputs.indices) {
                    val subType = subTypes[j]
                    val input = inputs[j]
                    when (subType) {
                        "LTE" -> inputArray = input
                        "ENDC" -> inputENDCArray = input
                        "NR" -> inputNRArray = input
                    }
                }

                if (inputNRArray?.isNotEmpty() == true && inputArray.isEmpty()) {
                    inputArray = inputNRArray
                    defaultNr = true
                }
            }
            val description = descriptionList.getOrElse(i) { "" }

            val parsing =
                Parsing(
                    inputArray,
                    inputENDCArray,
                    inputNRArray,
                    defaultNr,
                    type,
                    description,
                    jsonFormat
                )

            parsedCapabilities.add(parsing)
        }
        return parsedCapabilities
    }

    fun store(libraryIndex: LibraryIndex, path: String, compression: Boolean): MultiIndexLine {
        val multiDir = "$path/multi"
        val outputPath = "$multiDir/$id.json"

        val indexLines = parsingList.map { it.store(libraryIndex, path, compression) }

        val multiIndexLine =
            MultiIndexLine(
                id,
                timestamp = indexLines.lastOrNull()?.timestamp ?: Instant.now().toEpochMilli(),
                description,
                indexLines
            )

        val encodedString = Json.custom().encodeToString(multiIndexLine)

        IO.outputFile(encodedString.toByteArray(), outputPath, compression)
        libraryIndex.addMultiLine(multiIndexLine)
        return multiIndexLine
    }
}
