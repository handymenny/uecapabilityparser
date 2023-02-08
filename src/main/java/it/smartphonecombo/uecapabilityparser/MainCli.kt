package it.smartphonecombo.uecapabilityparser

import com.ericsson.mts.asn1.KotlinJsonFormatWriter
import com.ericsson.mts.asn1.converter.ConverterNSG
import com.ericsson.mts.asn1.converter.ConverterOsix
import com.ericsson.mts.asn1.converter.ConverterWireshark
import com.github.holgerbrandl.jsonbuilder.json
import it.smartphonecombo.uecapabilityparser.Utility.getAsn1Converter
import it.smartphonecombo.uecapabilityparser.Utility.indexOf
import it.smartphonecombo.uecapabilityparser.Utility.multipleParser
import it.smartphonecombo.uecapabilityparser.Utility.outputFile
import it.smartphonecombo.uecapabilityparser.Utility.preformatHexData
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.Rat
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import it.smartphonecombo.uecapabilityparser.importer.Tshark
import it.smartphonecombo.uecapabilityparser.importer.lte.Import0xB0CD
import it.smartphonecombo.uecapabilityparser.importer.lte.ImportCarrierPolicy
import it.smartphonecombo.uecapabilityparser.importer.lte.ImportMTKLte
import it.smartphonecombo.uecapabilityparser.importer.lte.ImportNvItem
import it.smartphonecombo.uecapabilityparser.importer.ltenr.ImportCellularPro
import it.smartphonecombo.uecapabilityparser.importer.ltenr.ImportNsg
import it.smartphonecombo.uecapabilityparser.importer.ltenr.ImportWireshark
import it.smartphonecombo.uecapabilityparser.importer.nr.Import0xB826
import it.smartphonecombo.uecapabilityparser.importer.nr.ImportCapPrune
import it.smartphonecombo.uecapabilityparser.newEngine.ImportCapabilityInformationJson
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

/**
 * The Class Main.
 *
 * @author handy
 */
