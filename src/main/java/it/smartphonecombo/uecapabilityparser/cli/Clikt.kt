package it.smartphonecombo.uecapabilityparser.cli

import com.ericsson.mts.asn1.KotlinJsonFormatWriter
import com.ericsson.mts.asn1.converter.AbstractConverter
import com.ericsson.mts.asn1.converter.ConverterNSG
import com.ericsson.mts.asn1.converter.ConverterOsix
import com.ericsson.mts.asn1.converter.ConverterQcat
import com.ericsson.mts.asn1.converter.ConverterWireshark
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.inputStream
import it.smartphonecombo.uecapabilityparser.extension.indexOf
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
import it.smartphonecombo.uecapabilityparser.util.MtsAsn1Helpers
import it.smartphonecombo.uecapabilityparser.util.Output
import java.io.InputStreamReader
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object Clikt : CliktCommand(name = "UE Capability Parser", printHelpOnEmptyArgs = true) {
    private val input by
        option("-i", "--input", help = "Main capability file").inputStream().required()

    private val inputNR by option("--inputNR", help = "NR UE Capability file").inputStream()

    private val inputENDC by option("--inputENDC", help = "ENDC UE Capability file").inputStream()

    private val defaultNR by
        option("--nr", "--defaultNR", help = "Main capability input is NR (otherwise LTE)").flag()

    private val multiple0xB826 by
        option(
                "--multi",
                "--multiple0xB826",
                help =
                    """Use this option if input contains several 0xB826 hexdumps separated by blank 
                        |lines and optionally prefixed with "Payload :""""
                        .trimMargin()
            )
            .flag()

    private val type by
        option(
                "-t",
                "--type",
                help =
                    """Type of capability. Valid values are: H (UE Capability Hex Dump), 
                        |W (Wireshark UE Capability Information), N (NSG UE Capability Information), 
                        |C (Carrier policy), CNR (NR Cap Prune), E (28874 nvitem binary, decompressed), 
                        |Q (QCAT 0xB0CD), QNR (0xB826 hexdump), M (MEDIATEK CA_COMB_INFO), 
                        |O (OSIX UE Capability Information), QC (QCAT UE Capability Information)."""
                        .trimMargin()
            )
            .choice("H", "W", "N", "C", "CNR", "E", "Q", "QNR", "M", "O", "QC", ignoreCase = true)
            .required()

    private val csv by
        option(
            "-c",
            "--csv",
            help =
                """Output a csv, if "-" is the csv will be output to standard output.
                    |Some parsers output multiple CSVs, in these cases "-LTE", "-NR", "-EN-DC",
                    | "-NR-DC" will be added before the extension."""
                    .trimMargin()
        )

    private val ueLog by
        option(
            "-l",
            "--uelog",
            help =
                """Output the uelog, if "-" is specified the uelog will be output to standard output."""
        )

    private val debug by option("-d", "--debug", help = "Print debug info.").flag()

    override fun run() {
        Config["debug"] = debug.toString()
        val comboList = parsing()
        csv?.let {
            val csvOutput = if (it == "-") null else it
            csvOutput(comboList, csvOutput)
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
                "H" -> return ueCapabilityHandling()
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

    private fun ueCapabilityHandling(): Capabilities {
        val imports = ImportCapabilityInformation
        val inputMainText = input.reader().use(InputStreamReader::readText)
        val inputNRText = inputNR?.reader()?.use(InputStreamReader::readText)
        val inputENDCText = inputENDC?.reader()?.use(InputStreamReader::readText)

        val ratContainerMap =
            if (type == "H") {
                jsonFromHex(inputMainText, inputNRText, inputENDCText)
            } else {
                var combined = inputMainText
                // Combine all inputs
                inputENDCText?.let { combined += it }
                inputNRText?.let { combined += it }
                jsonFromText(combined)
            }
        val jsonOutput = JsonObject(ratContainerMap)

        if (ueLog != null) {
            val ueLogOutput = if (ueLog == "-") null else ueLog
            Output.outputFileOrStdout(jsonOutput.toString(), ueLogOutput)
        }

        val jsonEutra = jsonOutput.getOrDefault(Rat.EUTRA.toString(), null) as? JsonObject
        val jsonEutraNr = jsonOutput.getOrDefault(Rat.EUTRA_NR.toString(), null) as? JsonObject
        val jsonNr = jsonOutput.getOrDefault(Rat.NR.toString(), null) as? JsonObject

        return imports.parse(jsonEutra, jsonEutraNr, jsonNr)
    }

    private fun jsonFromHex(
        inputMainText: String,
        inputNRText: String?,
        inputENDCText: String?
    ): Map<String, JsonElement> {
        val ratContainerMap = mutableMapOf<String, JsonElement>()
        val defaultRat = if (defaultNR) Rat.NR else Rat.EUTRA
        ratContainerMap += MtsAsn1Helpers.getUeCapabilityJsonFromHex(defaultRat, inputMainText)
        if (inputNRText != null) {
            ratContainerMap += MtsAsn1Helpers.getUeCapabilityJsonFromHex(Rat.NR, inputNRText)
        }
        if (inputENDCText != null) {
            ratContainerMap +=
                MtsAsn1Helpers.getUeCapabilityJsonFromHex(Rat.EUTRA_NR, inputENDCText)
        }

        return ratContainerMap
    }

    private fun jsonFromText(input: String): Map<String, JsonElement> {

        lateinit var eutraIdentifier: Regex
        lateinit var nrIdentifier: Regex
        lateinit var mrdcIdentifier: Regex
        lateinit var converter: AbstractConverter

        when (type) {
            "W" -> {
                eutraIdentifier = "${Rat.EUTRA.ratCapabilityIdentifier}\\s".toRegex()
                nrIdentifier = "${Rat.NR.ratCapabilityIdentifier}\\s".toRegex()
                mrdcIdentifier = "${Rat.EUTRA_NR.ratCapabilityIdentifier}\\s".toRegex()
                converter = ConverterWireshark()
            }
            "N" -> {
                eutraIdentifier = "rat-Type : ${Rat.EUTRA}\\s".toRegex()
                nrIdentifier = "rat-Type : ${Rat.NR}\\s".toRegex()
                mrdcIdentifier = "rat-Type : ${Rat.EUTRA_NR}\\s".toRegex()
                converter = ConverterNSG()
            }
            "O" -> {
                eutraIdentifier = "${Rat.EUTRA.ratCapabilityIdentifier}\\s".toRegex()
                nrIdentifier = "${Rat.NR.ratCapabilityIdentifier}\\s".toRegex()
                mrdcIdentifier = "${Rat.EUTRA_NR.ratCapabilityIdentifier}\\s".toRegex()
                converter = ConverterOsix()
            }
            "QC" -> {
                eutraIdentifier = "value ${Rat.EUTRA.ratCapabilityIdentifier} ::=\\s".toRegex()
                nrIdentifier = "value ${Rat.NR.ratCapabilityIdentifier} ::=\\s".toRegex()
                mrdcIdentifier = "value ${Rat.EUTRA_NR.ratCapabilityIdentifier} ::=\\s".toRegex()
                converter = ConverterQcat()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
        val formatWriter = KotlinJsonFormatWriter()
        val ratContainerMap = mutableMapOf<String, JsonElement>()

        val list =
            listOf(
                    Rat.EUTRA to input.indexOf(eutraIdentifier),
                    Rat.EUTRA_NR to input.indexOf(mrdcIdentifier),
                    Rat.NR to input.indexOf(nrIdentifier),
                )
                .filter { it.second != -1 }
                .sortedBy { it.second }
        var eutra = ""
        var eutraNr = ""
        var nr = ""
        for (i in list.indices) {
            val (rat, start) = list[i]
            val end = list.getOrNull(i + 1)?.second ?: input.length
            when (rat) {
                Rat.EUTRA ->
                    eutra =
                        input.substring(
                            start + eutraIdentifier.toString().length - 1,
                            end,
                        )
                Rat.EUTRA_NR ->
                    eutraNr =
                        input.substring(
                            start + mrdcIdentifier.toString().length - 1,
                            end,
                        )
                Rat.NR -> nr = input.substring(start + nrIdentifier.toString().length - 1, end)
                else -> {
                    // Do nothing
                }
            }
        }
        if (eutra.isNotBlank()) {
            MtsAsn1Helpers.getAsn1Converter(Rat.EUTRA, converter)
                .convert(
                    Rat.EUTRA.ratCapabilityIdentifier,
                    eutra.byteInputStream(),
                    formatWriter,
                )
            formatWriter.jsonNode?.let { ratContainerMap.put(Rat.EUTRA.toString(), it) }
        }
        if (eutraNr.isNotBlank() || nr.isNotBlank()) {
            val nrConverter = MtsAsn1Helpers.getAsn1Converter(Rat.NR, converter)
            if (eutraNr.isNotBlank()) {
                nrConverter.convert(
                    Rat.EUTRA_NR.ratCapabilityIdentifier,
                    eutraNr.byteInputStream(),
                    formatWriter,
                )
                formatWriter.jsonNode?.let { ratContainerMap.put(Rat.EUTRA_NR.toString(), it) }
            }
            if (nr.isNotBlank()) {
                nrConverter.convert(
                    Rat.NR.ratCapabilityIdentifier,
                    nr.byteInputStream(),
                    formatWriter,
                )
                formatWriter.jsonNode?.let { ratContainerMap.put(Rat.NR.toString(), it) }
            }
        }
        return ratContainerMap
    }

    private fun csvOutput(comboList: Capabilities, csvPath: String?) {
        if (type !in arrayOf("W", "N", "H", "QNR", "CNR", "QC", "O")) {
            return Output.outputFileOrStdout(Output.toCsv(comboList), csvPath)
        }

        val lteCombos = comboList.lteCombos
        if (lteCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(lteCombos),
                csvPath?.let { Output.appendBeforeExtension(it, "-LTE") }
            )
        }
        val nrCombos = comboList.nrCombos
        if (nrCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(nrCombos),
                csvPath?.let { Output.appendBeforeExtension(it, "-NR") }
            )
        }
        val enDcCombos = comboList.enDcCombos
        if (enDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(enDcCombos),
                csvPath?.let { Output.appendBeforeExtension(it, "-EN-DC") }
            )
        }
        val nrDcCombos = comboList.nrDcCombos
        if (nrDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(nrDcCombos),
                csvPath?.let { Output.appendBeforeExtension(it, "-NR-DC") }
            )
        }
    }
}
