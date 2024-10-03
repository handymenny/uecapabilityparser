package it.smartphonecombo.uecapabilityparser.model.nsg

import it.smartphonecombo.uecapabilityparser.io.DateTimeSerializer
import it.smartphonecombo.uecapabilityparser.io.HexSerializer
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NsgJsonLog(
    @SerialName("version") val formatVersion: Int,
    @SerialName("library") val libraryVersion: String,
    @SerialName("device") val device: NsgDevice,

    // log skipped, not useful for us

    @SerialName("starttime") @Serializable(with = DateTimeSerializer::class) val startTime: Long,
    @SerialName("endtime") @Serializable(with = DateTimeSerializer::class) val endTime: Long,
    @SerialName("PCAPHeader")
    @Serializable(with = HexSerializer::class)
    val pcapHeader: ByteArrayDeepEquals,
    @SerialName("data") val data: List<NsgDataItem> = emptyList(),
)

@Serializable
data class NsgDevice(val index: Int, val name: String, val subscription: Int, val type: String)

@Serializable
data class NsgDataItem(
    // We only care about messages
    val messages: List<NsgMessage> = emptyList()
)

@Serializable
data class NsgMessage(
    // We only care about pcap packet and title
    @SerialName("PCAPPacket")
    @Serializable(with = HexSerializer::class)
    val pcapPacket: ByteArrayDeepEquals? = null,
    @SerialName("Title") val title: String = "",
) {
    fun isUeCap() = title.contains("UE Cap", true) || title.contains("Segment", true)
}
