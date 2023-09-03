package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.modulation.toModulation
import it.smartphonecombo.uecapabilityparser.util.ImportQcHelpers
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import korlibs.memory.extract
import korlibs.memory.extract1
import korlibs.memory.extract10
import korlibs.memory.extract2
import korlibs.memory.extract3
import korlibs.memory.extract4
import korlibs.memory.extract5
import korlibs.memory.extract6
import korlibs.memory.extract7
import korlibs.memory.extract8
import korlibs.memory.finsert
import korlibs.memory.isOdd

/**
 * A parser for Qualcomm 0xB826 Log Item (NR5G RRC Supported CA Combos).
 *
 * Some BW, mimo and modulation values are guessed, so they can be wrong or incomplete.
 */
object Import0xB826 : ImportCapabilities {

    /**
     * This parser take as [input] a [ByteArray] of a 0xB826 (binary)
     *
     * The output is a [Capabilities] with the list of parsed NR CA combos stored in
     * [nrCombos][Capabilities.nrCombos], the list of parsed EN DC combos stored in
     * [enDcCombos][Capabilities.enDcCombos] and the list of parsed NR DC combos stored in
     * [nrDcCombos][Capabilities.nrDcCombos]
     *
     * It supports 0xB826 with or without header.
     *
     * It has been tested with the following 0xB826 versions: 2, 3, 4, 6, 7, 8, 9, 10, 13, 14.
     *
     * If you have a 0xB826 of a different version, please share it with info at smartphonecombo dot
     * it.
     */
    override fun parse(input: ByteArray): Capabilities {
        val capabilities = Capabilities()
        var listCombo = emptyList<ICombo>()
        val byteBuffer = ByteBuffer.wrap(input)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        try {
            val logSize = ImportQcHelpers.getQcDiagLogSize(byteBuffer, capabilities)
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

            listCombo = mutableListWithCapacity(numCombos)

            for (i in 1..numCombos) {
                val combo = parseCombo(byteBuffer, version, source)
                listCombo.add(combo)
            }
        } catch (ignored: BufferUnderflowException) {
            // Do nothing
        }

        if (debug) {
            println(
                listCombo.joinToString(
                    prefix = "[",
                    postfix = "]",
                    transform = ICombo::toCompactStr
                )
            )
        }

        if (listCombo.isNotEmpty()) {
            if (listCombo.first() is ComboEnDc) {
                capabilities.enDcCombos = listCombo.typedList()
            } else if (listCombo.first() is ComboNrDc) {
                capabilities.nrDcCombos = listCombo.typedList()
            } else {
                capabilities.nrCombos = listCombo.typedList()
            }
        }
        return capabilities
    }

    /**
     * Returns the source of the combos list.
     *
     * It can be "RF", "PM", "RF_ENDC", "RF_NRCA" or "RF_NRDC".
     *
     * Supported for 0xB826 v4 and above.
     */
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

    /**
     * Return the num of combos in this log. Also set index and totalCombos in [capabilities] if
     * available.
     */
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

