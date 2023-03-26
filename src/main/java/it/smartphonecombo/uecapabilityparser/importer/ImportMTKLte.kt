package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.IComponent
import it.smartphonecombo.uecapabilityparser.model.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.model.lte.ComponentLte
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.util.NoSuchElementException

/**
 * A parser for *MSG_ID_ERRC_RCM_UE_PRE_CA_COMB_INFO* and *MSG_ID_ERRC_RCM_UE_CA_COMB_INFO*.
 *
 * *UE_PRE_CA_COMB_INFO* contains the LTE combos supported by a MTK device before any filtering.
 * While *UE_CA_COMB_INFO* contains the LTE combos supported after filtering (carrier policy/ue cap
 * enquiry).
 */
object ImportMTKLte : ImportCapabilities {

    /**
     * This parser take as [input] an [InputStream] containing the ELT text representation of the
     * *MSG_ID_ERRC_RCM_UE_PRE_CA_COMB_INFO* and *MSG_ID_ERRC_RCM_UE_CA_COMB_INFO*.
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It can parse multiple messages in the same input.
     */
    override fun parse(input: InputStream): Capabilities {
        val listCombos: MutableList<ComboLte> = ArrayList()
        val iterator = input.reader().use(InputStreamReader::readLines).map(String::trim).iterator()
        try {
            while (iterator.firstOrNull { it.startsWith("band_comb[") } != null) {
                val bands = parseCombo(iterator) ?: continue

                val bandArray = bands.toTypedArray<IComponent>()
                bandArray.sortWith(IComponent.defaultComparator.reversed())

                listCombos.add(ComboLte(bandArray))
            }
        } catch (ignored: NoSuchElementException) {
            // Do nothing
        }
        return Capabilities(listCombos)
    }

    /**
     * Parse a single combo.
     *
     * Return the list of lte Components found.
     *
     * Return null if parsing fails or if there is no component.
     */
    @Throws(NoSuchElementException::class)
    private fun parseCombo(input: Iterator<String>): List<ComponentLte>? {
        val numCCs = extractInt(input.next())

        // Check if combo contains any CCs
        if (numCCs < 1) {
            return null
        }

        val arrayLength = extractArraySize(input.next())
        val bands = parseComponents(minOf(numCCs, arrayLength), input)

        val line = input.firstOrNull { it.startsWith("band_mimo = Array") } ?: return null
        val mimoArrayLength = extractArraySize(line)

        parseMimo(minOf(numCCs, mimoArrayLength), input, bands)
        return bands
    }

    /** Extract MIMO information and update [bands] accordingly. */
    @Throws(NoSuchElementException::class)
    private fun parseMimo(numCCs: Int, input: Iterator<String>, bands: List<ComponentLte>) {
        for (i in 0 until numCCs) {
            input.firstOrNull { it.startsWith("band_mimo[$i]") } ?: break
            val line = extractValue(input.next()).split(" ").first()
            if (line == "ERRC_CAPA_CA_MIMO_CAPA_FOUR_LAYERS") {
                bands[i].mimoDL = 4
            } else if (line == "ERRC_CAPA_CA_MIMO_CAPA_TWO_LAYERS") {
                bands[i].mimoDL = 2
            }
        }
    }

    /** Parse [numCCs] components. */
    @Throws(NoSuchElementException::class)
    private fun parseComponents(numCCs: Int, input: Iterator<String>): List<ComponentLte> {
        val bands = mutableListOf<ComponentLte>()
        for (i in 0 until numCCs) {
            input.firstOrNull { it.startsWith("band_param[$i]") }
            val baseBand = extractInt(input.next())
            val classUL = BwClass.valueOfMtkIndex(extractInt(input.next()))
            val classDL = BwClass.valueOfMtkIndex(extractInt(input.next()))
            val band = ComponentLte(baseBand, classDL, classUL, 0, null, null)
            bands.add(band)
        }
        return bands
    }

    /** Extract the field value from the given line */
    private fun extractValue(line: String): String {
        return line.split("=").last().trim()
    }

    private val arrayRegex = """Array\[(\d+)]""".toRegex()
    /** Get the size of the array from the given line */
    private fun extractArraySize(line: String): Int {
        return arrayRegex.find(line)?.groupValues?.get(1)?.toInt() ?: 0
    }

    /** Extract the field value from the given line and converts it to int */
    @Throws(NumberFormatException::class)
    private fun extractInt(line: String): Int {
        return Integer.decode(extractValue(line))
    }

    /**
     * Return the first element matching the given predicate or null if not found.
     *
     * NB: This function will update iterator cursor.
     */
    private inline fun Iterator<String>.firstOrNull(predicate: (String) -> Boolean): String? {
        for (item in this) {
            if (predicate(item)) {
                return item
            }
        }
        return null
    }
}
