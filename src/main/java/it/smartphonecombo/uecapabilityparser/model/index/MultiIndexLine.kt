package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.extension.decodeFromInputSource
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MultiIndexLine(
    val id: String,
    val timestamp: Long,
    val description: String,
    val indexLineIds: List<String>,
    val compressed: Boolean = false,
) {
    companion object {
        fun fromFile(multiIndexFile: File): MultiIndexLine {
            val compressed = multiIndexFile.extension == "gz"
            val jsonTxt = multiIndexFile.toInputSource(compressed)
            val multiLine = Json.custom().decodeFromInputSource<MultiIndexLine>(jsonTxt)

            return multiLine
        }
    }
}
