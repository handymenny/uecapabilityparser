package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.indexOfMin
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.readUByte
import it.smartphonecombo.uecapabilityparser.extension.readUShortLE
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.util.ImportQcHelpers
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

object Import0xB0CDBin : ImportCapabilities {

    /**
     * This parser take as [input] a [InputSource] of a 0xB0CD (binary)
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It supports 0xB0CD with or without header.
     *
     * It has been tested with the following 0xB0CD versions: 24, 32, 40, 41.
     *
     * If you have a 0xB0CD of a different version, please share it with info at smartphonecombo dot
     * it.
     */
    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        var listCombo = emptyList<ComboLte>()
        val stream = BufferedInputStream(input.inputStream())

        try {
            val logSize =
                ImportQcHelpers.getQcDiagLogSize(stream, input.size().toInt(), capabilities)
            capabilities.setMetadata("logSize", logSize)
            if (debug) {
                echoSafe("Log file size: $logSize bytes")
            }

            val version = stream.readUByte()
            capabilities.setMetadata("version", version)
            if (debug) {
                echoSafe("Version $version\n")
            }

            val numCombos = stream.readUByte()
            if (debug) {
                echoSafe("Num Combos $numCombos\n")
            }
            capabilities.setMetadata("numCombos", numCombos)

            if (version < 41) {
                stream.readUByte()
            }

            listCombo = mutableListWithCapacity(numCombos)

            repeat(numCombos) {
                val combo = parseCombo(stream, version)
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

        capabilities.lteCombos = listCombo

        return capabilities
    }

    /** Parses a combo */
    private fun parseCombo(stream: InputStream, version: Int): ComboLte {
        val numComponents = getNumComponents(stream, version)
        val bands = mutableListWithCapacity<ComponentLte>(numComponents)
        repeat(numComponents) {
            val component = parseComponent(stream, version)
            if (component.band != 0) {
                bands.add(component)
            }
        }

        // Unknown version < v24 has two bytes of padding after each combo
        if (version < 24) {
            stream.skipBytes(2)
        }

        bands.sortDescending()

        return ComboLte(bands)
    }

    /** Return the num of components of a combo. */
    private fun getNumComponents(stream: InputStream, version: Int): Int {
        // V24, V32, V40 have fixed number of components
        return if (version < 41) 6 else stream.readUByte()
    }

    /** Parse a component */
    private fun parseComponent(stream: InputStream, version: Int): ComponentLte {
        val band = stream.readUShortLE()
        val component = ComponentLte(band)

        component.classDL = BwClass.valueOf(stream.readUByte())

        if (version >= 41) {
            component.classUL = BwClass.valueOf(stream.readUByte())
            component.mimoDL = parseMimo(stream.readUByte(), true)
            component.mimoUL = parseMimo(stream.readUByte(), true)
        } else if (version >= 32) {
            // versions < 40 don't have an indexed mimo
            val mimoIsIndexed = version >= 40

            component.mimoDL = parseMimo(stream.readUByte(), mimoIsIndexed)
            component.classUL = BwClass.valueOf(stream.readUByte())
            component.mimoUL = parseMimo(stream.readUByte(), mimoIsIndexed)
        } else {
            component.classUL = BwClass.valueOf(stream.readUByte())
        }

        // V40 and V41 have UL QAM CAP
        if (version >= 40) {
            val modUL = stream.readUByte()
            if (component.classUL != BwClass.NONE) {
                component.modUL = getQamFromIndex(modUL)
            }
        }

        return component
    }

    /**
     * Convert the given "raw" value to mimo. If indexed is true (version >= 40) calls
     * [Mimo.fromQcIndex], otherwise calls [Mimo.from]
     */
    private fun parseMimo(value: Int, indexed: Boolean): Mimo {
        return if (indexed) Mimo.fromQcIndex(value) else Mimo.from(value)
    }

    /**
     * Return qam from Qualcomm diag index.
     *
     * The sequence generator is guessed, so it can be wrong or incomplete.
     */
    private fun getQamFromIndex(index: Int): Modulation {
        /*
            Some examples:
            0 -> INVALID
            1 -> 64_QAM
            2 -> 256_QAM
            3 -> 64_64_QAM
            4 -> 256_64_QAM
            5 -> 256_256_QAM
            6 -> 64_64_64_QAM
            7 -> 256_64_64_QAM
            8 -> 256_256_64_QAM
            9 -> 256_256_256_QAM
            ...
        */
        var result = arrayOf(ModulationOrder.NONE)
        repeat(index) {
            val indexOfMin = result.indexOfMin()
            when (result[indexOfMin]) {
                ModulationOrder.QAM256 -> result = Array(result.size + 1) { ModulationOrder.QAM64 }
                ModulationOrder.QAM64 -> result[indexOfMin] = ModulationOrder.QAM256
                else -> result[indexOfMin] = ModulationOrder.QAM64
            }
        }

        val resultQam = Modulation.from(result.toList())

        return resultQam
    }
}
