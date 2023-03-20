package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val MAX_CC = 5

class ImportNvItem : ImportCapabilities {
    override fun parse(input: InputStream): Capabilities {
        var lteComponents = emptyArray<IComponent>()
        val listCombo = ArrayList<ComboLte>()
        val byteArray = input.use(InputStream::readBytes)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.skipBytes(4)
        while (byteBuffer.remaining() > 0) {
            when (byteBuffer.readUnsignedShort()) {
                333 -> {
                    lteComponents = readDLbands(byteBuffer, true, 7)
                    if (lteComponents.isEmpty()) {
                        return Capabilities()
                    }
                }
                334 -> {
                    val combo = readULbands(byteBuffer, lteComponents, true, 7)
                    listCombo.add(combo)
                }
                201 -> {
                    lteComponents = readDLbands(byteBuffer, true, 0)
                    if (lteComponents.isEmpty()) {
                        return Capabilities()
                    }
                }
                202 -> {
                    val combo = readULbands(byteBuffer, lteComponents, true, 0)
                    listCombo.add(combo)
                }
                137 -> {
                    lteComponents = readDLbands(byteBuffer, false, 0)
                    if (lteComponents.isEmpty()) {
                        return Capabilities()
                    }
                }
                138 -> {
                    val combo = readULbands(byteBuffer, lteComponents, false, 0)
                    listCombo.add(combo)
                }
                else -> {}
            }
        }
        return Capabilities(listCombo)
    }

    private fun readDLbands(
        input: ByteBuffer,
        mimoPresent: Boolean,
        additionalBytes: Int
    ): Array<IComponent> {
        val lteComponents: MutableList<ComponentLte> = ArrayList()
        for (i in 0..MAX_CC) {
            val band = input.readUnsignedShort()
            val bclass = (input.readUnsignedByte() + 0x40).toChar()
            var ant = 2
            if (mimoPresent) {
                ant = input.readUnsignedByte()
            }
            input.skipBytes(additionalBytes)
            if (band != 0) {
                lteComponents.add(ComponentLte(band, bclass, '0', ant, null, null))
            }
        }
        lteComponents.sortWith(IComponent.defaultComparator.reversed())
        return lteComponents.toTypedArray()
    }

    private fun readULbands(
        input: ByteBuffer,
        dlBands: Array<IComponent>,
        mimoPresent: Boolean,
        additionalBytes: Int
    ): ComboLte {
        val copyBand = mutableListOf<IComponent>()
        var i = 0
        while (i < dlBands.size) {
            copyBand.add((dlBands[i] as ComponentLte).copy())
            i++
        }
        val numberOfDLbands = copyBand.size
        i = 0
        while (i < numberOfDLbands) {
            val band = input.readUnsignedShort()
            val ulClass = (input.readUnsignedByte() + 0x40).toChar()
            var ant = 1
            if (mimoPresent) {
                ant = input.readUnsignedByte()
            }
            input.skipBytes(additionalBytes)
            if (band != 0) {
                for (dlBand in copyBand) {
                    if (band == dlBand.band) {
                        dlBand.classUL = ulClass
                        break
                    }
                }
            }
            i++
        }
        while (i <= MAX_CC) {
            input.skipBytes(additionalBytes + 3 + if (mimoPresent) 1 else 0)
            i++
        }
        return ComboLte(copyBand.toTypedArray())
    }
}
