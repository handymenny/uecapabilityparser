package it.smartphonecombo.uecapabilityparser.server

import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.query.SearchableField
import kotlinx.serialization.Serializable

@Serializable
data class ServerStatus(
    val version: String,
    val endpoints: List<String>,
    val logTypes: List<LogType>,
    val maxRequestSize: Long,
    val searchableFields: List<SearchableField>
)
