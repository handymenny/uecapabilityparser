package it.smartphonecombo.uecapabilityparser

import it.smartphonecombo.uecapabilityparser.Utility.multipleParser
import it.smartphonecombo.uecapabilityparser.Utility.outputFile
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
import org.apache.commons.cli.*
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
        val help = Option("h", "help", false, "Print this help message.")
        options.addOption(help)
        val inputFile = Option("i", "input", true, "Main capability file.")
        inputFile.isRequired = true
        options.addOption(inputFile)
        val inputFileNR = Option("inputNR", true, "NR UE Capability file.")
        options.addOption(inputFileNR)
        val inputFileENDC = Option("inputENDC", true, "ENDC UE Capability file.")
        options.addOption(inputFileENDC)
        val defaultInputIsNR = Option("nr", "defaultNR", false, "Main capability input is NR (otherwise LTE).")
        options.addOption(defaultInputIsNR)
        val multiple0xB826 = Option(
            "multi",
            "multiple0xB826",
            false,
            "Use this option if input contains several 0xB826 hexdumps separated by blank lines and optionally prefixed with \"Payload :\"."
        )
        options.addOption(multiple0xB826)
        val type = Option(
            "t",
            "type",
            true,
            "Type of capability.\nValid values are:\nH (UE Capability Hex Dump)\nW (Wireshark UE Capability Information)\nN (NSG UE Capability Information)\nP (CellularPro UE Capability Information)\nC (Carrier policy)\nCNR (NR Cap Prune)\nE (28874 nvitem binary, decompressed)\nQ (QCAT 0xB0CD)\nQNR (0xB826 hexdump)\nM (MEDIATEK CA_COMB_INFO)."
        )
        type.isRequired = true
        options.addOption(type)
        val json = Option(
            "j", "compactJson", true,
            "Output a Compact Json (used by smartphonecombo.it), if no file specified the json will be output to standard output."
        )
        json.setOptionalArg(true)
        options.addOption(json)
        val csv = Option(
            "c", "csv", true,
            "Output a csv, if no file specified the csv will be output to standard output.\nSome parsers output multiple CSVs, in these cases \"-LTE\", \"-NR\", \"-EN-DC\", \"-generic\" will be added before the extension."
        )
        csv.setOptionalArg(true)
        options.addOption(csv)
        val uelog = Option(
            "l", "uelog", true,
            "Output the uelog, if no file specified the uelog will be output to standard output."
        )
        uelog.setOptionalArg(true)
        options.addOption(uelog)
        val debug = Option("d", "debug", false, "Print debug info.")
        options.addOption(debug)
        val tsharkPath = Option(
            "T", "TsharkPath", true,
            "Set tshark path. (Tshark is used for H type)"
        )
        options.addOption(tsharkPath)
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
                            inputENDC = Utility.readFile(cmd.getOptionValue("inputENDC"), StandardCharsets.UTF_8)
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
                    else -> {
                        System.err.println(
                            "Only type W (wireshark), N (NSG), H (Hex Dump), P (CellularPro), C (Carrier policy), CNR (NR Cap Prune), E (28874 nvitem), Q (0xB0CD text), M (CA_COMB_INFO), QNR (0xB826 hexdump) are supported!"
                        )
                        exitProcess(1)
                    }
                }
                val comboList = if (typeLog == "QNR") {
                    multipleParser(input, cmd.hasOption("multi"), imports)
                } else {
                    imports.parse(input)
                }
                if (cmd.hasOption("uelog")) {
                    val outputFile = cmd.getOptionValue("uelog")
                    outputFile(input, outputFile)
                }
                if (cmd.hasOption("csv")) {
                    val fileName: String? = cmd.getOptionValue("csv")
                    if (typeLog == "W" || typeLog == "N" || typeLog == "H" || typeLog == "P" || typeLog == "QNR") {
                        val lteCombos = comboList.lteCombos
                        if (!lteCombos.isNullOrEmpty()) {
                            outputFile(
                                Utility.toCsv(lteCombos),
                                fileName?.let {
                                    Utility.appendBeforeExtension(it, "-LTE")
                                }
                            )
                        }
                        val nrCombos = comboList.nrCombos
                        if (!nrCombos.isNullOrEmpty()) {
                            outputFile(
                                Utility.toCsv(nrCombos),
                                fileName?.let {
                                    Utility.appendBeforeExtension(it, "-NR")
                                }
                            )
                        }
                        val enDcCombos = comboList.enDcCombos
                        if (!enDcCombos.isNullOrEmpty()) {
                            outputFile(
                                Utility.toCsv(enDcCombos),
                                fileName?.let {
                                    Utility.appendBeforeExtension(it, "-EN-DC")
                                }
                            )
                        }
                    } else outputFile(
                        Utility.toCsv(comboList), fileName
                    )
                }
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
                if (cmd.hasOption("compactJson")) {
                    val outputFile = cmd.getOptionValue("compactJson")
                    outputFile(Utility.compact(Capabilities(list)).combosToString(), outputFile)
                }
            } catch (e: Exception) {
                System.err.println("Error")
                e.printStackTrace()
            }
        } catch (e: ParseException) {
            if (!args.contains("-h") && !args.contains("--help")) {
                System.err.println(e.localizedMessage)
            }
            formatter.printHelp("ueCapabilityParser", options)
            exitProcess(1)
        }
    }
}