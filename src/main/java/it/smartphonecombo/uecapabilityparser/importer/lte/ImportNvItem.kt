package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.extension.toBwClass
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val MAX_CC = 5

object ImportNvItem : ImportCapabilities {

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
                    dlComponents = parseItem(byteBuffer, itemType)
                }
                334,
                202,
                138 -> {
                    val ulComponents = parseItem(byteBuffer, itemType)
                    val bandArray = mergeAndSort(dlComponents, ulComponents)
                    listCombo.add(ComboLte(bandArray))
                }
                else -> throw IllegalArgumentException("Invalid item type")
            }
        }
        return Capabilities(listCombo)
    }

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
            val bwClass = input.readUnsignedByte().toBwClass()

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
                    ComponentLte(band, bwClass, '0', ant, null, null)
                } else {
                    ComponentLte(band, '0', bwClass, 0, null, null)
                }

            lteComponents.add(component)
        }

        return lteComponents
    }

    private fun mergeAndSort(
        dlComponents: List<ComponentLte>,
        ulComponents: List<ComponentLte>
    ): Array<IComponent> {
        val components = dlComponents.mapToTypedArray(IComponent::clone)
        for (ulComponent in ulComponents) {
            val matchingComponent =
                components
                    .filter { it.band == ulComponent.band && it.classUL == '0' }
                    .maxBy(IComponent::classDL)

            matchingComponent.classUL = ulComponent.classUL
        }

        components.sortWith(IComponent.defaultComparator.reversed())
        return components
    }

    private inline fun <reified T> List<T>.mapToTypedArray(transform: (T) -> T) =
        Array(size, init = { i -> transform(this[i]) })
}
