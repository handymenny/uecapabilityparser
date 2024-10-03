package it.smartphonecombo.uecapabilityparser.server

import io.javalin.http.ContentType
import io.javalin.http.Context
import it.smartphonecombo.uecapabilityparser.extension.attachFile
import it.smartphonecombo.uecapabilityparser.extension.bodyAsClassEfficient
import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.notFound
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.query.Query
import it.smartphonecombo.uecapabilityparser.query.SearchableField
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.MultiParsing
import it.smartphonecombo.uecapabilityparser.util.Parsing
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.contracts.ExperimentalContracts
import kotlinx.serialization.json.Json

object Routes {
    private val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    private val idRegex = "[a-f0-9-]{36}(?:-[0-9]+)?".toRegex()

    @OptIn(ExperimentalContracts::class)
    private fun validateId(id: String?) {
        kotlin.contracts.contract { returns() implies (id != null) }
        if (id?.matches(idRegex) != true) throw IllegalArgumentException("Wrong id")
    }

    fun parse(ctx: Context, store: String?, index: LibraryIndex, compression: Boolean) {
        val request = ctx.bodyAsClassEfficient<RequestParse>()
        val parsed = Parsing.fromRequest(request)!!
        ctx.json(parsed.capabilities)
        if (store != null) parsed.store(index, store, compression)
    }

    fun parseMultiPart(ctx: Context, store: String?, index: LibraryIndex, compression: Boolean) {
        val requestsStr = ctx.formParam("requests")!!
        val requestsJson = Json.custom().decodeFromString<List<RequestMultiPart>>(requestsStr)
        val files = ctx.uploadedFiles()
        val parsed = MultiParsing.fromRequest(requestsJson, files)!!
        ctx.json(parsed.getMultiCapabilities())
        if (store != null) parsed.store(index, store, compression)
    }

    fun csv(ctx: Context) {
        val request = ctx.bodyAsClassEfficient<RequestCsv>()
        val comboList = request.input
        val type = request.type
        val date = dataFormatter.format(ZonedDateTime.now(ZoneOffset.UTC))
        val newFmt = (request as? RequestCsv.LteCa)?.newCsvFormat ?: false
        ctx.attachFile(
            IOUtils.toCsv(comboList, newFmt).toInputSource(),
            "${type}-${date}.csv",
            ContentType.TEXT_CSV,
        )
    }

    fun status(ctx: Context, maxRequestSize: Long, endpoints: List<String>) {
        val version = Config.getOrDefault("project.version", "")
        val logTypes = LogType.validEntries
        val status =
            ServerStatus(
                version,
                endpoints,
                logTypes,
                maxRequestSize,
                SearchableField.getAllSearchableFields(),
            )
        ctx.json(status)
    }

    fun storeList(ctx: Context, index: LibraryIndex) {
        ctx.json(index)
    }

    fun storeGetItem(ctx: Context, index: LibraryIndex) {
        val id = ctx.queryParam("id") ?: throw IllegalArgumentException("Wrong id")
        val item = index.find(id) ?: return ctx.notFound()
        ctx.json(item)
    }

    fun storeGetMultiItem(ctx: Context, index: LibraryIndex) {
        val id = ctx.queryParam("id") ?: throw IllegalArgumentException("Wrong id")
        val item = index.findMulti(id) ?: return ctx.notFound()
        ctx.json(item)
    }

    fun storeGetOutput(ctx: Context, index: LibraryIndex, store: String) {
        val id = ctx.queryParam("id")
        validateId(id)

        val capabilities = index.getOutput(id, store) ?: return ctx.notFound()
        ctx.json(capabilities)
    }

    fun storeGetMultiOutput(ctx: Context, index: LibraryIndex, store: String) {
        val id = ctx.queryParam("id")
        validateId(id)

        val multiIndexLine = index.findMulti(id) ?: return ctx.notFound()
        val indexLineIds = multiIndexLine.indexLineIds
        val capabilitiesList = mutableListWithCapacity<Capabilities>(indexLineIds.size)
        for (indexId in indexLineIds) {
            val capabilities = index.getOutput(indexId, store) ?: continue
            capabilitiesList.add(capabilities)
        }

        val multiCapabilities =
            MultiCapabilities(capabilitiesList, multiIndexLine.description, multiIndexLine.id)
        ctx.json(multiCapabilities)
    }

    fun storeGetInput(ctx: Context, index: LibraryIndex, store: String) {
        val id = ctx.queryParam("id")
        validateId(id)

        val indexLine = index.findByInput(id) ?: return ctx.notFound()
        val compressed = indexLine.compressed
        val filePath = "$store/input/$id"

        val file = IOUtils.getInputSource(filePath, compressed) ?: return ctx.notFound()
        ctx.attachFile(file, id, ContentType.APPLICATION_OCTET_STREAM)
    }

    fun storeListFiltered(ctx: Context, index: LibraryIndex, store: String) {
        val request = ctx.bodyAsClassEfficient<Query>()
        val result = index.filterByQuery(request, store)
        ctx.json(result)
    }

    fun getOpenApi(ctx: Context) {
        val openapi = {}.javaClass.getResourceAsStream("/swagger/openapi.json")
        if (openapi != null) {
            val text = openapi.reader().readText()
            ctx.contentType(ContentType.JSON)
            ctx.result(text)
        }
    }
}
