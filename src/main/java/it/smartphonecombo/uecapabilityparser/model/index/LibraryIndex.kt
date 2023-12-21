package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.nameWithoutAnyExtension
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.io.IOUtils.readTextFromFile
import it.smartphonecombo.uecapabilityparser.model.Capabilities
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

    fun findByOutput(id: String): IndexLine? = find(id)

    /** return a list of all single-capability indexes */
    fun getAll() = items.toList()

    companion object {
        fun buildIndex(path: String): LibraryIndex {
            val outputDir = "$path/output"
            val inputDir = "$path/input"
            val multiDir = "$path/multi"

            // Create directories if they don't exist
            arrayOf(outputDir, inputDir, multiDir).forEach { IOUtils.createDirectories(it) }

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

                            val capStr =
                                readTextFromFile(outputFile, compressed) ?: return@mapNotNull null

                            // Drop any extension
                            val id = outputFile.nameWithoutAnyExtension()

                            val capabilities = Json.custom().decodeFromString<Capabilities>(capStr)
                            val inputs =
                                inputFiles
                                    .filter { it.name.startsWith(id) }
                                    .map(File::nameWithoutAnyExtension)

                            IndexLine(
                                id,
                                capabilities.timestamp,
                                capabilities.getStringMetadata("description") ?: "",
                                inputs,
                                compressed,
                                capabilities.getStringMetadata("defaultNR").toBoolean(),
                                capabilities.parserVersion
                            )
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
                            val jsonTxt = readTextFromFile(multiFile, compressed)

                            jsonTxt?.let { Json.custom().decodeFromString<MultiIndexLine>(it) }
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
