package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CD
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilityInformation
import it.smartphonecombo.uecapabilityparser.importer.ImportLteCarrierPolicy
import it.smartphonecombo.uecapabilityparser.importer.ImportMTKLte
import it.smartphonecombo.uecapabilityparser.importer.ImportNrCapPrune
import it.smartphonecombo.uecapabilityparser.importer.ImportNvItem
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.util.Import0xB826Helpers.parseMultiple0xB826
import it.smartphonecombo.uecapabilityparser.util.ImportCapabilitiesHelpers.convertUeCapabilityToJson
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant
import kotlin.system.measureTimeMillis
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

data class Parsing(
    private val input: InputStream,
    private val inputNR: InputStream?,
    private val inputENDC: InputStream?,
    private val defaultNR: Boolean = false,
    private val multiple0xB826: Boolean = false,
    private val type: String,
    private val jsonFormat: Json = Json
) {
    private var jsonUeCap: JsonObject? = null
    val capabilities = parseCapabilitiesAndSetMetadata()

    val ueLog: String
        get() =
            jsonUeCap?.let { jsonFormat.encodeToString(jsonUeCap) }
                ?: input.reader().use(InputStreamReader::readText)

    private fun parseCapabilitiesAndSetMetadata(): Capabilities {
        val capabilities: Capabilities
        val processTime = measureTimeMillis { capabilities = parseCapabilities() }
        capabilities.setMetadata("parser-version", Property.getProperty("project.version") ?: "")
        capabilities.setMetadata("log-type", type)
        capabilities.setMetadata("timestamp", Instant.now())
        capabilities.setMetadata("processing-time", "${processTime}ms")
        return capabilities
    }

    private fun parseCapabilities(): Capabilities {
        val imports =
            when (type) {
                "E" -> ImportNvItem
                "C" -> ImportLteCarrierPolicy
                "CNR" -> ImportNrCapPrune
                "Q" -> Import0xB0CD
                "M" -> ImportMTKLte
                "QNR" -> Import0xB826
                "W",
                "N",
                "O",
                "QC",
                "H" -> ImportCapabilityInformation
                else -> return Capabilities()
            }

        if (imports == Import0xB826) {
            return parseMultiple0xB826(
                input.reader().use(InputStreamReader::readText),
                multiple0xB826
            )
        }

        if (imports == ImportCapabilityInformation) {
            jsonUeCap = convertUeCapabilityToJson(type, input, inputNR, inputENDC, defaultNR)
            val eutra = jsonUeCap?.get(Rat.EUTRA.toString()) as? JsonObject
            val eutraNr = jsonUeCap?.get(Rat.EUTRA_NR.toString()) as? JsonObject
            val nr = jsonUeCap?.get(Rat.NR.toString()) as? JsonObject

            return (imports as ImportCapabilityInformation).parse(eutra, eutraNr, nr)
        }

        return input.use { imports.parse(it) }
    }
}
