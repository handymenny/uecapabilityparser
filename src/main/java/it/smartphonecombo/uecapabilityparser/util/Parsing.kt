package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CDBin
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilityInformation
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.server.RequestParse
import it.smartphonecombo.uecapabilityparser.util.ImportCapabilitiesHelpers.convertUeCapabilityToJson
import it.smartphonecombo.uecapabilityparser.util.ImportQcHelpers.parseMultiple0xB826
import it.smartphonecombo.uecapabilityparser.util.ImportQcHelpers.parseMultiple0xBOCD
import java.time.Instant
import kotlin.system.measureTimeMillis
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class Parsing(
    private val input: ByteArray,
    private val inputNR: ByteArray?,
    private val inputENDC: ByteArray?,
    private val defaultNR: Boolean = false,
    private val type: LogType,
    private val description: String = "",
    private val jsonFormat: Json = Json
) {
    private var jsonUeCap: JsonObject? = null
    val capabilities = parseCapabilitiesAndSetMetadata()

    val ueLog: String
        get() = jsonUeCap?.let { jsonFormat.encodeToString(jsonUeCap) } ?: input.decodeToString()

    private fun parseCapabilitiesAndSetMetadata(): Capabilities {
        val capabilities: Capabilities
        val processTime = measureTimeMillis { capabilities = parseCapabilities() }
        capabilities.logType = type
        capabilities.timestamp = Instant.now().toEpochMilli()
        capabilities.setMetadata("processingTime", "${processTime}ms")
        if (defaultNR) capabilities.setMetadata("defaultNR", "true")

        // Set description
        if (description.isNotEmpty()) {
            capabilities.setMetadata("description", description)
        }

        return capabilities
    }

    private fun parseCapabilities(): Capabilities {
        val imports = LogType.getImporter(type) ?: return Capabilities()

        if (imports == Import0xB826) {
            return parseMultiple0xB826(input.decodeToString())
        }

        if (imports == Import0xB0CDBin) {
            return parseMultiple0xBOCD(input.decodeToString())
        }

        if (imports == ImportCapabilityInformation) {
            jsonUeCap = convertUeCapabilityToJson(type, input, inputNR, inputENDC, defaultNR)
            val eutra = jsonUeCap?.get(Rat.EUTRA.toString()) as? JsonObject
            val eutraNr = jsonUeCap?.get(Rat.EUTRA_NR.toString()) as? JsonObject
            val nr = jsonUeCap?.get(Rat.NR.toString()) as? JsonObject

            return (imports as ImportCapabilityInformation).parse(eutra, eutraNr, nr)
        }

        return imports.parse(input)
    }

    fun store(libraryIndex: LibraryIndex?, path: String, compression: Boolean): IndexLine {
        val inputDir = "$path/input"
        val outputDir = "$path/output"
        val id = capabilities.id
        val inputs = arrayOf(input, inputNR, inputENDC)
        val inputsPath = mutableListOf<String>()

        inputs.filterNotNull().filterNot(ByteArray::isEmpty).forEachIndexed { index, data ->
            val fileName = "$id-$index"
            val inputPath = "$inputDir/$fileName"
            IOUtils.outputFile(data, inputPath, compression)
            inputsPath.add(fileName)
        }

        val encodedString = Json.custom().encodeToString(capabilities)
        val outputPath = "$outputDir/$id.json"
        IOUtils.outputFile(encodedString.toByteArray(), outputPath, compression)
        val indexLine =
            IndexLine(
                id,
                capabilities.timestamp,
                capabilities.getStringMetadata("description") ?: "",
                inputsPath,
                compression,
                capabilities.getStringMetadata("defaultNR").toBoolean(),
                capabilities.parserVersion
            )
        libraryIndex?.addLine(indexLine)
        return indexLine
    }

    companion object {
        fun fromRequest(req: RequestParse): Parsing? {
            val defaultNR = req.defaultNR || req.input == null

            if (req.input == null && req.inputNR == null || req.type == LogType.INVALID) {
                return null
            }

            return Parsing(
                req.input ?: req.inputNR!!,
                if (defaultNR) null else req.inputNR,
                req.inputENDC,
                defaultNR,
                req.type,
                req.description
            )
        }
    }
}
