package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.readUByte
import it.smartphonecombo.uecapabilityparser.extension.readUShortLE
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.toMimo
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.InflaterInputStream

private const val MAX_CC = 6

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
     * This parser take as [input] a [InputSource] of an uncompressed or zlib-compressed
     * NVItem 28874.
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It supports 28874 containing the following descriptor types: 137, 138, 201, 202, 333, 334.
     *
     * Throws an [IllegalArgumentException] if an invalid or unsupported descriptor type is found.
     */
    @Throws(IllegalArgumentException::class)
    override fun parse(input: InputSource): Capabilities {
        var dlComponents = emptyList<ComponentLte>()
        val listCombo = mutableListOf<ComboLte>()
        var stream: InputStream = BufferedInputStream(input.inputStream())

        try {
            stream.mark(32) // set mark for reset
            val isZlib = stream.readUShortLE() == 0x9C78 // zlib header check
            stream.reset() // Reset stream
            if (isZlib) stream = InflaterInputStream(stream) // Inflate stream

            stream.skipBytes(4)

            while (true) {
                when (val itemType = stream.readUShortLE()) {
                    333,
                    201,
                    137 -> {
                        // Get DL Components
                        dlComponents = parseItem(stream, itemType)
                    }
                    334,
                    202,
                    138 -> {
                        // Get UL Components
                        val ulComponents = parseItem(stream, itemType)
                        // merge DL and UL Components
                        listCombo.add(ComboLte(dlComponents, ulComponents))
                    }
                    else -> throw IllegalArgumentException("Invalid item type")
                }
            }
        } catch (_: IOException) {
            // do nothing
        }
        stream.close()
        return Capabilities(listCombo)
    }

    /**
     * Parses a descriptor/item. It supports the following descriptor types: 137, 138, 201, 202,
     * 333, 334.
     *
     * Throws an [IllegalArgumentException] if an invalid or unsupported descriptor type is found.
     */
    @Throws(IllegalArgumentException::class)
    private fun parseItem(input: InputStream, descriptorType: Int): List<ComponentLte> {
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
        input: InputStream,
        hasMimo: Boolean = false,
        hasMultiMimo: Boolean = false,
        isDL: Boolean = true
    ): List<ComponentLte> {
        val lteComponents = mutableListWithCapacity<ComponentLte>(MAX_CC)

        for (i in 0 until MAX_CC) {
            // read band and bwClass
            val band = input.readUShortLE()
            val bwClass = BwClass.valueOf(input.readUByte())

            // read mimo/multiMimo
            val ant =
                if (!hasMimo) {
                    EmptyMimo
                } else if (!hasMultiMimo) {
                    input.readUByte().toMimo()
                } else {
                    val list = mutableListWithCapacity<Int>(8)
                    repeat(8) {
                        val antSubCC = input.readUByte()
                        if (antSubCC > 0) {
                            list.add(antSubCC)
                        }
                    }
                    Mimo.from(list)
                }

            if (band == 0) {
                // Null/Empty component
                continue
            }

            val component =
                if (isDL) {
                    ComponentLte(band, bwClass, BwClass.NONE, ant)
                } else {
                    ComponentLte(band, BwClass.NONE, bwClass, mimoUL = ant)
                }

            lteComponents.add(component)
        }

        return lteComponents
    }
}
