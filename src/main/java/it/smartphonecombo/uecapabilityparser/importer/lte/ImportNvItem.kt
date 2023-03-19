package it.smartphonecombo.uecapabilityparser.importer.lte

import com.mindprod.ledatastream.LERandomAccessFile
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.EOFException
import java.io.IOException

private const val MAX_CC = 5

class ImportNvItem : ImportCapabilities {
    override fun parse(filename: String): Capabilities {
        var lteComponents = emptyArray<IComponent>()
        val listCombo = ArrayList<ComboLte>()
        var input: LERandomAccessFile? = null
        try {
            input = LERandomAccessFile(filename, "r")
            input.skipBytes(4)
            while (true) {
                try {
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
                } catch (ex: EOFException) {
                    break
                }
            }
        } catch (e: Exception) {} finally {
            try {
                input?.close()
            } catch (e: IOException) {}
        }
        return Capabilities(listCombo)
    }

    @Throws(IOException::class)
    private fun readDLbands(
        input: LERandomAccessFile,
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

    @Throws(IOException::class)
    private fun readULbands(
        input: LERandomAccessFile,
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