internal object MainCli {
    @JvmStatic
    fun main(args: Array<String>) {
        val options = Options()

        Option("h", "help", false, "Print this help message.").also {
            options.addOption(it)
        }

        Option("i", "input", true, "Main capability file.").also {
            it.isRequired = true
            options.addOption(it)
        }

        Option("inputNR", true, "NR UE Capability file.").also {
            options.addOption(it)
        }

        Option("inputENDC", true, "ENDC UE Capability file.").also {
            options.addOption(it)
        }

        Option("nr", "defaultNR", false, "Main capability input is NR (otherwise LTE).").also {
            options.addOption(it)
        }

        Option(
            "multi",
            "multiple0xB826",
            false,
            "Use this option if input contains several 0xB826 hexdumps separated by blank lines and optionally prefixed with \"Payload :\"."
        ).also {
            options.addOption(it)
        }

        Option(
            "t",
            "type",
            true,
            "Type of capability.\nValid values are:\nH (UE Capability Hex Dump)\nW (Wireshark UE Capability Information)\nN (NSG UE Capability Information)\nP (CellularPro UE Capability Information)\nC (Carrier policy)\nCNR (NR Cap Prune)\nE (28874 nvitem binary, decompressed)\nQ (QCAT 0xB0CD)\nQNR (0xB826 hexdump)\nM (MEDIATEK CA_COMB_INFO)\nO (OSIX UE Capability Information)."
        ).also {
            it.isRequired = true
            options.addOption(it)
        }

        Option(
            "l", "uelog", true, "Output the uelog, if no file specified the uelog will be output to standard output."
        ).also {
            it.setOptionalArg(true)
            options.addOption(it)
        }

        Option("d", "debug", false, "Print debug info.").also {
            options.addOption(it)
        }

        Option(
            "T", "TsharkPath", true, "Set tshark path. (Tshark is used for H type)"
        ).also {
            options.addOption(it)
        }

        Option(
            "n", "newEngine", false, "Use the new experimental engine. It works only with type H, W, N, O."
        ).also {
            options.addOption(it)
        }

        Option(
            "f", "format", true, "Output format to use. Valid options are: csv, json-csv, json-compact. (Default: csv)"
        ).also {
            it.args = 1
            options.addOption(it)
        }

        Option(
            "o",
            "out-file",
            false,
            "Optional filename to output to, otherwise outputs to stdout. Don't include an extension: this is decided by the '--format' option."
        ).also {
            options.addOption(it)
        }

        val parser: CommandLineParser = DefaultParser()
        val formatter = HelpFormatter()
        val cmd: CommandLine
        try {
            cmd = parser.parse(options, args)
            val flags = Config
            if (cmd.hasOption("help")) {
                formatter.printHelp("ueCapabilityParser", options)
                return
            }
            if (cmd.hasOption("debug")) {
                flags["debug"] = "true"
            }
            if (cmd.hasOption("TsharkPath")) {
                flags["TsharkPath"] = cmd.getOptionValue("TsharkPath")
            }
            val typeLog = cmd.getOptionValue("type")

            val comboList: Capabilities =
                if (cmd.hasOption("newEngine") && (typeLog == "H" || typeLog == "N" || typeLog == "W" || typeLog == "O")) {
                    newEngine(cmd, typeLog)
                } else {
                    oldEngine(cmd, typeLog)
                }


            val baseOutputFileName: String? = cmd.getOptionValue("out-file");
            val outputFormat: String? = cmd.getOptionValue("format")

            when ((outputFormat ?: "").lowercase()) {
                "", "csv" -> {
                    val fileName = if (baseOutputFileName == null) null else "$baseOutputFileName.csv"

                    if (!(arrayOf("W", "N", "H", "P", "QNR", "O").contains(typeLog))) {
                        outputFile(
                            Utility.toCsv(comboList), fileName
                        )
                    } else {
                        val lteCombos = comboList.lteCombos
                        if (!lteCombos.isNullOrEmpty()) {
                            outputFile(Utility.toCsv(lteCombos), fileName?.let {
                                Utility.appendBeforeExtension(it, "-LTE")
                            })
                        }

                        val nrCombos = comboList.nrCombos
                        if (!nrCombos.isNullOrEmpty()) {
                            outputFile(Utility.toCsv(nrCombos), fileName?.let {
                                Utility.appendBeforeExtension(it, "-NR")
                            })
                        }

                        val enDcCombos = comboList.enDcCombos
                        if (!enDcCombos.isNullOrEmpty()) {
                            outputFile(Utility.toCsv(enDcCombos), fileName?.let {
                                Utility.appendBeforeExtension(it, "-EN-DC")
                            })
                        }
                    }
                }

                "json-csv" -> {
                    var outputJson = json { }

                    if (!(arrayOf("W", "N", "H", "P", "QNR", "O").contains(typeLog))) {
                        var key = "generic"
                        var combos: List<ComboLte>? = null

                        if (!comboList.lteCombos.isNullOrEmpty()) {
                            key = "lte"
                            combos = comboList.lteCombos
                        } else if (!comboList.nrCombos.isNullOrEmpty()) {
                            key = "nr"
                            combos = comboList.lteCombos
                        } else if (!comboList.enDcCombos.isNullOrEmpty()) {
                            key = "endc"
                            combos = comboList.lteCombos
                        }

                        if (combos != null) {
                            outputJson = outputJson.append(
                                key,
                                Utility.toCsv(combos),
                            )
                        }
                    } else {
                        val lteCombos = comboList.lteCombos
                        if (!lteCombos.isNullOrEmpty()) {
                            outputJson = outputJson.append(
                                "lte", Utility.toCsv(lteCombos)
                            )
                        }

                        val nrCombos = comboList.nrCombos
                        if (!nrCombos.isNullOrEmpty()) {
                            outputJson = outputJson.append(
                                "nr", Utility.toCsv(nrCombos)
                            )
                        }

                        val enDcCombos = comboList.enDcCombos
                        if (!enDcCombos.isNullOrEmpty()) {
                            outputJson = outputJson.append(
                                "endc", Utility.toCsv(enDcCombos)
                            )
                        }
                    }


                    val fileName = if (baseOutputFileName == null) null else "$baseOutputFileName.json"

                    Utility.outputFile(outputJson.toString(), fileName)
                }

                "json-compact" -> {
                    var list = comboList.lteCombos
                    if (list != null) {
                        list =
                            list.sortedWith(Comparator.comparing({ obj: ComboLte -> obj.masterComponents }) { s1: Array<IComponent>, s2: Array<IComponent> ->
                                var i = 0
                                while (i < s1.size && i < s2.size) {
                                    val result = s1[i].compareTo(s2[i])
                                    if (result != 0) return@comparing result
                                    i++
                                }
                                s1.size.compareTo(s2.size)
                            })
                    }

                    val fileName = if (baseOutputFileName == null) null else "$baseOutputFileName.json"
                    Utility.outputFile(Utility.compact(Capabilities(list)).combosToString(), fileName)
                }

                else -> {
                    throw ParseException("Invalid output format provided: $outputFormat")
                }
            }
        } catch (e: ParseException) {
            if (!args.contains("-h") && !args.contains("--help")) {
                System.err.println(e.localizedMessage)
            }
            formatter.printHelp("ueCapabilityParser", options)
            exitProcess(1)
        }
    }

