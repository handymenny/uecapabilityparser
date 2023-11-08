package it.smartphonecombo.uecapabilityparser.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultiCapabilities(
    @SerialName("capabilitiesList") val capabilities: List<Capabilities> = emptyList(),
    @SerialName("description") val description: String = "",
    @SerialName("id") var id: String
)
