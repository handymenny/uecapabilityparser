@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.nameWithoutAnyExtension
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import java.io.File
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * A subset of Capabilities + inputs, description and compressed fields. It can be deserialized from
 * capabilities json.
 */
@Serializable
data class IndexLine(
    val id: String,
    val timestamp: Long,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) var description: String = "",
    var inputs: List<String> = emptyList(),
    var compressed: Boolean = false,
    val parserVersion: String,
) {
    // This is only used to extract description from capabilities JSON
    private var metadata: MutableMap<String, String>? = null

    init {
        // if metadata is set, extract description from that
        metadata?.let {
            description = it["description"] ?: ""
            // unset metadata to free resources
            metadata = null
        }
    }

    companion object {
        fun fromFile(outputFile: File, inputFiles: List<File>): IndexLine {
            val compressed = outputFile.extension == "gz"

            val capTxt = outputFile.toInputSource(compressed)

            // Drop any extension
            val id = outputFile.nameWithoutAnyExtension()

            val inputs =
                inputFiles.filter { it.name.startsWith(id) }.map(File::nameWithoutAnyExtension)

            val index = Json.custom().decodeFromInputSource<IndexLine>(capTxt)
            index.compressed = compressed
            index.inputs = inputs

            return index
        }
    }
}
