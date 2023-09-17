package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.Option
import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.deprecated
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.optionalValue
import com.github.ajalt.clikt.parameters.options.required
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

object Clikt :
    CliktCommand(
        name = "uecapabilityparser",
        printHelpOnEmptyArgs = true,
        invokeWithoutSubcommand = true
    ) {

    init {
        versionOption(version = Property.getProperty("project.version") ?: "")

        val subcommands = arrayOf(Cli, Server)
        // Set subcommands
        subcommands(*subcommands)

        // Register subcommands options, so cli doesn't fail if it's invoked without a subcommand
        subcommands.forEach { command ->
            val cmdName = command.commandName
            val unfilteredNames = command.registeredOptions().flatMap(Option::names)
            val names = unfilteredNames.filter { it != "-d" && it != "--debug" }.toTypedArray()

            val deprecatedMessage =
                "WARNING: running without a command is deprecated, add \"$cmdName\" before options"
            val opt = option(*names, hidden = true).optionalValue("").deprecated(deprecatedMessage)

            if (cmdName == "cli") oldCliOptions = opt else oldServerOptions = opt

            registerOption(opt)
        }

        // Customize help formatter
        context {
            helpFormatter = {
                MordantHelpFormatter(it, showDefaultValues = true, requiredOptionMarker = "*")
            }
        }
    }

    // this is common to both oldCli and oldServer
    private val debug by option("-d", "--debug", hidden = true).flag()
    private lateinit var oldCliOptions: OptionDelegate<String?>
    private lateinit var oldServerOptions: OptionDelegate<String?>

    override fun run() {
        if (debug) Config["debug"] = "true"

        if (currentContext.invokedSubcommand == null) {
            // No subcommand, redirect to cli or server command
            if (oldServerOptions.value != null) {
                Server.main(currentContext.originalArgv)
            } else if (oldCliOptions.value != null) {
                Cli.main(currentContext.originalArgv)
            } else {
                echoFormattedHelp()
            }
        }
    }
}

object Cli :
    CliktCommand(
        name = "cli",
        help = "Starts ue capability parser in cli mode",
        printHelpOnEmptyArgs = true
    ) {
    private val input by option("-i", "--input", help = HelpMessage.INPUT).inputStream().required()

    private val inputNR by option("--inputNR", help = HelpMessage.INPUT_NR).inputStream()

    private val inputENDC by option("--inputENDC", help = HelpMessage.INPUT_ENDC).inputStream()

    private val defaultNR by option("--nr", "--defaultNR", help = HelpMessage.DEFAULT_NR).flag()

    private val multiple0xB826 by
        option("--multi", "--multiple0xB826", help = HelpMessage.MULTIPLE_0XB826)
            .flag()
            .deprecated(
                "WARNING: --multiple0xB826 is deprecated, it's the default behaviour",
                "option deprecated, default behaviour"
            )

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
                "RF",
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
        // Set debug if it's passed to subcommand
        if (debug) Config["debug"] = debug.toString()

        jsonFormat = if (jsonPrettyPrint) Json { prettyPrint = true } else Json
        parsing =
            Parsing(
                input.readBytes(),
                inputNR?.readBytes(),
                inputENDC?.readBytes(),
                defaultNR,
                type,
                jsonFormat
            )

        val capabilities = parsing.capabilities

        ueLog?.let {
            val ueLogOutput = if (ueLog == "-") null else ueLog
            IO.outputFileOrStdout(parsing.ueLog, ueLogOutput)
        }
        csv?.let {
            val csvOutput = if (it == "-") null else it
            csvOutput(capabilities, csvOutput)
        }
        json?.let {
            val jsvOutput = if (it == "-") null else it
            IO.outputFileOrStdout(jsonFormat.encodeToString(capabilities), jsvOutput)
        }
    }

    private fun csvOutput(comboList: Capabilities, csvPath: String?) {
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

    // -p/--port was -s/--server in 0.1.0
    private val server by
        option("-s", "--server", help = HelpMessage.SERVER, metavar = "PORT")
            .int()
            .optionalValue(8080)
            .deprecated("WARNING: option --server is deprecated, use --port instead", "")

    private val store by StoreOptions().cooccurring()

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    override fun run() {
        // Set debug if it's passed to subcommand
        if (debug) Config["debug"] = debug.toString()

        // Process store
        store?.let {
            Config["store"] = it.path
            Config["compression"] = it.compression.toString()
            Config["reparse"] = it.reparse
        }

        // Start server
        val inputPort = server ?: port
        // if inputPort is 0, serverPort is random
        val serverPort = ServerMode.run(inputPort)

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
