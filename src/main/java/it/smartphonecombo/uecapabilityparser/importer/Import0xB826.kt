package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.insert
import it.smartphonecombo.uecapabilityparser.extension.isOdd
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.readNBits
import it.smartphonecombo.uecapabilityparser.extension.readUByte
import it.smartphonecombo.uecapabilityparser.extension.readUShortLE
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.toBandwidth
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
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * A parser for Qualcomm 0xB826 Log Item (NR5G RRC Supported CA Combos).
 *
 * Some BW, mimo and modulation values are guessed, so they can be wrong or incomplete.
 */
object Import0xB826 : ImportCapabilities {

    /**
     * This parser take as [input] a [InputSource] of a 0xB826 (binary)
     *
     * The output is a [Capabilities] with the list of parsed NR CA combos stored in
     * [nrCombos][Capabilities.nrCombos], the list of parsed EN DC combos stored in
     * [enDcCombos][Capabilities.enDcCombos] and the list of parsed NR DC combos stored in
     * [nrDcCombos][Capabilities.nrDcCombos]
     *
     * It supports 0xB826 with or without header.
     *
     * It has been tested with the following 0xB826 versions: 2, 3, 4, 6, 7, 8, 9, 10, 13, 14, 17.
     *
     * If you have a 0xB826 of a different version, please share it with info at smartphonecombo dot
     * it.
     */
    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        var listCombo = emptyList<ICombo>()
        val stream = BufferedInputStream(input.inputStream())

        try {
            val logSize =
                ImportQcHelpers.getQcDiagLogSize(stream, input.size().toInt(), capabilities)
            capabilities.setMetadata("logSize", logSize)
            if (debug) {
                echoSafe("Log file size: $logSize bytes")
            }

            val version = stream.readUShortLE()
            capabilities.setMetadata("version", version)
            if (debug) {
                echoSafe("Version $version\n")
            }

            stream.skipBytes(2)

            val numCombos = getNumCombos(stream, version, capabilities)
            if (debug) {
                echoSafe("Num Combos $numCombos\n")
            }
            capabilities.setMetadata("numCombos", numCombos)

            val source: String? = getSource(version, stream)
            source?.let {
                capabilities.setMetadata("source", it)
                if (debug) {
                    echoSafe("source $it\n")
                }
            }

            listCombo = mutableListWithCapacity(numCombos)

            for (i in 1..numCombos) {
                val combo = parseCombo(stream, version, source)
                listCombo.add(combo)
            }
        } catch (_: IOException) {
            // Do nothing
        }
        stream.close()