    private fun oldEngine(
        cmd: CommandLine, typeLog: String
    ): Capabilities {
        try {
            var input: String
            var inputNR = ""
            var inputENDC = ""
            if (typeLog == "E" || typeLog == "M") {
                input = cmd.getOptionValue("input")
            } else {
                input = Utility.readFile(cmd.getOptionValue("input"), StandardCharsets.UTF_8)

                if (typeLog == "H" || typeLog == "P" || typeLog == "N" || typeLog == "W") {
                    if (cmd.hasOption("inputNR")) {
                        inputNR = Utility.readFile(cmd.getOptionValue("inputNR"), StandardCharsets.UTF_8)
                    }
                    if (cmd.hasOption("inputENDC")) {
                        inputENDC = Utility.readFile(
                            cmd.getOptionValue("inputENDC"), StandardCharsets.UTF_8
                        )
                    }

                    if (typeLog != "H") {
                        input += inputENDC + inputNR
                    }
                }
            }
            val imports: ImportCapabilities?
            when (typeLog) {
                "W" -> imports = ImportWireshark()
                "N" -> imports = ImportNsg()
                "H" -> {
                    val tshark = Tshark()
                    val rat = if (cmd.hasOption("defaultNR")) Rat.nr else Rat.eutra
                    input = tshark.startDecoder(input, "lte-rrc.ul.dcch", rat)
                    if (inputENDC.isNotBlank()) {
                        input += tshark.startDecoder(inputENDC, "lte-rrc.ul.dcch", Rat.eutra_nr)
                    }
                    if (inputNR.isNotBlank()) {
                        input += tshark.startDecoder(inputNR, "lte-rrc.ul.dcch", Rat.nr)
                    }
                    imports = ImportWireshark()
                }

                "P" -> imports = ImportCellularPro()
                "E" -> imports = ImportNvItem()
                "C" -> imports = ImportCarrierPolicy()
                "CNR" -> imports = ImportCapPrune()
                "Q" -> imports = Import0xB0CD()
                "M" -> imports = ImportMTKLte()
                "QNR" -> imports = Import0xB826()
                "O" -> {
                    System.err.println(
                        "Type O (Osix) is supported only by the new experimental engine.\n" + "You can enable it with --newEngine."
                    )
                    exitProcess(1)
                }

                else -> {
                    System.err.println(
                        "Only type W (wireshark), N (NSG), H (Hex Dump), P (CellularPro), " + "C (Carrier policy), CNR (NR Cap Prune), E (28874 nvitem), " + "Q (0xB0CD text), M (CA_COMB_INFO), QNR (0xB826 hexdump) are supported!"
                    )
                    exitProcess(1)
                }
            }

            if (cmd.hasOption("uelog")) {
                val outputFile = cmd.getOptionValue("uelog")
                outputFile(input, outputFile)
            }

            return if (typeLog == "QNR") {
                multipleParser(input, cmd.hasOption("multi"), imports)
            } else {
                imports.parse(input)
            }
        } catch (e: Exception) {
            System.err.println("Error")
            e.printStackTrace()
            exitProcess(1)
        }
    }

