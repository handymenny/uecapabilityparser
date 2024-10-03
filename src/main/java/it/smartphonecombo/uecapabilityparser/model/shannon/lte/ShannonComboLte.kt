@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.lte

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("Combo")
data class ShannonComboLte(
    /** List of Components. */
    @ProtoNumber(1) val components: List<ShannonComponentLte> = emptyList(),
    /**
     * The supportedBandwidthCombinationSet of this combo.
     *
     * It's stored as a 32bit unsigned int, each of its bits has the same value of the corresponding
     * bit in the BitString. 0 means default i.e. only BCS 0 supported (if applicable).
     */
    @ProtoNumber(2) @SerialName("bcs") private val rawBcs: Long? = null,
    @ProtoNumber(3) val unknown1: Long,
    @ProtoNumber(4) val unknown2: Long,
) {
    val bcs
        get() =
            when (rawBcs) {
                0L -> BCS.fromQualcommCP("0")
                null -> EmptyBCS
                else -> rawBcs.toString(2).padStart(32, '0').let { BCS.fromBinaryString(it) }
            }
}
