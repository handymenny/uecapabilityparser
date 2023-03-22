package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.util.NoSuchElementException

object ImportMTKLte : ImportCapabilities {

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

    @Throws(NoSuchElementException::class)
    private fun parseCombo(iterator: Iterator<String>): List<ComponentLte>? {
        val numCCs = extractInt(iterator.next())

        // Check if combo contains any CCs
        if (numCCs < 1) {
            return null
        }

        val arrayLength = extractArraySize(iterator.next())
        val bands = parseComponents(minOf(numCCs, arrayLength), iterator)

        val line = iterator.firstOrNull { it.startsWith("band_mimo = Array") } ?: return null
        val mimoArrayLength = extractArraySize(line)

        parseMimo(minOf(numCCs, mimoArrayLength), iterator, bands)
        return bands
    }

    @Throws(NoSuchElementException::class)
    private fun parseMimo(numCCs: Int, iterator: Iterator<String>, bands: List<ComponentLte>) {
        for (i in 0 until numCCs) {
            iterator.firstOrNull { it.startsWith("band_mimo[$i]") } ?: break
            val line = extractValue(iterator.next()).split(" ").first()
            if (line == "ERRC_CAPA_CA_MIMO_CAPA_FOUR_LAYERS") {
                bands[i].mimoDL = 4
            } else if (line == "ERRC_CAPA_CA_MIMO_CAPA_TWO_LAYERS") {
                bands[i].mimoDL = 2
            }
        }
    }

    @Throws(NoSuchElementException::class)
    private fun parseComponents(numCCs: Int, iterator: Iterator<String>): List<ComponentLte> {
        val bands = mutableListOf<ComponentLte>()
        for (i in 0 until numCCs) {
            iterator.firstOrNull { it.startsWith("band_param[$i]") }
            val baseBand = extractInt(iterator.next())
            val classUL = extractInt(iterator.next()).toBwClassMtk()
            val classDL = extractInt(iterator.next()).toBwClassMtk()
            val band = ComponentLte(baseBand, classDL, classUL, 0, null, null)
            bands.add(band)
        }
        return bands
    }

    private fun Int.toBwClassMtk(): Char {
        val value = (this + 0x41).toChar()

        return if (value in 'A'..'F') value else '0'
    }

    private fun extractValue(line: String): String {
        return line.split("=").last().trim()
    }

    private val arrayRegex = """Array\[(\d+)]""".toRegex()
    private fun extractArraySize(line: String): Int {
        return arrayRegex.find(line)?.groupValues?.get(1)?.toInt() ?: 0
    }

    @Throws(NumberFormatException::class)
    private fun extractInt(line: String): Int {
        return Integer.decode(extractValue(line))
    }

    private inline fun Iterator<String>.firstOrNull(predicate: (String) -> Boolean): String? {
        for (item in this) {
            if (predicate(item)) {
                return item
            }
        }
        return null
    }
}