    private fun newEngine(
        cmd: CommandLine, typeLog: String
    ): Capabilities {
        try {
            var input: String
            var inputNR = ""
            var inputENDC = ""
            input = Utility.readFile(cmd.getOptionValue("input"), StandardCharsets.UTF_8)
            if (typeLog == "H" || typeLog == "N" || typeLog == "W") {
                if (cmd.hasOption("inputNR")) {
                    inputNR = Utility.readFile(cmd.getOptionValue("inputNR"), StandardCharsets.UTF_8)
                }
                if (cmd.hasOption("inputENDC")) {
                    inputENDC = Utility.readFile(
                        cmd.getOptionValue("inputENDC"), StandardCharsets.UTF_8
                    )
                }
                if (typeLog == "H") {
                    input = preformatHexData(input)
                    inputNR = preformatHexData(inputNR)
                    inputENDC = preformatHexData(inputENDC)
                } else {
                    input += inputENDC + inputNR
                }
            }
            val imports = ImportCapabilityInformationJson()
            val formatWriter = KotlinJsonFormatWriter()
            val ratContainerMap = mutableMapOf<String, JsonElement>()
            lateinit var eutraIdentifier: Regex
            lateinit var nrIdentifier: Regex
            lateinit var mrdcIdentifier: Regex

            val converter = when (typeLog) {
                "W" -> {
                    eutraIdentifier = "${Rat.eutra.ratCapabilityIdentifier}\\s".toRegex()
                    nrIdentifier = "${Rat.nr.ratCapabilityIdentifier}\\s".toRegex()
                    mrdcIdentifier = "${Rat.eutra_nr.ratCapabilityIdentifier}\\s".toRegex()
                    ConverterWireshark()
                }

                "N" -> {
                    eutraIdentifier = "rat-Type : ${Rat.eutra}\\s".toRegex()
                    nrIdentifier = "rat-Type : ${Rat.nr}\\s".toRegex()
                    mrdcIdentifier = "rat-Type : ${Rat.eutra_nr}\\s".toRegex()
                    ConverterNSG()
                }

                "O" -> {
                    eutraIdentifier = "${Rat.eutra.ratCapabilityIdentifier}\\s".toRegex()
                    nrIdentifier = "${Rat.nr.ratCapabilityIdentifier}\\s".toRegex()
                    mrdcIdentifier = "${Rat.eutra_nr.ratCapabilityIdentifier}\\s".toRegex()
                    ConverterOsix()
                }

                "H" -> null
                else -> {
                    System.err.println(
                        "Only type W (wireshark), N (NSG), H (Hex Dump), O (Osix) are supported " + "by the new Engine!"
                    )
                    exitProcess(1)
                }
            }

            if (typeLog == "H") {
                val defaultRat = if (cmd.hasOption("defaultNR")) Rat.nr else Rat.eutra
                ratContainerMap += Utility.getUeCapabilityJsonFromHex(defaultRat, input)
                if (inputNR.isNotBlank()) {
                    ratContainerMap += Utility.getUeCapabilityJsonFromHex(Rat.nr, input)
                }
                if (inputENDC.isNotBlank()) {
                    ratContainerMap += Utility.getUeCapabilityJsonFromHex(Rat.eutra_nr, input)
                }
            } else {
                val list = listOf(
                    Rat.eutra to input.indexOf(eutraIdentifier),
                    Rat.eutra_nr to input.indexOf(mrdcIdentifier),
                    Rat.nr to input.indexOf(nrIdentifier)
                ).filter { it.second != -1 }.sortedBy { it.second }
                var eutra = ""
                var eutraNr = ""
                var nr = ""
                for (i in list.indices) {
                    val (rat, start) = list[i]
                    val end = list.getOrNull(i + 1)?.second ?: input.length
                    when (rat) {
                        Rat.eutra -> eutra = input.substring(start + eutraIdentifier.toString().length - 1, end)
                        Rat.eutra_nr -> eutraNr = input.substring(start + mrdcIdentifier.toString().length - 1, end)
                        Rat.nr -> nr = input.substring(start + nrIdentifier.toString().length - 1, end)
                        else -> {}
                    }
                }
                if (eutra.isNotBlank()) {
                    getAsn1Converter(Rat.eutra, converter!!).convert(
                        Rat.eutra.ratCapabilityIdentifier, eutra.byteInputStream(), formatWriter
                    )
                    formatWriter.jsonNode?.let { ratContainerMap.put(Rat.eutra.toString(), it) }
                }
                if (eutraNr.isNotBlank() || nr.isNotBlank()) {
                    val nrConverter = getAsn1Converter(Rat.nr, converter!!)
                    if (eutraNr.isNotBlank()) {
                        nrConverter.convert(
                            Rat.eutra_nr.ratCapabilityIdentifier, eutraNr.byteInputStream(), formatWriter
                        )
                        formatWriter.jsonNode?.let { ratContainerMap.put(Rat.eutra_nr.toString(), it) }
                    }
                    if (nr.isNotBlank()) {
                        nrConverter.convert(Rat.nr.ratCapabilityIdentifier, nr.byteInputStream(), formatWriter)
                        formatWriter.jsonNode?.let { ratContainerMap.put(Rat.nr.toString(), it) }
                    }
                }
            }

            val jsonOutput = JsonObject(ratContainerMap)

            if (cmd.hasOption("uelog")) {
                val outputFile = cmd.getOptionValue("uelog")
                outputFile(jsonOutput.toString(), outputFile)
            }

            val jsonEutra = jsonOutput.getOrDefault(Rat.eutra.toString(), null) as? JsonObject
            val jsonEutraNr = jsonOutput.getOrDefault(Rat.eutra_nr.toString(), null) as? JsonObject
            val jsonNr = jsonOutput.getOrDefault(Rat.nr.toString(), null) as? JsonObject

            return imports.parse(jsonEutra, jsonEutraNr, jsonNr)
        } catch (e: Exception) {
            System.err.println("Error")
            e.printStackTrace()
            exitProcess(1)
        }
    }
}
