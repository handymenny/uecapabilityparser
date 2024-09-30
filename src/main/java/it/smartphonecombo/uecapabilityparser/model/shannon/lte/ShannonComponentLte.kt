@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model.shannon.lte

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.toMimo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("Component")
data class ShannonComponentLte(
    /** LTE Bands are stored as int. */
    @ProtoNumber(1) @SerialName("band") private val band: Int,
    /**
     * First 8 bits encode mimo, second 8 bits encode bw class. Bw class is encoded setting to 1 the
     * bit representing the bw class index. Ex. 1000 0000 = class A (index 0), 0100 0000 = class B
     * (index 1), 00100 0000 = class C (index 2)
     *
     * Mimo is encoded as enum, 0 -> 2, 1 -> 4
     */
    @ProtoNumber(2) @SerialName("bwClassMimoDl") private val rawBwClassMimoDl: Int,
    /**
     * First 8 bits encode mimo, second 8 bits encode bw class. Bw class is encoded setting to 1 the
     * bit representing the bw class index. Ex. 1000 0000 = class A (index 0), 0100 0000 = class B
     * (index 1), 00100 0000 = class C (index 2)
     *
     * Mimo is encoded as enum, 0 -> 1, 1 -> 2
     */
    @ProtoNumber(3) @SerialName("bwClassMimoUl") private val rawBwClassMimoUl: Int,
) {

    val bwClassDl: BwClass
        get() = fromRawToBwClass(rawBwClassMimoDl)

    val bwClassUl: BwClass
        get() = fromRawToBwClass(rawBwClassMimoUl)

    val mimoDl: Mimo
        get() = fromRawToMimo(rawBwClassMimoDl, LinkDirection.DOWNLINK)

    val mimoUl: Mimo
        get() = fromRawToMimo(rawBwClassMimoUl, LinkDirection.UPLINK)

    fun toComponent(): ComponentLte {
        return ComponentLte(band, bwClassDl, bwClassUl, mimoDl, mimoUl)
    }

    private fun fromRawToMimo(raw: Int, direction: LinkDirection): Mimo {
        val mimoIndex = (raw and 0xF) + 1
        val shift = if (direction == LinkDirection.DOWNLINK) 1 else 0

        val mimo = mimoIndex shl shift

        return mimo.toMimo()
    }

    private fun fromRawToBwClass(raw: Int): BwClass {
        // bits 8 - 15
        val upperHalf = raw shr 8 and 0xFF
        val bitString = upperHalf.toString(2).padStart(8, '0')

        // get position of first 1
        val indexOfFirstOne = bitString.indexOf("1") + 1

        return BwClass.valueOf(indexOfFirstOne)
    }
}