    /** Parses a combo */
    private fun parseCombo(
        byteBuffer: ByteBuffer,
        version: Int,
        source: String?,
    ): ICombo {
        if (version >= 8) {
            byteBuffer.skipBytes(3)
        }
        val numComponents = getNumComponents(byteBuffer, version)
        val bands = mutableListWithCapacity<ComponentLte>(numComponents)
        var nrBands = mutableListWithCapacity<ComponentNr>(numComponents)
        var nrDcBands = mutableListWithCapacity<ComponentNr>(numComponents)
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
                bands.add(component as ComponentLte)
            }
        }

        /*
         * We assume that 0xb826 without explicit combo type in source don't support NR CA FR1-FR2.
         */
        if (bands.isEmpty() && !source.equals("RF_NRCA")) {
            val (fr2bands, fr1bands) = nrBands.partition { it.isFR2 }

            if (fr2bands.isNotEmpty() && fr1bands.isNotEmpty()) {
                nrBands = fr1bands.toMutableList()
                nrDcBands = fr2bands.toMutableList()
            }
        }

        bands.sortDescending()

        nrBands.sortDescending()

        nrDcBands.sortDescending()

        return if (bands.isNotEmpty()) {
            ComboEnDc(bands, nrBands)
        } else if (nrDcBands.isNotEmpty()) {
            ComboNrDc(nrBands, nrDcBands)
        } else {
            ComboNr(nrBands)
        }
    }

    /** Return the num of components of a combo. */
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

    /**
     * Parse a component.
     *
     * This just calls [parseComponentV8] if version >= 8 or [parseComponentPreV8] otherwise.
     */
    private fun parseComponent(byteBuffer: ByteBuffer, version: Int): IComponent {
        return if (version >= 8) {
            parseComponentV8(byteBuffer, version)
        } else {
            parseComponentPreV8(byteBuffer, version)
        }
    }

    /** Parse a component. It only supports versions < 8 */
    private fun parseComponentPreV8(byteBuffer: ByteBuffer, version: Int): IComponent {
        val band = byteBuffer.readUnsignedShort()
        val byte = byteBuffer.readUnsignedByte()
        val bwClass = BwClass.valueOf(byte.extract8(1))
        val isNr = byte.isOdd

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }

        component.classDL = bwClass
        component.mimoDL = Mimo.fromQcIndex(byteBuffer.readUnsignedByte())
        val ulClass = byteBuffer.readUnsignedByte().extract8(1)
        component.classUL = BwClass.valueOf(ulClass)
        val mimoUL = byteBuffer.readUnsignedByte()
        component.mimoUL = Mimo.fromQcIndex(mimoUL)
        val modUL = byteBuffer.readUnsignedByte()
        if (component.classUL != BwClass.NONE) {
            component.modUL = getQamFromIndex(modUL).toModulation()
        }

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
                val bwIndex = short.extract5(6)
                nrBand.maxBandwidth = getBWFromIndexV6(bwIndex)
            } else {
                // v2-v5 stores the max bw as a 10 bit integer
                nrBand.maxBandwidth = short.extract10(6)
            }
        } else {
            byteBuffer.skipBytes(3)
        }
        return component
    }

    /** Parse a component. It supports versions >= 8 */
    private fun parseComponentV8(byteBuffer: ByteBuffer, version: Int): IComponent {
        val short = byteBuffer.readUnsignedShort()

        val band = short.extract(0, 9)
        val isNr = short.extract(9)
        val bwClass = BwClass.valueOf(short.extract5(10))

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }
        component.classDL = bwClass
        val byte = byteBuffer.readUnsignedByte()

        val mimoLeft = byte.extract6(0)
        val mimoRight = short.extract1(15)
        val mimo = mimoRight.finsert(mimoLeft, 1)
        component.mimoDL = Mimo.fromQcIndex(mimo)

        val byte2 = byteBuffer.readUnsignedByte()
        val mimoUL = byte2.extract7(3)
        component.mimoUL = Mimo.fromQcIndex(mimoUL)

        val classUlLeft = byte2.extract3(0)
        val classUlRight = byte.extract2(6)
        val classUl = classUlRight.finsert(classUlLeft, 2)
        component.classUL = BwClass.valueOf(classUl)

        val byte3 = byteBuffer.readUnsignedByte()
        val modUL = byte3.extract2(1)
        if (component.classUL != BwClass.NONE) {
            component.modUL = getQamFromIndex(modUL).toModulation()
        }

        if (isNr) {
            val nrBand = component as ComponentNr
            val byte4 = byteBuffer.readUnsignedByte()

            val scsLeft = byte4.extract2(0)
            val scsRight = byte3.extract1(7)
            val scsIndex = scsRight.finsert(scsLeft, 1)
            nrBand.scs = getSCSFromIndex(scsIndex)

            val maxBWindex = if (version >= 10) byte4.extract6(2) else byte4.extract5(2)
            nrBand.maxBandwidth = getBWFromIndexV8(maxBWindex)
            byteBuffer.skipBytes(2)
        } else {
            byteBuffer.skipBytes(3)
        }
        return component
    }

    /**
     * Return qam from index.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getQamFromIndex(index: Int): ModulationOrder {
        return when (index) {
            2,
            5 -> ModulationOrder.QAM256
            3,
            6 -> ModulationOrder.QAM1024
            else -> ModulationOrder.QAM64
        }
    }

    /**
     * Return maxBw from index for 0xB826 versions >= 8.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getBWFromIndexV8(index: Int): Int {
        return when (index) {
            1 -> 5
            2 -> 10
            3 -> 15
            in 4..8 -> 20
            9 -> 25
            10 -> 30
            11,
            30 -> 40
            in 12..16 -> 50
            17,
            31 -> 60
            18 -> 70
            19 -> 80
            20 -> 90
            in 21..29 -> 100
            else -> index
        }
    }

    /**
     * Return maxBw from index for 0xB826 versions 6 and 7.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getBWFromIndexV6(index: Int): Int {
        return when (index) {
            5 -> 5
            6 -> 15
            in 1..4,
            7 -> 20
            8 -> 25
            9 -> 30
            10 -> 40
            11,
            in 15..18 -> 50
            12 -> 60
            13 -> 80
            14,
            in 19..26 -> 100
            else -> index
        }
    }

    /**
     * Return the combo source from index.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
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

    /**
     * Return max SCS from index.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
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
