package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.nameWithoutAnyExtension
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.IO
import it.smartphonecombo.uecapabilityparser.util.IO.readTextFromFile
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class LibraryIndex(private val items: MutableList<IndexLine>) {
    @Transient private val lock = Any()

    fun addLine(line: IndexLine): Boolean {
        synchronized(lock) {
            return items.add(line)
        }
    }

    fun find(id: String): IndexLine? {
        return items.find { it.id == id }
    }

    fun findByInput(id: String): IndexLine? {
        return items.find { item -> item.inputs.any { it == id } }
    }

    fun findByOutput(id: String): IndexLine? = find(id)

    /** return a list of all elements */
    fun getAll() = items.toList()

    companion object {
        fun buildIndex(path: String): LibraryIndex {
            val outputDir = "$path/output"
            val inputDir = "$path/input"

            // Create directories if they don't exist
            IO.createDirectories(outputDir)
            IO.createDirectories(inputDir)

            val outputFiles = File(outputDir).listFiles() ?: emptyArray()
            val inputFiles = File(inputDir).listFiles() ?: emptyArray()
            // Sort files to make library consistent
            inputFiles.sort()
            outputFiles.sort()
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
                                capabilities.getStringMetadata("defaultNR").toBoolean()
                            )
                        } catch (ex: Exception) {
                            System.err.println("Error ${ex.localizedMessage}")
                            null
                        }
                    }
                    .toMutableList()
            items.sortBy { it.timestamp }
            return LibraryIndex(items)
        }
    }
}
