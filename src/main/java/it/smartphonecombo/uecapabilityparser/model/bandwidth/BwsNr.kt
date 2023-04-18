package it.smartphonecombo.uecapabilityparser.model.bandwidth

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class BwsNr(
    val scs: Int,
    @SerialName("bandwidths-dl") val bwsDL: IntArray,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("bandwidths-ul")
    val bwsUL: IntArray = bwsDL
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BwsNr) return false

        if (scs != other.scs) return false
        if (!bwsDL.contentEquals(other.bwsDL)) return false
        if (!bwsUL.contentEquals(other.bwsUL)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scs
        result = 31 * result + bwsDL.contentHashCode()
        result = 31 * result + bwsUL.contentHashCode()
        return result
    }
}