        if (debug) {
            echoSafe(
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
        stream: InputStream,
    ): String? {
        if (version <= 3) {
            return null
        }

        // Parse source field
        val sourceIndex = stream.readUByte()
        return getSourceFromIndex(sourceIndex)
    }

    /**
     * Return the num of combos in this log. Also set index and totalCombos in [capabilities] if
     * available.
     */
    private fun getNumCombos(stream: InputStream, version: Int, capabilities: Capabilities): Int {
        // Version < 3 only has the num of combos of this log
        // version > 3 also have the total combos of the series and the index of this specific log
        if (version <= 3) {
            return stream.readUShortLE()
        }

        val totalCombos = stream.readUShortLE()
        capabilities.setMetadata("totalCombos", totalCombos)
        val index = stream.readUShortLE()
        capabilities.setMetadata("index", index)
        if (debug) {
            echoSafe("Total Numb Combos $totalCombos\n")
            echoSafe("Index $index\n")
        }
        return stream.readUShortLE()
    }

    /** Parses a combo */
    private fun parseCombo(
        stream: InputStream,
        version: Int,
        source: String?,
    ): ICombo {
        if (version >= 8) {
            stream.skipBytes(3)
        }
        val numComponents = getNumComponents(stream, version)
        val bands = mutableListWithCapacity<ComponentLte>(numComponents)
        var nrBands = mutableListWithCapacity<ComponentNr>(numComponents)
        var nrDcBands = mutableListWithCapacity<ComponentNr>(numComponents)
        when (version) {
            6,
            8 -> stream.skipBytes(1)
            7 -> stream.skipBytes(3)
            in 9..13 -> stream.skipBytes(9)
            in 14..Int.MAX_VALUE -> stream.skipBytes(25)
        }
        for (i in 0 until numComponents) {
            val component = parseComponent(stream, version)
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
    private fun getNumComponents(stream: InputStream, version: Int): Int {
        val numBands = stream.readUByte()

        val offset =
            if (version < 3) {
                0
            } else if (version <= 7) {
                1
            } else {
                3
            }

        return numBands.readNBits(4, offset)
    }

    /**
     * Parse a component.
     *
     * This just calls [parseComponentV8] if version >= 8 or [parseComponentPreV8] otherwise.
     */
    private fun parseComponent(stream: InputStream, version: Int): IComponent {
        return if (version >= 8) {
            parseComponentV8(stream, version)
        } else {
            parseComponentPreV8(stream, version)
        }
    }

    /** Parse a component. It only supports versions < 8 */
    private fun parseComponentPreV8(stream: InputStream, version: Int): IComponent {
        val band = stream.readUShortLE()
        val byte = stream.readUByte()
        val bwClass = BwClass.valueOf(byte.readNBits(8, offset = 1))
        val isNr = byte.isOdd()

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }

        component.classDL = bwClass
        component.mimoDL = Mimo.fromQcIndex(stream.readUByte())
        val ulClass = stream.readUByte().readNBits(8, offset = 1)
        component.classUL = BwClass.valueOf(ulClass)
        val mimoUL = stream.readUByte()
        component.mimoUL = Mimo.fromQcIndex(mimoUL)
        // Only values 0, 1, 2 have been seen on the wild
        val modUL = stream.readUByte()
        if (component.classUL != BwClass.NONE) {
            component.modUL = getQamFromIndex(modUL, true).toModulation()
        }

        if (isNr) {
            val nrBand = component as ComponentNr
            stream.skipBytes(1)

            val short = stream.readUShortLE()

            var scsIndex = short.readNBits(4)
            if (version < 3) {
                scsIndex += 1
            }

            nrBand.scs = getSCSFromIndex(scsIndex)

            if (version >= 6) {
                val bwIndex = short.readNBits(5, offset = 6)
                nrBand.maxBandwidthDl = getBWFromIndexV6(bwIndex).toBandwidth()

                if (component.classUL != BwClass.NONE) {
                    val bwIndexUl = short.readNBits(5, offset = 11)
                    nrBand.maxBandwidthUl = getBWFromIndexV6(bwIndexUl).toBandwidth()
                }
            } else {
                // v2-v5 stores the max bw as a 10 bit integer
                nrBand.maxBandwidthDl = short.readNBits(10, offset = 6).toBandwidth()

                // version <= 5 don't have a separate field for maxBwUl
                if (component.classUL != BwClass.NONE) {
                    nrBand.maxBandwidthUl = nrBand.maxBandwidthDl
                }
            }
        } else {
            stream.skipBytes(3)
        }
        return component
    }

    /** Parse a component. It supports versions >= 8 */
    private fun parseComponentV8(stream: InputStream, version: Int): IComponent {
        val short = stream.readUShortLE()

        val band = short.readNBits(9)
        val isNr = short.readNBits(1, offset = 9) == 1
        val bwClass = BwClass.valueOf(short.readNBits(5, offset = 10))

        val component =
            if (isNr) {
                ComponentNr(band)
            } else {
                ComponentLte(band)
            }
        component.classDL = bwClass
        val byte = stream.readUByte()

        val mimoLeft = byte.readNBits(6)
        val mimoRight = short.readNBits(1, offset = 15)
        val mimo = mimoRight.insert(mimoLeft, 1)
        component.mimoDL = Mimo.fromQcIndex(mimo)

        val byte2 = stream.readUByte()
        val mimoUL = byte2.readNBits(7, offset = 3)
        component.mimoUL = Mimo.fromQcIndex(mimoUL)

        val classUlLeft = byte2.readNBits(3)
        val classUlRight = byte.readNBits(2, offset = 6)
        val classUl = classUlRight.insert(classUlLeft, 2)
        component.classUL = BwClass.valueOf(classUl)

        val byte3 = stream.readUByte()
        // Only values 0, 1 have been seen on the wild
        val modULSecondBit = byte3.readNBits(1, offset = 1)
        val modULFirstBit = byte3.readNBits(1, offset = 2)
        val modUL = modULFirstBit.insert(modULSecondBit, 1)

        if (component.classUL != BwClass.NONE) {
            // v8 doesn't have 64qam
            component.modUL = getQamFromIndex(modUL, false).toModulation()
        }

        if (isNr) {
            val nrBand = component as ComponentNr
            val byte4 = stream.readUByte()
            val byte5 = stream.readUByte()

            val scsLeft = byte4.readNBits(2)
            val scsRight = byte3.readNBits(1, offset = 7)
            val scsIndex = scsRight.insert(scsLeft, 1)
            nrBand.scs = getSCSFromIndex(scsIndex)

            if (version >= 17) {
                val maxBWindex = byte4.readNBits(6, offset = 2).insert(byte5.readNBits(1), 6)
                nrBand.maxBandwidthDl = getBWFromIndexV17(maxBWindex)
                val maxBwIndexUl = byte5.readNBits(7, offset = 1)
                nrBand.maxBandwidthUl = getBWFromIndexV17(maxBwIndexUl)
            } else if (version >= 10) {
                val maxBWindex = byte4.readNBits(6, offset = 2)
                nrBand.maxBandwidthDl = getBWFromIndexV10(maxBWindex)
                val maxBwIndexUl = byte5.readNBits(6)
                nrBand.maxBandwidthUl = getBWFromIndexV10(maxBwIndexUl)
            } else {
                val maxBWindex = byte4.readNBits(5, offset = 2)
                nrBand.maxBandwidthDl = getBWFromIndexV8(maxBWindex)
                val maxBwIndexUl = byte4.readNBits(1, offset = 7).insert(byte5.readNBits(4), 1)
                nrBand.maxBandwidthUl = getBWFromIndexV8(maxBwIndexUl)
            }

            stream.skipBytes(1)
        } else {
            stream.skipBytes(3)
        }
        if (version >= 17) {
            stream.skipBytes(1)
        }
        return component
    }

    /**
     * Return qam from index.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getQamFromIndex(index: Int, preV8: Boolean): ModulationOrder {
        // only values 0, 1, 2 have been seen on the wild for < v8
        // only values 0, 1 have been seen on the wild for >= v8
        return when (index) {
            1 -> if (preV8) ModulationOrder.QAM64 else ModulationOrder.QAM256
            2 -> ModulationOrder.QAM256
            else -> ModulationOrder.NONE
        }
    }

    /**
     * Return maxBw from index for 0xB826 versions >= 8 and < 10.
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getBWFromIndexV8(index: Int): Bandwidth {
        val mixed =
            when (index) {
                22 -> listOf(100, 60)
                31 -> listOf(60, 40)
                else -> null
            }

        if (mixed != null) {
            return Bandwidth.from(mixed)
        }

        val single =
            when (index) {
                1 -> 5
                2 -> 10
                3 -> 15
                in 4..8 -> 20
                9 -> 25
                10 -> 30
                11,
                30 -> 40
                in 12..16 -> 50
                17 -> 60
                18 -> 70
                19 -> 80
                20 -> 90
                in 21..29 -> 100
                else -> index
            }

        return single.toBandwidth()
    }

    /**
     * Return maxBw from index for 0xB826 versions >= 10.
     *
     * The first 32 values are decoded by [getBWFromIndexV8]
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getBWFromIndexV10(index: Int): Bandwidth {
        if (index < 32) {
            // V8 Decodes the range 0-31
            return getBWFromIndexV8(index)
        }

        val mixed =
            when (index) {
                32 -> listOf(100, 40)
                39 -> listOf(40, 10)
                40 -> listOf(40, 20)
                42 -> listOf(30, 20)
                46 -> listOf(50, 5)
                47 -> listOf(50, 10)
                48 -> listOf(50, 15)
                49 -> listOf(50, 20)
                50 -> listOf(40, 15)
                52 -> listOf(30, 25)
                53 -> listOf(20, 10)
                54 -> listOf(20, 15)
                57 -> listOf(80, 20)
                58 -> listOf(40, 30)
                59 -> listOf(100, 90)
                60 -> listOf(30, 10)
                61 -> listOf(100, 20)
                62 -> listOf(80, 40)
                63 -> listOf(50, 40)
                else -> null
            }

        if (mixed != null) {
            return Bandwidth.from(mixed)
        }

        val single =
            when (index) {
                in 33..36 -> 200
                37 -> 10
                38 -> 25
                41 -> 35
                43 -> 60
                44 -> 30
                45 -> 45
                51 -> 15
                55 -> 5
                56 -> 80
                else -> index
            }

        return single.toBandwidth()
    }

    /**
     * Return maxBw from index for 0xB826 versions >= 17.
     *
     * The first 64 values are decoded by [getBWFromIndexV10]
     *
     * Some values are guessed, so they can be wrong or incomplete.
     */
    private fun getBWFromIndexV17(index: Int): Bandwidth {
        if (index < 64) {
            // V10 Decodes the range 0-63
            return getBWFromIndexV10(index)
        }

        val mixed =
            when (index) {
                64 -> listOf(100, 50)
                65 -> listOf(100, 80)
                else -> {
                    echoSafe("Warning: 0xB826 BW index is above 65: $index", err = true)
                    emptyList()
                }
            }

        return Bandwidth.from(mixed)
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
