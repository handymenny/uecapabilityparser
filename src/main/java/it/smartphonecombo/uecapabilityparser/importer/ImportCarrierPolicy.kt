package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import java.io.InputStream
import java.io.InputStreamReader

/** A parser for LTE Combinations as found in Qualcomm Carrier Policies */
object ImportCarrierPolicy : ImportCapabilities {

    /**
     * This parser take as [input] an [InputStream] containing LTE Combinations using the same
     * format used in Qualcomm Carrier Policies.
     *
     * In this format each combination is separated from the other by a ";".
     *
     * This combination consists of carrier components separated by "-", at the end of which there's
     * a string representing the supported BCSs (prefixed with "-"). The combination can be prefixed
     * with "e-"
     *
     * Each carrier component is composed of the number representing the LTE band, followed by a
     * character representing the downlink bandwidth class, followed by a number representing the
     * number of RX antennas, followed by a character representing the uplink bandwidth class.
     *
     * The number of RX antennas and the uplink bandwidth class are optional.
     *
     * The BCS can consist of:
     * - an integer representing a single supported bcs
     * - an integer prefixed with "m" representing the set of supported BCSs (it's the summation of
     *   2 elevated to the bcs for all supported bcs)
     * - the string "mAll" indicating that all BCSs are supported
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     */
    override fun parse(input: InputStream): Capabilities {
        val caBandCombosString = input.reader().use(InputStreamReader::readText)

        val listCombo =
            caBandCombosString
                .split(';', '"')
                .filter(String::isNotBlank)
                .mapNotNull(this::parseCombo)

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
