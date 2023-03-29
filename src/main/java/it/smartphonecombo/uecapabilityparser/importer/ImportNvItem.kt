package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.jvm.Throws

private const val MAX_CC = 5

/**
 * A parser for Qualcomm NVItem 28874 (RFNV_LTE_CA_BW_CLASS_COMBO_I).
 *
 * This NVItem is found in the latest Qualcomm 4G modems (~ 2017 and beyond) and defines supported
 * LTE Carrier Aggregations.
 *
 * This NVItem isn't found in Qualcomm 5G modems.
 *
 * Inspired by [28874Decoder](https://github.com/HandyMenny/28874Decoder)
 */
object ImportNvItem : ImportCapabilities {

    /**
     * This parser take as [input] an [InputStream] of a decompressed NVItem 28874.
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It supports 28874 containing the following descriptor types: 137, 138, 201, 202, 333, 334.
     *
     * Throws an [IllegalArgumentException] if an invalid or unsupported descriptor type is found.
     */
    @Throws(IllegalArgumentException::class)
    override fun parse(input: InputStream): Capabilities {
        var dlComponents = emptyList<ComponentLte>()
        val listCombo = ArrayList<ComboLte>()
        val byteArray = input.use(InputStream::readBytes)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.skipBytes(4)

        while (byteBuffer.remaining() > 0) {
            when (val itemType = byteBuffer.readUnsignedShort()) {
                333,
                201,
                137 -> {
                    // Get DL Components
                    dlComponents = parseItem(byteBuffer, itemType)
                }
                334,
                202,
                138 -> {
                    // Get UL Components
                    val ulComponents = parseItem(byteBuffer, itemType)
                    // merge DL and UL Components
                    val bandArray = mergeAndSort(dlComponents, ulComponents)
                    listCombo.add(ComboLte(bandArray))
                }
                else -> throw IllegalArgumentException("Invalid item type")
            }
        }
        return Capabilities(listCombo)
    }

    /**
     * Parses a descriptor/item. It supports the following descriptor types: 137, 138, 201, 202,
     * 333, 334.
     *
     * Throws an [IllegalArgumentException] if an invalid or unsupported descriptor type is found.
     */
    @Throws(IllegalArgumentException::class)
    private fun parseItem(input: ByteBuffer, descriptorType: Int): List<ComponentLte> {
        return when (descriptorType) {
            334 -> readComponents(input, hasMimo = true, hasMultiMimo = true, isDL = false)
            333 -> readComponents(input, hasMimo = true, hasMultiMimo = true)
            202 -> readComponents(input, hasMimo = true, isDL = false)
            201 -> readComponents(input, hasMimo = true)
            138 -> readComponents(input, isDL = false)
            137 -> readComponents(input)
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    /** Read/Parse carrier components of a single descriptor from the given input. */
    private fun readComponents(
        input: ByteBuffer,
        hasMimo: Boolean = false,
        hasMultiMimo: Boolean = false,
        isDL: Boolean = true
    ): List<ComponentLte> {
        val lteComponents = mutableListOf<ComponentLte>()

        for (i in 0..MAX_CC) {
            // read band and bwClass
            val band = input.readUnsignedShort()
            val bwClass = BwClass.valueOf(input.readUnsignedByte())

            // read mimo/multiMimo
            var ant = if (isDL) 2 else 1
            if (hasMimo) {
                ant = input.readUnsignedByte()
                if (hasMultiMimo) {
                    repeat(7) {
                        // Ignore mimo for each component in intraband contigous CA
                        input.readUnsignedByte()
                    }
                }
            }

            if (band == 0) {
                // Null/Empty component
                continue
            }

            val component =
                if (isDL) {
                    ComponentLte(band, bwClass, BwClass.NONE, ant)
                } else {
                    ComponentLte(band, BwClass.NONE, bwClass)
                }

            lteComponents.add(component)
        }

        return lteComponents
    }

    /** Merge DL Components and UL Components */
    private fun mergeAndSort(
        dlComponents: List<ComponentLte>,
        ulComponents: List<ComponentLte>
    ): Array<ComponentLte> {
        val components = dlComponents.mapToTypedArray(ComponentLte::clone)
        for (ulComponent in ulComponents) {
            val matchingComponent =
                components
                    .filter { it.band == ulComponent.band && it.classUL == BwClass.NONE }
                    .maxBy(ComponentLte::classDL)

            matchingComponent.classUL = ulComponent.classUL
        }

        components.sortDescending()
        return components
    }

    /** Like map but returning a typedArray */
    private inline fun <reified T> List<T>.mapToTypedArray(transform: (T) -> T) =
        Array(size, init = { i -> transform(this[i]) })
}
