package it.smartphonecombo.uecapabilityparser.model

import java.util.*
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultiCapabilities(
    @SerialName("capabilitiesList") val capabilities: List<Capabilities> = emptyList(),
    @SerialName("description") val description: String = "",
    @Required @SerialName("id") var id: String = UUID.randomUUID().toString(),
)
