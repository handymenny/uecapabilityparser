package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.firstOrNull
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.toMimo

/** A parser for LTE Combinations as reported by Qct Modem Capabilities */
object ImportQctModemCap : ImportCapabilities {

    /**
     * This parser take as [input] a [InputSource] containing LTE Combinations as reported by Qct
     * Modem Capabilities.
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It can parse multiple messages in the same input.
     */
    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        val listCombo = mutableListOf<ComboLte>()

        input.useLines { seq ->
            try {
                val lines = seq.iterator()
                while (lines.hasNext()) {
                    val source = getValue(lines, "Source")
                    val type = getValue(lines, "Type")
                    val numCombos = getValue(lines, "Combos")?.toIntOrNull() ?: 0
                    val combosHeader = lines.firstOrNull { it.contains("""^\s+#\s+""".toRegex()) }

                    if (combosHeader == null) {
                        continue
                    }

                    val sourceStr = "${source}-${type}".uppercase()
                    capabilities.addMetadata("source", sourceStr)
                    capabilities.addMetadata("numCombos", numCombos)

                    val indexDl = combosHeader.indexOf("DL Bands", ignoreCase = true)
                    val indexUl = combosHeader.indexOf("UL Bands", ignoreCase = true)

                    repeat(numCombos) {
                        val combo = parseCombo(lines.next(), indexDl, indexUl)
                        listCombo.add(combo)
                    }
                }
            } catch (ignored: NoSuchElementException) {
                // Do nothing
            }
        }

        capabilities.lteCombos = listCombo

        return capabilities
    }

    /**
     * Converts the given comboString to a [ComboLte].
     *
     * Returns null if parsing fails
     */
    private fun parseCombo(comboString: String, indexDl: Int, indexUl: Int): ComboLte {
        val dlComponents = parseComponents(comboString.substring(indexDl, indexUl), true)

        val ulComponents = parseComponents(comboString.substring(indexUl), false)

        return ComboLte(dlComponents, ulComponents)
    }

    /**
     * Converts the given componentsString to a List of [ComponentLte].
     *
     * Returns null if parsing fails.
     */
    private fun parseComponents(componentsString: String, isDl: Boolean): List<ComponentLte> {
        val components = mutableListWithCapacity<ComponentLte>(6)
        for (componentStr in componentsString.split('-', ' ')) {
            val component = parseComponent(componentStr, isDl)
            if (component != null) {
                components.add(component)
            }
        }
        return components
    }

    /**
     * Regex used to extract the various parts of a component.
     *
     * Mixed mimo is represented with the highest value as normal digit and the others as subscript
     * separated by space (MMSP).
     *
     * Example: 40D4 ₄ ₂
     *
     * Note: in old versions bwClass was lowercase.
     */
    private val componentRegex = """(\d{1,3})([A-Fa-f])([124]?(:?\p{Zs}[₁₂₄]){0,4})""".toRegex()

    /**
     * Converts the given componentString to a [ComponentLte].
     *
     * Returns null if parsing fails.
     */
    private fun parseComponent(componentString: String, isDl: Boolean): ComponentLte? {
        val result = componentRegex.find(componentString) ?: return null

        val (_, bandRegex, bwClassRegex, mimoRegex) = result.groupValues

        val baseBand = bandRegex.toInt()
        val bwClass = BwClass.valueOf(bwClassRegex)
        val mimoStr = mimoRegex.subscriptToDigit().filterNot(Char::isWhitespace)
        val mimo = mimoStr.toIntOrNull()?.toMimo() ?: EmptyMimo

        return if (isDl) {
            ComponentLte(baseBand, classDL = bwClass, mimoDL = mimo)
        } else {
            ComponentLte(baseBand, classUL = bwClass, mimoUL = mimo)
        }
    }

    /**
     * Search for the first string beginning with given key. Then extract the value. This works for
     * strings like "key : value".
     */
    private fun getValue(iterator: Iterator<String>, key: String): String? {
        val string = iterator.firstOrNull { it.startsWith(key, true) } ?: return null
        return string.split(":").last().trim()
    }

    /** Converts all the subscript in the given string to digit */
    private fun String.subscriptToDigit(): String {
        val listChar = map { char ->
            if (char in '₀'..'₉') {
                char - '₀'.code + '0'.code
            } else {
                char
            }
        }
        return String(listChar.toCharArray())
    }
}
