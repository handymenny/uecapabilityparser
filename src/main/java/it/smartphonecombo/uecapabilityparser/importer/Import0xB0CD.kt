package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Modulation
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import java.io.InputStream
import java.io.InputStreamReader

/** A parser for 0xB0CD LTE RRC Supported CA Combos * */
object Import0xB0CD : ImportCapabilities {

    /**
     * This parser take as [input] an [InputStream] containing the QCAT text representation of the
     * 0xB0CD (LTE RRC Supported CA Combos) log item.
     *
     * The output is a [Capabilities] with the list of parsed LTE combos stored in
     * [lteCombos][Capabilities.lteCombos].
     *
     * It can parse multiple 0xB0CD in the same input.
     *
     * It supports 0xB0CD version 32, 40 and 41.
     */
    override fun parse(input: InputStream): Capabilities {
        val listCombo = mutableListOf<ComboLte>()
        val lines = input.reader().use(InputStreamReader::readLines).iterator()

        while (lines.hasNext()) {
            val headers = getHeaders(lines)
            val version = getVersionFromHeaders(headers)

            if (version == -1) {
                break
            }

            while (lines.hasNext()) {
                val combo = processCombo(lines, version) ?: break
                listCombo.add(combo)
            }
        }

        return Capabilities(listCombo)
    }

    /**
     * Extracts the headers of a 0xB0CD. Headers may occupy one or two lines, are preceded and
     * followed by a line containing only "-"
     */
    private fun getHeaders(lines: Iterator<String>): List<String> {
        var headerStart = false
        val headers = mutableListOf<List<String>>()
        var result: List<String> = emptyList()

        while (lines.hasNext()) {
            val line = lines.next()
            if (line.isHeaderSeparator()) {
                if (headerStart) {
                    break
                } else {
                    headerStart = true
                }
            } else if (headerStart) {
                headers.add(split0xB0CDFields(line))
            }
        }

        if (headers.size == 1) {
            // V40, v41 = one row for headers
            result = headers.first()
        } else if (headers.size == 2) {
            // V32 = two rows for headers
            result = headers.first().zip(headers.last()) { a, b -> "$a $b".trim() }
        }
        return result
    }

    /** Detect 0xB0CD version corresponding to the given headers */
    private fun getVersionFromHeaders(headers: List<String>): Int {
        if (headers.lastOrNull() == "UL Max Antennas") {
            return 32
        } else if (headers.getOrNull(4) == "DL Max Antennas Index") {
            return 40
        } else if (headers.getOrNull(1) == "Num_Band") {
            return 41
        }
        return -1
    }

    /** This method parses a single combination (combo) * */
    private fun processCombo(lines: Iterator<String>, version: Int): ComboLte? {
        val components =
            when (version) {
                32 -> processCombo32(lines)
                40 -> processCombo40(lines)
                41 -> processCombo41(lines)
                else -> null
            }

        if (components.isNullOrEmpty()) {
            return null
        }

        components.sortDescending()
        return ComboLte(components)
    }

    /** This method parses a single combination (combo) from 0xB0CD v41 * */
    private fun processCombo41(lines: Iterator<String>): MutableList<ComponentLte> {
        // Num of bands is dynamic
        var numBands = 1
        var index = 0
        val bands = mutableListOf<ComponentLte>()

        while (lines.hasNext() && index < numBands) {
            val values = split0xB0CDFields(lines.next())
            if (values.size != 9 || values[2].toInt() != index) {
                break
            }
            if (index == 0) {
                numBands = values[1].toInt()
            }
            val baseBand = values[3].toInt()
            val dlClass = extractBwClass(values[4])
            val ulClass = extractBwClass(values[5])
            val dlMimo = extractMimo(values[6])
            // LTE UL Mimo is ignored at the moment
            extractMimo(values[7])
            val ulMod = Modulation.of(values[8])
            bands.add(ComponentLte(baseBand, dlClass, ulClass, dlMimo, modUL = ulMod))
            index++
        }

        return bands
    }

    /** This method parses a single combination (combo) from 0xB0CD v40 * */
    private fun processCombo40(lines: Iterator<String>): MutableList<ComponentLte> {
        // Num of bands is fixed
        val numBands = 5
        var index = 0
        val bands = mutableListOf<ComponentLte>()

        while (lines.hasNext() && index < numBands) {
            val values = split0xB0CDFields(lines.next())
            if (values.size != 8) {
                break
            }
            index = values[1].toInt()
            val baseBand = values[2].toIntOrNull() ?: continue
            val dlClass = extractBwClass(values[3])
            val dlMimo = extractMimo(values[4])
            val ulClass = extractBwClass(values[5])
            // LTE UL Mimo is ignored at the moment
            extractMimo(values[6])
            val ulMod = Modulation.of(values[7])
            bands.add(ComponentLte(baseBand, dlClass, ulClass, dlMimo, modUL = ulMod))
        }

        return bands
    }

    /** This method parses a single combination (combo) from 0xB0CD v32 * */
    private fun processCombo32(lines: Iterator<String>): MutableList<ComponentLte> {
        var index = 0
        val bands = mutableListOf<ComponentLte>()
        // Num of bands is fixed
        val numBands = 5
        while (lines.hasNext() && index < numBands) {
            val values = split0xB0CDFields(lines.next())
            if (values.size != 7) {
                break
            }
            index = values[1].toInt()
            val baseBand = values[2].toIntOrNull() ?: continue
            val dlClass = BwClass.valueOf(values[3].toInt())
            val dlMimo = values[4].toInt()
            val ulClass = BwClass.valueOf(values[5].toInt())
            // LTE UL Mimo is ignored at the moment
            values[6]
            bands.add(ComponentLte(baseBand, dlClass, ulClass, dlMimo))
        }

        return bands
    }

    /**
     * Converts the given string to the corresponding BW Class It's applicable to 0xB0CD v40 and
     * v41.
     */
    private fun extractBwClass(value: String): BwClass {
        if (value.isEmpty() || value.endsWith("NONE")) {
            return BwClass.NONE
        }
        return BwClass(value.last())
    }

    /**
     * Converts the given ANTENNA_INDEX string to the corresponding number of antennas. It's
     * applicable to 0xB0CD v40 and v41.
     */
    private fun extractMimo(value: String): Int {
        if (value.isEmpty() || value.endsWith("INVALID_INDEX")) {
            return 0
        }
        return value.split('_').drop(2).dropLast(1).firstNotNullOf { it.toIntOrNull() }
    }

    /**
     * Returns a [List] containing the values (trimmed) extracted from this [row].
     *
     * Each value in the row should be separated by a "|," with an additional "|" at the beginning
     * and end of the row.
     */
    private fun split0xB0CDFields(row: String): List<String> {
        return row.split('|').drop(1).dropLast(1).map(String::trim)
    }

    /**
     * Return true if this string is a header separator. A header separator is a non-blank line
     * containing only "-"
     */
    private fun String.isHeaderSeparator(): Boolean {
        val trimmed = this.trim()
        return this.isNotEmpty() && trimmed.all { it == '-' }
    }
}
