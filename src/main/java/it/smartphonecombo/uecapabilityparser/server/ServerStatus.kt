package it.smartphonecombo.uecapabilityparser.server

import it.smartphonecombo.uecapabilityparser.model.LogType
import kotlinx.serialization.Serializable

@Serializable
data class ServerStatus(
    val version: String,
    val endpoints: List<String>,
    val logTypes: List<LogType>,
    val maxRequestSize: Long
)
