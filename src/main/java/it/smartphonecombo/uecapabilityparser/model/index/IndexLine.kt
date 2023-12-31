@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.index

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

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
    var defaultNR: Boolean = false,
    val parserVersion: String
) {
    // This is only used to extract description and defaultNR from capabilities JSON
    private var metadata: MutableMap<String, String>? = null

    init {
        // if metadata is set, extract description and default nr from that
        metadata?.let {
            description = it["description"] ?: ""
            defaultNR = it["defaultNR"].toBoolean()
            // unset metadata to free resources
            metadata = null
        }
    }
}
