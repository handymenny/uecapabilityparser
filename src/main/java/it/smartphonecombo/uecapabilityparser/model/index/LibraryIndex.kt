package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.util.Output
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

    companion object {
        fun buildIndex(path: String): LibraryIndex {
            val outputDir = "$path/output"
            val inputDir = "$path/input"

            // Create directories if they don't exist
            Output.createDirectories(outputDir)
            Output.createDirectories(inputDir)

            val outputFiles = File(outputDir).listFiles() ?: emptyArray()
            val inputFiles = File(inputDir).listFiles() ?: emptyArray()
            // Sort files to make library consistent
            inputFiles.sort()
            outputFiles.sort()
            val items =
                outputFiles
                    .mapNotNull { outputFile ->
                        try {
                            val capabilities =
                                Json.custom().decodeFromString<Capabilities>(outputFile.readText())
                            val inputs =
                                inputFiles
                                    .filter { inputFile ->
                                        inputFile.nameWithoutExtension.startsWith(
                                            outputFile.nameWithoutExtension
                                        )
                                    }
                                    .map { it.name }

                            IndexLine(
                                outputFile.nameWithoutExtension,
                                capabilities.timestamp,
                                capabilities.getStringMetadata("description") ?: "",
                                inputs
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
