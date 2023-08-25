package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.optionalValue
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.int
import it.smartphonecombo.uecapabilityparser.extension.appendBeforeExtension
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.server.ServerMode
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.Output
import it.smartphonecombo.uecapabilityparser.util.Parsing
import it.smartphonecombo.uecapabilityparser.util.Property
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Clikt : CliktCommand(name = "UE Capability Parser", printHelpOnEmptyArgs = true) {
    init {
        versionOption(version = Property.getProperty("project.version") ?: "")
    }

    private val server by
        option("-s", "--server", help = HelpMessage.SERVER, metavar = "PORT", eager = true)
            .int()
            .optionalValue(8080)
            .validate { port ->
                // Process debug
                val isDebug = context.originalArgv.any { arg -> arg == "--debug" || arg == "-d" }
                Config["debug"] = isDebug.toString()
                val debugMessage = if (isDebug) " with debug enabled" else ""
                // Process store
                var storeMessage = ""
                val storeIndex = context.originalArgv.indexOfFirst { arg -> arg == "--store" }
                if (storeIndex != -1) {
                    Config["store"] = context.originalArgv[storeIndex + 1]
                    storeMessage = if (isDebug) " and with store enabled" else " with store enabled"
                }
                ServerMode.run(port)
                val serverStartMessage = "Server started at port $port$debugMessage$storeMessage"
                val webUiMessage =
                    """
                    |Web UI (demo) available at http://localhost:$port/
                    |OpenAPI Spec available at http://localhost:$port/openapi
                    |Swagger UI available at http://localhost:$port/swagger
                    |"""
                        .trimMargin()
                // stop processing other options
                throw PrintMessage(
                    "$serverStartMessage\n$webUiMessage",
                    statusCode = 0,
                    printError = false
                )
            }

    private val store by option("--store", help = HelpMessage.STORE, metavar = "DIR")

    private val input by option("-i", "--input", help = HelpMessage.INPUT).inputStream().required()

    private val inputNR by option("--inputNR", help = HelpMessage.INPUT_NR).inputStream()

    private val inputENDC by option("--inputENDC", help = HelpMessage.INPUT_ENDC).inputStream()

    private val defaultNR by option("--nr", "--defaultNR", help = HelpMessage.DEFAULT_NR).flag()

    private val multiple0xB826 by
        option("--multi", "--multiple0xB826", help = HelpMessage.MULTIPLE_0XB826).flag()

    private val type by
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
                ignoreCase = true
            )
            .required()

    private val csv by option("-c", "--csv", help = HelpMessage.CSV, metavar = "FILE")

    private val json by option("-j", "--json", help = HelpMessage.JSON, metavar = "FILE")

    private val jsonPrettyPrint by
        option("--json-pretty-print", help = HelpMessage.JSON_PRETTY_PRINT).flag()

    private val ueLog by option("-l", "--uelog", help = HelpMessage.UE_LOG, metavar = "FILE")

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    private lateinit var jsonFormat: Json

    private lateinit var parsing: Parsing

    override fun run() {
        Config["debug"] = debug.toString()

        jsonFormat = if (jsonPrettyPrint) Json { prettyPrint = true } else Json
        parsing =
            Parsing(
                input.readBytes(),
                inputNR?.readBytes(),
                inputENDC?.readBytes(),
                defaultNR,
                multiple0xB826,
                type,
                jsonFormat
            )

        val capabilities = parsing.capabilities

        ueLog?.let {
            val ueLogOutput = if (ueLog == "-") null else ueLog
            Output.outputFileOrStdout(parsing.ueLog, ueLogOutput)
        }
        csv?.let {
            val csvOutput = if (it == "-") null else it
            csvOutput(capabilities, csvOutput)
        }
        json?.let {
            val jsvOutput = if (it == "-") null else it
            Output.outputFileOrStdout(jsonFormat.encodeToString(capabilities), jsvOutput)
        }
    }

    private fun csvOutput(comboList: Capabilities, csvPath: String?) {
        if (type !in arrayOf("W", "N", "H", "QNR", "CNR", "QC", "O")) {
            return Output.outputFileOrStdout(Output.toCsv(comboList), csvPath)
        }

        val lteCombos = comboList.lteCombos
        if (lteCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(lteCombos),
                csvPath?.appendBeforeExtension("-LTECA")
            )
        }
        val nrCombos = comboList.nrCombos
        if (nrCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(nrCombos),
                csvPath?.appendBeforeExtension("-NRCA")
            )
        }
        val enDcCombos = comboList.enDcCombos
        if (enDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(enDcCombos),
                csvPath?.appendBeforeExtension("-ENDC")
            )
        }
        val nrDcCombos = comboList.nrDcCombos
        if (nrDcCombos.isNotEmpty()) {
            Output.outputFileOrStdout(
                Output.toCsv(nrDcCombos),
                csvPath?.appendBeforeExtension("-NRDC")
            )
        }
    }
}
