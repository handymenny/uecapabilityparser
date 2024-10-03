package it.smartphonecombo.uecapabilityparser.model.bandwidth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BwsNr(
    val scs: Int,
    @SerialName("bandwidthsDl") val bwsDL: IntArray,
    @SerialName("bandwidthsUl") val bwsUL: IntArray,
) {
    constructor(scs: Int, bwsDlUL: IntArray) : this(scs, bwsDlUL, bwsDlUL)

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
