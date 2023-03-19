package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val MAX_CC = 5

class ImportNvItem : ImportCapabilities {
    override fun parse(filename: String): Capabilities {
        var lteComponents = emptyArray<IComponent>()
        val listCombo = ArrayList<ComboLte>()
        try {
            val file = File(filename)
            val byteArray = file.inputStream().use { it.readAllBytes() }
            val input = ByteBuffer.wrap(byteArray)
            input.order(ByteOrder.LITTLE_ENDIAN)
            input.skipBytes(4)
            while (input.remaining() > 0) {
                when (input.readUnsignedShort()) {
                    333 -> {
                        lteComponents = readDLbands(input, true, 7)
                        if (lteComponents.isEmpty()) {
                            return Capabilities()
                        }
                    }
                    334 -> {
                        val combo = readULbands(input, lteComponents, true, 7)
                        listCombo.add(combo)
                    }
                    201 -> {
                        lteComponents = readDLbands(input, true, 0)
                        if (lteComponents.isEmpty()) {
                            return Capabilities()
                        }
                    }
                    202 -> {
                        val combo = readULbands(input, lteComponents, true, 0)
                        listCombo.add(combo)
                    }
                    137 -> {
                        lteComponents = readDLbands(input, false, 0)
                        if (lteComponents.isEmpty()) {
                            return Capabilities()
                        }
                    }
                    138 -> {
                        val combo = readULbands(input, lteComponents, false, 0)
                        listCombo.add(combo)
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {}
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
