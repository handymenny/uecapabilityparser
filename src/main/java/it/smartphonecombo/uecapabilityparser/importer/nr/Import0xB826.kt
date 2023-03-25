package it.smartphonecombo.uecapabilityparser.importer.nr

import com.soywiz.kmem.extract
import com.soywiz.kmem.extract4
import com.soywiz.kmem.extract8
import com.soywiz.kmem.insert
import com.soywiz.kmem.isOdd
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
        val capabilities = Capabilities()
        val listCombo = ArrayList<ComboNr>()
        val byteArray = input.use(InputStream::readBytes)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        try {
            val logSize = getLogSize(byteBuffer, capabilities)
            capabilities.setMetadata("logSize", logSize)
            if (debug) {
                println("Log file size: $logSize bytes")
            }

            val version = byteBuffer.readUnsignedShort()
            capabilities.setMetadata("version", version)
            if (debug) {
                println("Version $version\n")
            }

            byteBuffer.skipBytes(2)

            val numCombos = getNumCombos(byteBuffer, version, capabilities)
            if (debug) {
                println("Num Combos $numCombos\n")
            }
            capabilities.setMetadata("numCombos", numCombos)

            val source: String? = getSource(version, byteBuffer)
            source?.let {
                capabilities.setMetadata("source", it)
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
                capabilities.enDcCombos = listCombo
            } else if (listCombo.first().isNrDc) {
                capabilities.nrDcCombos = listCombo
            } else {
                capabilities.nrCombos = listCombo
            }
        }
        return capabilities
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

    private fun getNumCombos(
        byteBuffer: ByteBuffer,
        version: Int,
        capabilities: Capabilities
    ): Int {
        // Version < 3 only has the num of combos of this log
        // version > 3 also have the total combos of the series and the index of this specific log
        if (version <= 3) {
            return byteBuffer.readUnsignedShort()
        }

        val totalCombos = byteBuffer.readUnsignedShort()
        capabilities.setMetadata("totalCombos", totalCombos)
        val index = byteBuffer.readUnsignedShort()
        capabilities.setMetadata("index", index)
        if (debug) {
            println("Total Numb Combos $totalCombos\n")
            println("Index $index\n")
        }
        return byteBuffer.readUnsignedShort()
    }

    private fun getLogSize(byteBuffer: ByteBuffer, capabilities: Capabilities): Int {
        // Try to read fileSize from the header
        val fileSize = byteBuffer.readUnsignedShort()

        // if fileSize = bufferSize 0xB826 has a header
        if (fileSize != byteBuffer.limit()) {
            // header missing, logSize is buffer size
            byteBuffer.rewind()
            return byteBuffer.limit()
        }

        val logItem = byteBuffer.readUnsignedShort().toString(16).uppercase()
        capabilities.setMetadata("logItem", "0x$logItem")
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
        val numComponents = getNumComponents(byteBuffer, version)
        val bands = mutableListOf<IComponent>()
        var nrBands = mutableListOf<IComponent>()
        var nrDcBands = mutableListOf<IComponent>()
        when (version) {
            6,
            8 -> byteBuffer.skipBytes(1)
            7 -> byteBuffer.skipBytes(3)
            in 9..13 -> byteBuffer.skipBytes(9)
            14 -> byteBuffer.skipBytes(25)
        }
        for (i in 0 until numComponents) {
            val component = parseComponent(byteBuffer, version)
            if (component is ComponentNr) {
                nrBands.add(component)
            } else {
                bands.add(component)
            }
        }

        /*
         * We assume that 0xb826 without explicit combo type in source
         * don't support NR CA FR1-FR2.
         */
        if (bands.isEmpty() && !source.equals("RF_NRCA")) {
            val (fr2bands, fr1bands) = nrBands.partition { (it as ComponentNr).isFR2 }

            if (fr2bands.isNotEmpty() && fr1bands.isNotEmpty()) {
                nrBands = fr1bands.toMutableList()
                nrDcBands = fr2bands.toMutableList()
            }
        }

        val bandArray = bands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        val nrBandsArray =
            nrBands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        val nrDcBandsArray =
            nrDcBands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
        return if (bandArray.isNotEmpty()) {
            ComboNr(bandArray, nrBandsArray)
        } else if (nrDcBandsArray.isNotEmpty()) {
            ComboNr(nrBandsArray, nrDcBandsArray)
        } else {
            ComboNr(nrBandsArray)
        }
    }

    private fun getNumComponents(byteBuffer: ByteBuffer, version: Int): Int {
        val numBands = byteBuffer.readUnsignedByte()

        val offset =
            if (version < 3) {
                0
            } else if (version <= 7) {
                1
            } else {
                3
            }

        return numBands.extract4(offset)
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
        val byte = byteBuffer.readUnsignedByte()
        val bwClass = byte.extract8(1).toBwClass()
        val isNr = byte.isOdd

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }

        component.classDL = bwClass
        component.mimoDL = getMimoFromIndex(byteBuffer.readUnsignedByte())
        val ulClass = byteBuffer.readUnsignedByte().extract8(1)
        component.classUL = ulClass.toBwClass()
        val mimoUL = byteBuffer.readUnsignedByte()
        component.mimoUL = getMimoFromIndex(mimoUL)
        val modUL = byteBuffer.readUnsignedByte()
        component.modUL = getQamFromIndex(modUL)

        if (isNr) {
            val nrBand = component as ComponentNr
            byteBuffer.skipBytes(1)

            val short = byteBuffer.readUnsignedShort()

            var scsIndex = short.extract4(0)
            if (version < 3) {
                scsIndex += 1
            }

            nrBand.scs = getSCSFromIndex(scsIndex)

            if (version >= 6) {
                val bwIndex = short.extract(6, 5)
                nrBand.maxBandwidth = getBWFromIndex(bwIndex)
            } else {
                val bwIndex = short.extract8(8)
                nrBand.maxBandwidth = bwIndex shl 2
            }
        } else {
            byteBuffer.skipBytes(3)
        }
        return component
    }

    private fun parseComponentV8(byteBuffer: ByteBuffer): IComponent {
        val short = byteBuffer.readUnsignedShort()

        val band = short.extract(0, 9)
        val isNr = short.extract(9)
        val bwClass = short.extract(10, 5).toBwClass()

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }
        component.classDL = bwClass
        val byte = byteBuffer.readUnsignedByte()

        val mimoLeft = byte.extract(0, 6)
        val mimoRight = short.extract(15, 1)
        val mimo = mimoRight.insert(mimoLeft, 1, 6)
        component.mimoDL = getMimoFromIndex(mimo)

        val byte2 = byteBuffer.readUnsignedByte()
        val mimoUL = byte2.extract(3, 7)
        component.mimoUL = getMimoFromIndex(mimoUL)

        val classUlLeft = byte2.extract(0, 3)
        val classUlRight = byte.extract(6, 2)
        val classUl = classUlRight.insert(classUlLeft, 2, 3)
        component.classUL = classUl.toBwClass()

        val byte3 = byteBuffer.readUnsignedByte()
        val modUL = byte3.extract(1, 2)
        component.modUL = getQamFromIndex(modUL)

        if (isNr) {
            val nrBand = component as ComponentNr
            val byte4 = byteBuffer.readUnsignedByte()

            val scsLeft = byte4.extract(0, 2)
            val scsRight = byte3.extract(7, 1)
            val scsIndex = scsRight.insert(scsLeft, 1, 2)
            nrBand.scs = getSCSFromIndex(scsIndex)

            val maxBWindex = byte4.extract(2, 5)
            nrBand.maxBandwidth = getBWFromIndexV8(maxBWindex)
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

    private fun getSCSFromIndex(index: Int): Int {
        return when (index) {
            1 -> 15
            2 -> 30
            3 -> 60
            4 -> 120
            else -> index
        }
    }
}
