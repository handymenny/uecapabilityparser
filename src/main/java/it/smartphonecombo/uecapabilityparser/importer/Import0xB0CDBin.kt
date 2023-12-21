package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.indexOfMin
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.util.IO.echoSafe
import it.smartphonecombo.uecapabilityparser.util.ImportQcHelpers
import it.smartphonecombo.uecapabilityparser.util.WeakConcurrentHashMap
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Import0xB0CDBin : ImportCapabilities {

    private val cacheQamIndex = WeakConcurrentHashMap<Int, Modulation>()

    /**
     * This parser take as [input] a [ByteArray] of a 0xB0CD (binary)
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
    override fun parse(input: ByteArray): Capabilities {
        val capabilities = Capabilities()
        var listCombo = emptyList<ComboLte>()
        val byteBuffer = ByteBuffer.wrap(input)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        try {
            val logSize = ImportQcHelpers.getQcDiagLogSize(byteBuffer, capabilities)
            capabilities.setMetadata("logSize", logSize)
            if (debug) {
                echoSafe("Log file size: $logSize bytes")
            }

            val version = byteBuffer.readUnsignedByte()
            capabilities.setMetadata("version", version)
            if (debug) {
                echoSafe("Version $version\n")
            }

            val numCombos = byteBuffer.readUnsignedByte()
            if (debug) {
                echoSafe("Num Combos $numCombos\n")
            }
            capabilities.setMetadata("numCombos", numCombos)

            if (version < 41) {
                byteBuffer.readUnsignedByte()
            }

            listCombo = mutableListWithCapacity(numCombos)

            for (i in 1..numCombos) {
                val combo = parseCombo(byteBuffer, version)
                listCombo.add(combo)
            }
        } catch (ignored: BufferUnderflowException) {
            // Do nothing
        }

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
    private fun parseCombo(byteBuffer: ByteBuffer, version: Int): ComboLte {
        val numComponents = getNumComponents(byteBuffer, version)
        val bands = mutableListWithCapacity<ComponentLte>(numComponents)
        for (i in 0 until numComponents) {
            val component = parseComponent(byteBuffer, version)
            if (component.band != 0) {
                bands.add(component)
            }
        }

        // Un unknown version < v24 has two bytes of padding after each combo
        if (version < 24) {
            byteBuffer.skipBytes(2)
        }

        bands.sortDescending()

        return ComboLte(bands)
    }

    /** Return the num of components of a combo. */
    private fun getNumComponents(byteBuffer: ByteBuffer, version: Int): Int {
        // V24, V32, V40 have fixed number of components
        return if (version < 41) 6 else byteBuffer.readUnsignedByte()
    }

    /** Parse a component */
    private fun parseComponent(byteBuffer: ByteBuffer, version: Int): ComponentLte {
        val band = byteBuffer.readUnsignedShort()
        val component = ComponentLte(band)

        component.classDL = BwClass.valueOf(byteBuffer.readUnsignedByte())

        if (version >= 41) {
            component.classUL = BwClass.valueOf(byteBuffer.readUnsignedByte())
            component.mimoDL = Mimo.fromQcIndex(byteBuffer.readUnsignedByte())
            component.mimoUL = Mimo.fromQcIndex(byteBuffer.readUnsignedByte())
        } else if (version >= 32) {
            component.mimoDL = Mimo.fromQcIndex(byteBuffer.readUnsignedByte())
            component.classUL = BwClass.valueOf(byteBuffer.readUnsignedByte())
            component.mimoUL = Mimo.fromQcIndex(byteBuffer.readUnsignedByte())
        } else {
            component.classUL = BwClass.valueOf(byteBuffer.readUnsignedByte())
        }

        // V40 and V41 have UL QAM CAP
        if (version >= 40) {
            val modUL = byteBuffer.readUnsignedByte()
            if (component.classUL != BwClass.NONE) {
                component.modUL = getQamFromIndex(modUL)
            }
        }

        return component
    }

    /**
     * Return qam from Qualcomm diag index.
     *
     * The sequence generator is guessed, so it can be wrong or incomplete.
     */
    fun getQamFromIndex(index: Int): Modulation {
        val cachedResult = cacheQamIndex[index]
        if (cachedResult != null) {
            return cachedResult
        }

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
        for (i in 1..index) {
            val indexOfMin = result.indexOfMin()
            when (result[indexOfMin]) {
                ModulationOrder.QAM256 -> result = Array(result.size + 1) { ModulationOrder.QAM64 }
                ModulationOrder.QAM64 -> result[indexOfMin] = ModulationOrder.QAM256
                else -> result[indexOfMin] = ModulationOrder.QAM64
            }
        }

        val resultQam = Modulation.from(result.toList())
        cacheQamIndex[index] = resultQam

        return resultQam
    }
}
