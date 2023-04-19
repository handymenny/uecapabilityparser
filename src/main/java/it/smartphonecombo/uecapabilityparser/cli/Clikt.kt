package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.inputStream
import it.smartphonecombo.uecapabilityparser.extension.appendBeforeExtension
import it.smartphonecombo.uecapabilityparser.importer.Import0xB0CD
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilityInformation
import it.smartphonecombo.uecapabilityparser.importer.ImportLteCarrierPolicy
import it.smartphonecombo.uecapabilityparser.importer.ImportMTKLte
import it.smartphonecombo.uecapabilityparser.importer.ImportNrCapPrune
import it.smartphonecombo.uecapabilityparser.importer.ImportNvItem
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Import0xB826Helpers
import it.smartphonecombo.uecapabilityparser.util.ImportCapabilitiesHelpers.convertUeCapabilityToJson
import it.smartphonecombo.uecapabilityparser.util.Output
import it.smartphonecombo.uecapabilityparser.util.Property
import java.io.InputStreamReader
import java.time.Instant
import kotlin.system.measureTimeMillis
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object Clikt : CliktCommand(name = "UE Capability Parser", printHelpOnEmptyArgs = true) {
    init {
        versionOption(version = Property.getProperty("project.version") ?: "")
    }

    private val input by option("-i", "--input", help = HelpMessage.INPUT).inputStream().required()

    private val inputNR by option("--inputNR", help = HelpMessage.INPUT_NR).inputStream()

    private val inputENDC by option("--inputENDC", help = HelpMessage.INPUT_ENDC).inputStream()

    private val defaultNR by option("--nr", "--defaultNR", help = HelpMessage.DEFAULT_NR).flag()

    private val multiple0xB826 by
        option("--multi", "--multiple0xB826", help = HelpMessage.MULTIPLE_0XB826).flag()

    private val type by
        option("-t", "--type", help = HelpMessage.TYPE)
            .choice("H", "W", "N", "C", "CNR", "E", "Q", "QNR", "M", "O", "QC", ignoreCase = true)
            .required()

    private val csv by option("-c", "--csv", help = HelpMessage.CSV, metavar = "FILE")

    private val json by option("-j", "--json", help = HelpMessage.JSON, metavar = "FILE")

    private val jsonPrettyPrint by
        option("--json-pretty-print", help = HelpMessage.JSON_PRETTY_PRINT).flag()

    private val ueLog by option("-l", "--uelog", help = HelpMessage.UE_LOG, metavar = "FILE")

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    private lateinit var jsonFormat: Json

    override fun run() {
        Config["debug"] = debug.toString()
        jsonFormat = if (jsonPrettyPrint) Json { prettyPrint = true } else Json

        val comboList: Capabilities
        val processTime = measureTimeMillis { comboList = parsing() }
        comboList.setMetadata("parser-version", Property.getProperty("project.version") ?: "")
        comboList.setMetadata("log-type", type)
        comboList.setMetadata("timestamp", Instant.now())
        comboList.setMetadata("processing-time", "${processTime}ms")

        csv?.let {
            val csvOutput = if (it == "-") null else it
            csvOutput(comboList, csvOutput)
        }
        json?.let {
            val jsvOutput = if (it == "-") null else it
            Output.outputFileOrStdout(jsonFormat.encodeToString(comboList), jsvOutput)
        }
    }

    private fun parsing(): Capabilities {
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
                "H" -> {
                    val jsonOutput =
                        convertUeCapabilityToJson(type, input, inputNR, inputENDC, defaultNR)
                    return ueCapabilityHandling(jsonOutput)
                }
                else -> return Capabilities()
            }

        if (ueLog != null) {
            val ueLogOutput = if (ueLog == "-") null else ueLog
            Output.outputFileOrStdout(input.reader().use(InputStreamReader::readText), ueLogOutput)
        }

        return if (type == "QNR") {
            Import0xB826Helpers.parseMultiple0xB826(
                input.reader().use(InputStreamReader::readText),
                multiple0xB826
            )
        } else {
            input.use { imports.parse(it) }
        }
    }

    private fun ueCapabilityHandling(input: JsonObject): Capabilities {
        val imports = ImportCapabilityInformation
        if (ueLog != null) {
            val ueLogOutput = if (ueLog == "-") null else ueLog
            Output.outputFileOrStdout(jsonFormat.encodeToString(input), ueLogOutput)
        }

        val jsonEutra = input.getOrDefault(Rat.EUTRA.toString(), null) as? JsonObject
        val jsonEutraNr = input.getOrDefault(Rat.EUTRA_NR.toString(), null) as? JsonObject
        val jsonNr = input.getOrDefault(Rat.NR.toString(), null) as? JsonObject

        return imports.parse(jsonEutra, jsonEutraNr, jsonNr)
    }

    private fun csvOutput(comboList: Capabilities, csvPath: String?) {
        if (type !in arrayOf("W", "N", "H", "QNR", "CNR", "QC", "O")) {
            return Output.outputFileOrStdout(Output.toCsv(comboList), csvPath)
        }

        val lteCombos = comboList.lteCombos
        if (lteCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(lteCombos),
                csvPath?.appendBeforeExtension("-LTE")
            )
        }
        val nrCombos = comboList.nrCombos
        if (nrCombos.isNotEmpty()) {
            Output.outputFileOrStdout(Output.toCsv(nrCombos), csvPath?.appendBeforeExtension("-NR"))
        }
        val enDcCombos = comboList.enDcCombos
        if (enDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(enDcCombos),
                csvPath?.appendBeforeExtension("-EN-DC")
            )
        }
        val nrDcCombos = comboList.nrDcCombos
        if (nrDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(nrDcCombos),
                csvPath?.appendBeforeExtension("-NR-DC")
            )
        }
    }
}
