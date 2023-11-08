package it.smartphonecombo.uecapabilityparser.model.index

import kotlinx.serialization.Serializable

@Serializable
data class MultiIndexLine(
    val id: String,
    val timestamp: Long,
    val description: String,
    val indexLineIds: List<String>,
    val compressed: Boolean = false
)
