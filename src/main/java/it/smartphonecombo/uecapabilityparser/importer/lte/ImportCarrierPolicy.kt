package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.io.InputStreamReader

object ImportCarrierPolicy : ImportCapabilities {

    override fun parse(input: InputStream): Capabilities {
        val caBandCombosString = input.reader().use(InputStreamReader::readText)

        val listCombo =
            caBandCombosString
                .split(';', '"')
                .filter(String::isNotBlank)
                .mapNotNull(this::parseCombo)
                .toList()

        return Capabilities(listCombo)
    }

    /** Converts the given comboString to a [ComboLte] Returns null if parsing fails */
    private fun parseCombo(comboString: String): ComboLte? {
        val components = comboString.split('-')
        val parsedComponents = mutableListOf<IComponent>()

        for (i in 0 until components.size - 1) {
            val componentString = components[i].filterNot(Char::isWhitespace)
            val component = parseComponent(componentString)
            if (component != null) {
                parsedComponents.add(component)
            }
        }

        if (parsedComponents.isEmpty()) {
            return null
        }

        val bcsString = components.last().filterNot(Char::isWhitespace)
        val bcsArray =
            try {
                parseBcs(bcsString)
            } catch (ignored: NumberFormatException) {
                return null
            }

        val componentsArray =
            parsedComponents.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()

        return ComboLte(componentsArray, bcsArray)
    }

    // Regex used to extract the various parts of a component
    private val componentRegex = """(\d{1,3})([A-F])([24]{0,5})([A-F]?)""".toRegex()

    /** Converts the given componentString to a [ComponentLte] Returns null if parsing fails */
    private fun parseComponent(componentString: String): ComponentLte? {
        val result = componentRegex.find(componentString) ?: return null

        val (_, baseBand, classDL, mimoDL, classUL) = result.groupValues

        return ComponentLte(
            baseBand.toInt(),
            classDL.first(),
            classUL.firstOrNull() ?: '0',
            mimoDL.toIntOrNull() ?: 0,
            null,
            null
        )
    }

    /**
     * Converts the given bcsString to an array of BCSs.
     * - If bcsString is empty or "mAll" returns an empty IntArray
     * - If bcsString starts with m it parses it as Qualcomm MultiBcs
     * - otherwise it returns an IntArray containing the integer extracted from bcsString
     */
    @Throws(NumberFormatException::class)
    private fun parseBcs(bcsString: String): IntArray {
        return if (bcsString == "mAll" || bcsString.isEmpty()) {
            IntArray(0)
        } else if (bcsString.startsWith('m')) {
            val bcs = bcsString.substring(1).toInt(16)
            Utility.qcomBcsToArray(bcs)
        } else {
            intArrayOf(bcsString.toInt())
        }
    }
}
