package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.int
import it.smartphonecombo.uecapabilityparser.extension.appendBeforeExtension
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.server.ServerMode
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.IO
import it.smartphonecombo.uecapabilityparser.util.Parsing
import it.smartphonecombo.uecapabilityparser.util.Property
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Clikt : CliktCommand(name = "uecapabilityparser", printHelpOnEmptyArgs = true) {

    init {
        versionOption(version = Property.getProperty("project.version") ?: "")

        val subcommands = arrayOf(Cli, Server)
        // Set subcommands
        subcommands(*subcommands)

        // Customize help formatter
        context {
            helpFormatter = {
                MordantHelpFormatter(it, showDefaultValues = true, requiredOptionMarker = "*")
            }
        }
    }

    override fun run() {
        // We don't need to do nothing here
    }
}

object Cli :
    CliktCommand(
        name = "cli",
        help = "Starts ue capability parser in cli mode",
        printHelpOnEmptyArgs = true
    ) {

    private val inputsList by
        option("-i", "--input", help = HelpMessage.INPUT)
            .inputStream()
            .split("""\s*,\s*""".toRegex())
            .multiple(required = true)

    private val typeList by
        option("-t", "--type", help = HelpMessage.TYPE)
            .choice(
                "H",
                "W",
                "N",
                "C",
                "CNR",
                "E",
                "Q",
                "QLTE",
                "QNR",
                "M",
                "O",
                "QC",
                "RF",
                ignoreCase = true
            )
            .multiple(required = true)
            .validate {
                require(it.size == inputsList.size) { HelpMessage.ERROR_TYPE_INPUT_MISMATCH }

                val nvItemInputs = inputsList.filterIndexed { index, _ -> it[index] == "E" }
                require(nvItemInputs.all { inputs -> inputs.size == 1 }) {
                    HelpMessage.ERROR_MULTIPLE_INPUTS_UNSUPPORTED
                }
            }

    private val subTypesList by
        option("--subTypes", help = HelpMessage.SUBTYPES)
            .choice("LTE", "ENDC", "NR", ignoreCase = true)
            .split("""\s*,\s*""".toRegex())
            .multiple()
            .validate {
                val hexInputs = inputsList.filterIndexed { index, _ -> typeList[index] == "H" }

                require(it.size == hexInputs.size) { HelpMessage.ERROR_SUBTYPES_TYPE_MISMATCH }

                require(it.zip(hexInputs).all { (a, b) -> a.size == b.size }) {
                    HelpMessage.ERROR_SUBTYPES_INPUT_MISMATCH
                }

                require(it.all { subTypes -> subTypes.size == subTypes.distinct().size }) {
                    HelpMessage.ERROR_SUBTYPES_DUPLICATE
                }
            }

    private val csv by option("-c", "--csv", help = HelpMessage.CSV, metavar = "FILE")

    private val json by option("-j", "--json", help = HelpMessage.JSON, metavar = "FILE")

    private val jsonPrettyPrint by
        option("--json-pretty-print", help = HelpMessage.JSON_PRETTY_PRINT).flag()

    private val ueLog by option("-l", "--uelog", help = HelpMessage.UE_LOG, metavar = "FILE")

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    private lateinit var jsonFormat: Json

    private lateinit var parsing: Parsing

    override fun run() {
        // Set debug
        if (debug) Config["debug"] = debug.toString()

        jsonFormat = if (jsonPrettyPrint) Json { prettyPrint = true } else Json
        val subTypeIterator = subTypesList.iterator()

        for (i in inputsList.indices) {
            val inputs = inputsList[i]
            val type = typeList[i]
            var inputArray = ByteArray(0)
            var inputENDCArray: ByteArray? = null
            var inputNRArray: ByteArray? = null
            var defaultNr = false

            if (type != "H") {
                inputArray = inputs.fold(inputArray) { acc, it -> acc + it.readBytes() }
            } else {
                val subTypes = subTypeIterator.next()
                for (j in inputs.indices) {
                    val subType = subTypes[j]
                    val input = inputs[j]
                    when (subType) {
                        "LTE" -> inputArray = input.readBytes()
                        "ENDC" -> inputENDCArray = input.readBytes()
                        "NR" -> inputNRArray = input.readBytes()
                    }
                }

                if (inputNRArray?.isNotEmpty() == true && inputArray.isEmpty()) {
                    inputArray = inputNRArray
                    defaultNr = true
                }
            }

            parsing = Parsing(inputArray, inputENDCArray, inputNRArray, defaultNr, type, jsonFormat)

            val capabilities = parsing.capabilities

            ueLog?.let {
                val ueLogOutput =
                    when {
                        it == "-" -> null
                        i == 0 -> it
                        else -> it.appendBeforeExtension("-${i+1}-")
                    }
                IO.outputFileOrStdout(parsing.ueLog, ueLogOutput)
            }
            csv?.let {
                val csvOutput =
                    when {
                        it == "-" -> null
                        i == 0 -> it
                        else -> it.appendBeforeExtension("-${i+1}-")
                    }
                csvOutput(capabilities, csvOutput, type)
            }
            json?.let {
                val jsvOutput =
                    when {
                        it == "-" -> null
                        i == 0 -> it
                        else -> it.appendBeforeExtension("-${i+1}-")
                    }
                IO.outputFileOrStdout(jsonFormat.encodeToString(capabilities), jsvOutput)
            }
        }
    }

    private fun csvOutput(comboList: Capabilities, csvPath: String?, type: String) {
        val lteOnlyTypes = arrayOf("C", "E", "Q", "QLTE", "M", "RF")
        if (type in lteOnlyTypes) {
            return IO.outputFileOrStdout(IO.toCsv(comboList.lteCombos), csvPath)
        }

        val lteCombos = comboList.lteCombos
        if (lteCombos.isNotEmpty()) {
            IO.outputFileOrStdout(IO.toCsv(lteCombos), csvPath?.appendBeforeExtension("-LTECA"))
        }
        val nrCombos = comboList.nrCombos
        if (nrCombos.isNotEmpty()) {
            IO.outputFileOrStdout(IO.toCsv(nrCombos), csvPath?.appendBeforeExtension("-NRCA"))
        }
        val enDcCombos = comboList.enDcCombos
        if (enDcCombos.isNotEmpty()) {
            IO.outputFileOrStdout(IO.toCsv(enDcCombos), csvPath?.appendBeforeExtension("-ENDC"))
        }
        val nrDcCombos = comboList.nrDcCombos
        if (nrDcCombos.isNotEmpty()) {
            IO.outputFileOrStdout(IO.toCsv(nrDcCombos), csvPath?.appendBeforeExtension("-NRDC"))
        }
    }
}

