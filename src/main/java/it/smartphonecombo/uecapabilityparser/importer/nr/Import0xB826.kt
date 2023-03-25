package it.smartphonecombo.uecapabilityparser.importer.nr

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.extension.toBwClass
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Import0xB826 : ImportCapabilities {
    override fun parse(input: InputStream): Capabilities {
        val combos = Capabilities()
        val listCombo = ArrayList<ComboNr>()
        val byteArray = input.use(InputStream::readBytes)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        try {
            val logSize = getLogSize(byteBuffer, combos)
            combos.setMetadata("logSize", logSize)
            if (debug) {
                println("Log file size: $logSize bytes")
            }

            val version = byteBuffer.readUnsignedShort()
            combos.setMetadata("version", version)
            if (debug) {
                println("Version $version\n")
            }

            byteBuffer.skipBytes(2)

            val numCombos = getNumCombos(byteBuffer, version, combos)
            if (debug) {
                println("Num Combos $numCombos\n")
            }
            combos.setMetadata("numCombos", numCombos)

            val source: String? = getSource(version, byteBuffer)
            source?.let {
                combos.setMetadata("source", it)
                if (debug) {
                    println("source $it\n")
                }
            }

            for (i in 1..numCombos) {
                val combo = parseCombo(byteBuffer, version, source)
                listCombo.add(combo)
            }
        } catch (ignored: BufferUnderflowException) {
            // Do nothing
        }

        if (debug) {
            println(listCombo)
        }

        if (listCombo.isNotEmpty()) {
            if (listCombo.first().isEnDc) {
                combos.enDcCombos = listCombo
            } else if (listCombo.first().isNrDc) {
                combos.nrDcCombos = listCombo
            } else {
                combos.nrCombos = listCombo
            }
        }
        return combos
    }

    private fun getSource(
        version: Int,
        byteBuffer: ByteBuffer,
    ): String? {
        if (version <= 3) {
            return null
        }

        // Parse source field
        val sourceIndex = byteBuffer.readUnsignedByte()
        return getSourceFromIndex(sourceIndex)
    }

    private fun getNumCombos(byteBuffer: ByteBuffer, version: Int, combos: Capabilities): Int {
        // Version < 3 only has the num of combos of this log
        // version > 3 also have the total combos of the series and the index of this specific log
        if (version <= 3) {
            return byteBuffer.readUnsignedShort()
        }

        val totalCombos = byteBuffer.readUnsignedShort()
        combos.setMetadata("totalCombos", totalCombos)
        val index = byteBuffer.readUnsignedShort()
        combos.setMetadata("index", index)
        if (debug) {
            println("Total Numb Combos $totalCombos\n")
            println("Index $index\n")
        }
        return byteBuffer.readUnsignedShort()
    }

    private fun getLogSize(byteBuffer: ByteBuffer, combos: Capabilities): Int {
        // Try to read fileSize from the header
        val fileSize = byteBuffer.readUnsignedShort()

        // if fileSize = bufferSize 0xB826 has a header
        if (fileSize != byteBuffer.limit()) {
            // header missing, logSize is buffer size
            byteBuffer.rewind()
            return byteBuffer.limit()
        }

        val logItem = byteBuffer.readUnsignedShort().toString(16).uppercase()
        combos.setMetadata("logItem", "0x$logItem")
        if (debug) {
            println("Log Item: 0x$logItem")
        }
        // Skip the rest of the header
        byteBuffer.skipBytes(8)
        return fileSize
    }

    private fun parseCombo(
        byteBuffer: ByteBuffer,
        version: Int,
        source: String?,
    ): ComboNr {
        if (version >= 8) {
            byteBuffer.skipBytes(3)
        }
        val numBands = getNumBands(byteBuffer, version)
        val bands = mutableListOf<IComponent>()
        var nrbands = mutableListOf<IComponent>()
        var nrdcbands = mutableListOf<IComponent>()
        when (version) {
            6,
            8 -> byteBuffer.skipBytes(1)
            7 -> byteBuffer.skipBytes(3)
            in 9..13 -> byteBuffer.skipBytes(9)
            14 -> byteBuffer.skipBytes(25)
        }
        for (i in 0 until numBands) {
            val component = parseComponent(byteBuffer, version)
            if (component is ComponentNr) {
                nrbands.add(component)
            } else {
                bands.add(component)
            }
        }

        /*
         * We assume that 0xb826 without explicit combo type in source
         * don't support NR CA FR1-FR2.
         */
        if (bands.isEmpty() && !source.equals("RF_NRCA")) {
            val (fr2bands, fr1bands) = nrbands.partition { (it as ComponentNr).isFR2 }

            if (fr2bands.isNotEmpty() && fr1bands.isNotEmpty()) {
                nrbands = fr1bands.toMutableList()
                nrdcbands = fr2bands.toMutableList()
            }
        }

        val bandArray = bands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        val nrbandsArray =
            nrbands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        val nrdcbandsArray =
            nrdcbands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        return if (bandArray.isNotEmpty()) {
            ComboNr(bandArray, nrbandsArray)
        } else if (nrdcbandsArray.isNotEmpty()) {
            ComboNr(nrbandsArray, nrdcbandsArray)
        } else {
            ComboNr(nrbandsArray)
        }
    }

    private fun getNumBands(byteBuffer: ByteBuffer, version: Int): Int {
        val numBands = byteBuffer.readUnsignedByte()

        return if (version < 3) {
            numBands
        } else if (version <= 7) {
            numBands ushr 1
        } else {
            (numBands ushr 3) and 0x0F
        }
    }

    private fun parseComponent(byteBuffer: ByteBuffer, version: Int): IComponent {
        return if (version >= 8) {
            parseComponentV8(byteBuffer)
        } else {
            parseComponentPreV8(byteBuffer, version)
        }
    }

    private fun parseComponentPreV8(byteBuffer: ByteBuffer, version: Int): IComponent {
        val band = byteBuffer.readUnsignedShort()
        val bwType = byteBuffer.readUnsignedByte()
        val bwclass = (bwType ushr 1).toBwClass()
        val isNr = bwType % 2 == 1

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }

        component.classDL = bwclass
        component.mimoDL = getMimoFromIndex(byteBuffer.readUnsignedByte())
        val ulClass = byteBuffer.readUnsignedByte() ushr 1
        component.classUL = ulClass.toBwClass()
        val mimoUL = byteBuffer.readUnsignedByte()
        component.mimoUL = getMimoFromIndex(mimoUL)
        val modUL = byteBuffer.readUnsignedByte()
        component.modUL = getQamFromIndex(modUL)

        if (isNr) {
            val nrband = component as ComponentNr
            byteBuffer.skipBytes(1)
            if (version >= 6) {
                val mixed = byteBuffer.readUnsignedShort()
                nrband.scs = 15 * (1 shl (mixed and 0x000F) - 1)
                nrband.maxBandwidth = getBWFromIndex(mixed ushr 6 and 0x1F)
            } else if (version > 2) {
                nrband.scs = 15 * (1 shl byteBuffer.readUnsignedByte() - 1)
                nrband.maxBandwidth = byteBuffer.readUnsignedByte() shl 2
            } else {
                nrband.scs = 15 * (1 + byteBuffer.readUnsignedByte())
                nrband.maxBandwidth = byteBuffer.readUnsignedByte() shl 2
            }
        } else {
            byteBuffer.skipBytes(3)
        }
        return component
    }

    private fun parseComponentV8(byteBuffer: ByteBuffer): IComponent {
        val mixed = byteBuffer.readUnsignedShort()
        val band = mixed and 0x1FF
        var temp = mixed ushr 9 and 0x1F
        val bwclass = (temp ushr 1).toBwClass()
        val isNr = temp % 2 == 1

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }
        component.classDL = bwclass
        temp = byteBuffer.readUnsignedByte()
        val mimo = ((temp shl 1) and 0x7F) + (mixed ushr 15)
        component.mimoDL = getMimoFromIndex(mimo)
        val temp2 = byteBuffer.readUnsignedByte()
        val mimoUL = (temp2 ushr 3) and 0x7F
        component.mimoUL = getMimoFromIndex(mimoUL)
        val classUl = ((temp ushr 6) + (temp2 shl 2)) and 0x1F
        component.classUL = classUl.toBwClass()
        temp = byteBuffer.readUnsignedByte()
        val modUL = (temp shr 1) and 0x3
        component.modUL = getQamFromIndex(modUL)

        if (isNr) {
            val nrband = component as ComponentNr
            var scsIndex = temp
            temp = byteBuffer.readUnsignedByte()
            scsIndex = 1 shl ((((scsIndex ushr 7) + (temp and 3 shl 1)) and 0x000F) - 1)
            nrband.scs = 15 * (scsIndex)
            val maxBWindex = temp shr 2 and 0x1F
            nrband.maxBandwidth = getBWFromIndexV8(maxBWindex)
            byteBuffer.skipBytes(2)
        } else {
            byteBuffer.skipBytes(3)
        }
        return component
    }

    private fun getMimoFromIndex(index: Int): Int {
        return when (index) {
            0 -> 0
            1,
            25,
            16,
            9,
            4 -> 1
            2,
            42,
            56,
            72,
            in 26..30,
            in 17..20,
            10,
            11,
            12,
            5,
            6 -> 2
            3,
            in 31..35,
            in 21..24,
            in 13..15,
            7,
            8 -> 4
            else -> index
        }
    }

    private fun getQamFromIndex(index: Int): String {
        return when (index) {
            2,
            5 -> "256qam"
            3,
            6 -> "1024qam"
            else -> "64qam"
        }
    }

    private fun getBWFromIndexV8(index: Int): Int {
        return when (index) {
            0 -> 5
            1,
            2 -> 10
            3 -> 15
            4,
            5 -> 20
            8,
            9 -> 25
            10 -> 30
            11 -> 40
            12,
            13 -> 50
            17 -> 60
            18 -> 70
            19,
            20 -> 80
            in 21..31 -> 100
            else -> index
        }
    }

    private fun getBWFromIndex(index: Int): Int {
        return when (index) {
            4 -> 5
            5 -> 10
            6 -> 15
            7 -> 20
            8 -> 25
            9 -> 30
            10 -> 40
            11,
            15 -> 50
            12 -> 60
            13 -> 80
            14,
            in 20..26 -> 100
            else -> index
        }
    }

    private fun getSourceFromIndex(index: Int): String {
        return when (index) {
            0 -> "RF"
            1 -> "PM"
            3 -> "RF_ENDC"
            4 -> "RF_NRCA"
            5 -> "RF_NRDC"
            else -> index.toString()
        }
    }
}
