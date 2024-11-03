package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.mapAsync
import it.smartphonecombo.uecapabilityparser.extension.nameWithoutAnyExtension
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.IOUtils.createDirectories
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.query.Query
import it.smartphonecombo.uecapabilityparser.util.LruCache
import it.smartphonecombo.uecapabilityparser.util.optimize
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@SerialName("LibraryIndex")
data class LibraryIndexImmutable(
    @Required val items: List<IndexLine>,
    val multiItems: List<MultiIndexLine> = emptyList(),
)

class LibraryIndex(
    private val items: MutableMap<String, IndexLine> = mutableMapOf(),
    private val multiItems: MutableMap<String, MultiIndexLine> = mutableMapOf(),
    outputCacheSize: Int? = 0,
) {
    private val lock = Any()
    private val outputCache = LruCache<String, Capabilities>(outputCacheSize)

    fun addLine(line: IndexLine) {
        synchronized(lock) { items[line.id] = line }
    }

    fun replaceLine(line: IndexLine) {
        synchronized(lock) {
            items[line.id] = line
            outputCache.remove(line.id)
        }
    }

    fun addMultiLine(line: MultiIndexLine) {
        synchronized(lock) { multiItems[line.id] = line }
    }

    fun find(id: String): IndexLine? = items[id]

    fun findMulti(id: String): MultiIndexLine? = multiItems[id]

    fun findByInput(id: String): IndexLine? {
        return getAll().find { item -> item.inputs.any { it == id } }
    }

    /** return an immutable list of all single-capability indexes */
    fun getAll() = toImmutableIndex().items

    /** return an immutable list of all multi-capability indexes */
    fun getMultiIndexes() = toImmutableIndex().multiItems

    fun getOutput(id: String, libraryPath: String, cacheIfFull: Boolean = true): Capabilities? {
        val cached = outputCache[id]
        if (cached != null) return cached

        val indexLine = find(id) ?: return null
        val compressed = indexLine.compressed
        val filePath = "$libraryPath/output/$id.json"
        val text = IOUtils.getInputSource(filePath, compressed) ?: return null
        val res = Json.custom().decodeFromInputSource<Capabilities>(text)
        if (outputCache.put(id, res, !cacheIfFull)) res.optimize()
        return res
    }

    fun filterByQuery(query: Query, libraryPath: String): LibraryIndexImmutable {
        val threadCount = minOf(Runtime.getRuntime().availableProcessors(), 4)
        val dispatcher = Dispatchers.IO.limitedParallelism(threadCount)

        // immutable copies of items and multi items
        val immutable = toImmutableIndex()
        val multiItemsList = immutable.multiItems
        val itemsList = immutable.items

        val validIdsDeferred =
            CoroutineScope(dispatcher).async {
                itemsList.mapAsync { cap ->
                    val res = getOutput(cap.id, libraryPath, false)?.let { query.evaluateQuery(it) }
                    if (res == true) cap.id else null
                }
            }

        val validIds = runBlocking { validIdsDeferred.await().filterNotNull().toSet() }

        val multiItemsFiltered =
            multiItemsList.filter { it.indexLineIds.any { id -> validIds.contains(id) } }
        val itemsFiltered = itemsList.filter { validIds.contains(it.id) }

        return LibraryIndexImmutable(itemsFiltered, multiItemsFiltered)
    }

    fun toImmutableIndex(): LibraryIndexImmutable {
        val itemsList: List<IndexLine>
        val multiItemsList: List<MultiIndexLine>

        synchronized(lock) {
            itemsList = items.values.toList()
            multiItemsList = multiItems.values.toList()
        }

        return LibraryIndexImmutable(itemsList, multiItemsList)
    }

    companion object {
        fun buildIndex(path: String, outputCacheSize: Int?): LibraryIndex {
            val outputDir = "$path/output"
            val inputDir = "$path/input"
            val multiDir = "$path/multi"

            // Create directories if they don't exist
            arrayOf(outputDir, inputDir, multiDir).forEach { createDirectories(it) }

            val outputFiles = File(outputDir).listFiles() ?: emptyArray()
            val inputFiles = File(inputDir).listFiles() ?: emptyArray()
            val multiFiles = File(multiDir).listFiles() ?: emptyArray()

            // Sort files to make library consistent
            arrayOf(outputFiles, inputFiles, multiFiles).forEach { it.sort() }

            val items =
                outputFiles
                    .mapNotNull { outputFile ->
                        try {
                            val compressed = outputFile.extension == "gz"

                            val capTxt = outputFile.toInputSource(compressed)

                            // Drop any extension
                            val id = outputFile.nameWithoutAnyExtension()

                            val inputs =
                                inputFiles
                                    .filter { it.name.startsWith(id) }
                                    .map(File::nameWithoutAnyExtension)

                            val index = Json.custom().decodeFromInputSource<IndexLine>(capTxt)
                            index.compressed = compressed
                            index.inputs = inputs

                            return@mapNotNull index
                        } catch (ex: Exception) {
                            echoSafe("Error ${ex.localizedMessage}", true)
                            null
                        }
                    }
                    .toMutableList()

            val multiItems =
                multiFiles
                    .mapNotNull { multiFile ->
                        try {
                            val compressed = multiFile.extension == "gz"
                            val jsonTxt = multiFile.toInputSource(compressed)
                            Json.custom().decodeFromInputSource<MultiIndexLine>(jsonTxt)
                        } catch (ex: Exception) {
                            echoSafe("Error ${ex.localizedMessage}", true)
                            null
                        }
                    }
                    .toMutableList()

            items.sortBy { it.timestamp }
            multiItems.sortBy { it.timestamp }

            val itemMap = items.associateBy { it.id }.toMutableMap()
            val multiMap = multiItems.associateBy { it.id }.toMutableMap()

            return LibraryIndex(itemMap, multiMap, outputCacheSize)
        }
    }
}