object Server : CliktCommand(name = "server", help = "Starts ue capability parser in server mode") {

    init {
        // Disable required mark
        context { helpFormatter = { MordantHelpFormatter(it, showDefaultValues = true) } }
    }

    // Subclass StoreOptions
    private class StoreOptions : OptionGroup() {
        val path by option("--store", help = HelpMessage.STORE, metavar = "DIR").required()
        val compression by option("--compression", help = HelpMessage.COMPRESSION).flag()
        val reparse by
            option("--reparse", help = HelpMessage.REPARSE, metavar = "Strategy")
                .choice("off", "auto", "force")
                .default("off")
    }

    private const val DEFAULT_PORT = 0

    private val port by
        option("-p", "--port", help = HelpMessage.PORT, metavar = "PORT")
            .int()
            .default(DEFAULT_PORT)

    private val store by StoreOptions().cooccurring()

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    override fun run() {
        // Set debug
        if (debug) Config["debug"] = debug.toString()

        // Process store
        store?.let {
            Config["store"] = it.path
            Config["compression"] = it.compression.toString()
            Config["reparse"] = it.reparse
        }

        // Start server
        // if inputPort is 0, serverPort is random
        val serverPort = ServerMode.run(port)

        echo(buildServerStartMessage(serverPort))
    }

    private fun buildServerStartMessage(serverPort: Int): String {
        val webUiMessage =
            """
            |Web UI (demo) available at http://localhost:$serverPort/
            |OpenAPI Spec available at http://localhost:$serverPort/openapi
            |Swagger UI available at http://localhost:$serverPort/swagger
            """
                .trimMargin()

        val features = mutableListOf<String>()
        if (Config["debug"].toBoolean()) features += "debug"
        if (store != null) features += "store"
        if (Config["compression"].toBoolean()) features += "compression"
        if (Config["reparse"] != "off") features += "reparse=${Config["reparse"]}"

        val featuresString =
            if (features.isNotEmpty()) {
                features.joinToString(" + ", " with ")
            } else ""

        return "Server started at port $serverPort$featuresString\n$webUiMessage"
    }
}
