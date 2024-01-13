package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.nameWithoutAnyExtension
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.IOUtils.createDirectories
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.LruCache
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class LibraryIndex(
    private val items: MutableList<IndexLine>,
    private val multiItems: MutableList<MultiIndexLine> = mutableListOf()
) {
    @Transient private val lock = Any()
    @Transient private val outputCache = LruCache<String, Capabilities>()

    fun addLine(line: IndexLine): Boolean {
        synchronized(lock) {
            return items.add(line)
        }
    }

    fun addMultiLine(line: MultiIndexLine): Boolean {
        synchronized(lock) {
            return multiItems.add(line)
        }
    }

    fun find(id: String): IndexLine? {
        return items.find { it.id == id }
    }

    fun findMulti(id: String): MultiIndexLine? {
        return multiItems.find { it.id == id }
    }

    fun findByInput(id: String): IndexLine? {
        return items.find { item -> item.inputs.any { it == id } }
    }

    /** return a list of all single-capability indexes */
    fun getAll() = items.toList()

    fun getOutput(id: String, libraryPath: String): Capabilities? {
        val cached = outputCache[id]
        if (cached != null) return cached

        val indexLine = find(id) ?: return null
        val compressed = indexLine.compressed
        val filePath = "$libraryPath/output/$id.json"
        val text = IOUtils.getInputSource(filePath, compressed) ?: return null
        val res = Json.custom().decodeFromInputSource<Capabilities>(text)
        outputCache.put(id, res)
        return res
    }

    companion object {
        fun buildIndex(path: String): LibraryIndex {
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

            return LibraryIndex(items, multiItems)
        }
    }
}
