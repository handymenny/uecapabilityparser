package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
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
import com.github.ajalt.clikt.parameters.types.boolean
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import it.smartphonecombo.uecapabilityparser.extension.appendBeforeExtension
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.server.ServerMode
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import it.smartphonecombo.uecapabilityparser.util.Parsing
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Clikt : CliktCommand(name = "uecapabilityparser") {

    override val printHelpOnEmptyArgs = true

    init {
        versionOption(version = Config.getOrDefault("project.version", ""))

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

object Cli : CliktCommand(name = "cli") {
    override val printHelpOnEmptyArgs = true

    private val inputsList by
        option("-i", "--input", help = HelpMessage.INPUT)
            .file(mustExist = true, canBeDir = false, mustBeReadable = true)
            .split("""\s*,\s*""".toRegex())
            .multiple(required = true)

    private val typeList by
        option("-t", "--type", help = HelpMessage.TYPE)
            .choice(*LogType.names, ignoreCase = true)
            .multiple(required = true)
            .validate {
                require(it.size == inputsList.size) { HelpMessage.ERROR_TYPE_INPUT_MISMATCH }
                val singleType = LogType.singleInput
                val singleInputs =
                    inputsList.filterIndexed { index, _ -> LogType.of(it[index]) in singleType }
                require(singleInputs.all { inputs -> inputs.size == 1 }) {
                    HelpMessage.ERROR_MULTIPLE_INPUTS_UNSUPPORTED
                }
            }

    private val subTypesList by
        option("--sub-types", help = HelpMessage.SUBTYPES)
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

    private val newCsvFormat by
        option("--new-csv-format", help = HelpMessage.NEW_CSV_FORMAT).boolean().default(false)

    private val json by option("-j", "--json", help = HelpMessage.JSON, metavar = "FILE")

    private val jsonPrettyPrint by
        option("--json-pretty-print", help = HelpMessage.JSON_PRETTY_PRINT).flag()

    private val ueLog by option("-l", "--uelog", help = HelpMessage.UE_LOG, metavar = "FILE")

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    private lateinit var jsonFormat: Json

    override fun help(context: Context) = "Starts ue capability parser in cli mode"

    override fun run() {
        // Set debug
        if (debug) Config["debug"] = debug.toString()

        jsonFormat = if (jsonPrettyPrint) Json { prettyPrint = true } else Json

        val inputsSource = inputsList.map { inputs -> inputs.map { it.toInputSource() } }

        val multiParsing =
            MultiParsing(
                inputsSource,
                typeList.map(LogType::of),
                subTypesList,
                jsonFormat = jsonFormat
            )

        val parsingList = multiParsing.parsingList

        for (i in parsingList.indices) {
            val parsing = parsingList[i]
            ueLog?.let {
                val ueLogOutput =
                    when {
                        it == "-" -> null
                        i == 0 -> it
                        else -> it.appendBeforeExtension("-${i+1}-")
                    }
                IOUtils.outputFileOrStdout(parsing.ueLog, ueLogOutput)
            }
            csv?.let {
                val csvOutput =
                    when {
                        it == "-" -> null
                        i == 0 -> it
                        else -> it.appendBeforeExtension("-${i+1}-")
                    }
                csvOutput(
                    parsing.capabilities,
                    csvOutput,
                    parsing.capabilities.logType,
                    newCsvFormat
                )
            }
        }

        // One json output
        json?.let {
            val jsonOutput = if (it == "-") null else it
            if (parsingList.size > 1) {
                val parsedCapabilities = parsingList.map(Parsing::capabilities)
                IOUtils.outputFileOrStdout(
                    jsonFormat.encodeToString(parsedCapabilities),
                    jsonOutput
                )
            } else {
                IOUtils.outputFileOrStdout(
                    jsonFormat.encodeToString(parsingList.first().capabilities),
                    jsonOutput
                )
            }
        }
    }

    private fun csvOutput(cap: Capabilities, csvPath: String?, type: LogType, newFmt: Boolean) {
        if (type in LogType.lteOnlyTypes) {
            return IOUtils.outputFileOrStdout(IOUtils.toCsv(cap.lteCombos, newFmt), csvPath)
        }

        val lteCombos = cap.lteCombos
        if (lteCombos.isNotEmpty()) {
            IOUtils.outputFileOrStdout(
                IOUtils.toCsv(lteCombos, newFmt),
                csvPath?.appendBeforeExtension("-LTECA")
            )
        }
        val nrCombos = cap.nrCombos
        if (nrCombos.isNotEmpty()) {
            IOUtils.outputFileOrStdout(
                IOUtils.toCsv(nrCombos),
                csvPath?.appendBeforeExtension("-NRCA")
            )
        }
        val enDcCombos = cap.enDcCombos
        if (enDcCombos.isNotEmpty()) {
            IOUtils.outputFileOrStdout(
                IOUtils.toCsv(enDcCombos),
                csvPath?.appendBeforeExtension("-ENDC")
            )
        }
        val nrDcCombos = cap.nrDcCombos
        if (nrDcCombos.isNotEmpty()) {
            IOUtils.outputFileOrStdout(
                IOUtils.toCsv(nrDcCombos),
                csvPath?.appendBeforeExtension("-NRDC")
            )
        }
    }
}

object Server : CliktCommand(name = "server") {
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
        val cache by
            option("--cache", metavar = "Items", help = HelpMessage.LIBRARY_CACHE)
                .int()
                .default(1000)
    }

    private const val DEFAULT_PORT = 0

    private val port by
        option("-p", "--port", help = HelpMessage.PORT, metavar = "PORT")
            .int()
            .default(DEFAULT_PORT)

    private val store by StoreOptions().cooccurring()

    private val maxRequestSize by
        option("-m", "--max-request-size", metavar = "Bytes", help = HelpMessage.MAX_REQUEST_SIZE)
            .long()
            .default(256 * 1000 * 1000)

    private val customCss by
        option("--custom-css", metavar = "FILE", help = HelpMessage.CUSTOM_CSS)
            .file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val customJs by
        option("--custom-js", metavar = "FILE", help = HelpMessage.CUSTOM_JS)
            .file(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val debug by option("-d", "--debug", help = HelpMessage.DEBUG).flag()

    override fun help(context: Context) = "Starts ue capability parser in server mode"

    override fun run() {
        // Set debug
        if (debug) Config["debug"] = debug.toString()
        Config["maxRequestSize"] = maxRequestSize.toString()
        Config["customCss"] = customCss?.path ?: ""
        Config["customJs"] = customJs?.path ?: ""

        // Process store
        store?.let {
            Config["store"] = it.path
            Config["compression"] = it.compression.toString()
            Config["reparse"] = it.reparse
            Config["cache"] = it.cache.toString()
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
        features += "max request size ${maxRequestSize/(1000.0 * 1000.0)}MB"
        if (Config["debug"].toBoolean()) features += "debug"
        if (store != null) features += "store"
        if (Config["compression"].toBoolean()) features += "compression"
        if (Config.getOrDefault("reparse", "off") != "off")
            features += "reparse ${Config["reparse"]}"

        features +=
            when (val items = Config["cache"]?.toIntOrNull()?.takeIf { it >= 0 }) {
                0 -> "cache disabled"
                null -> "cache unlimited"
                else -> "cache $items items"
            }

        val featuresString =
            if (features.isNotEmpty()) {
                features.joinToString(" + ", " with ")
            } else ""

        return "Server started at port $serverPort$featuresString\n$webUiMessage"
    }
}
