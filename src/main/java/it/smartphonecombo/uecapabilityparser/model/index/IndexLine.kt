package it.smartphonecombo.uecapabilityparser.model.index

import kotlinx.serialization.Serializable

@Serializable
data class IndexLine(
    val id: String,
    val timestamp: Long,
    val description: String,
    val inputs: List<String>,
    val compressed: Boolean = false,
    val defaultNR: Boolean = false
)
