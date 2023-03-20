package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImportMTKLte : ImportCapabilities {
    @Throws(IndexOutOfBoundsException::class)
    override fun parse(input: InputStream): Capabilities {
        var matchRes: MatchResult?
        val listCombos: MutableList<ComboLte> = ArrayList()
        val total: Int
        val arraySize: Int
        val lines = input.reader().use(InputStreamReader::readLines)
        var index = 0
        var pattern = Pattern.compile("ca_total_num = 0x(\\d*)", Pattern.CASE_INSENSITIVE)
        matchRes = scanUntilMatch(lines, pattern, index)
        if (matchRes != null) {
            index = matchRes.index
            total =
                try {
                    matchRes.matcher.group(1).toInt(16)
                } catch (ex: NumberFormatException) {
                    0
                }
        }
        pattern = Pattern.compile("band_comb = Array\\[(\\d*)]", Pattern.CASE_INSENSITIVE)
        matchRes = scanUntilMatch(lines, pattern, ++index)
        if (matchRes != null) {
            index = matchRes.index
            arraySize =
                try {
                    matchRes.matcher.group(1).toInt()
                } catch (ex: NumberFormatException) {
                    0
                }
        }
        pattern = Pattern.compile("band_comb\\[(\\d*)] = \\(struct\\)", Pattern.CASE_INSENSITIVE)
        val patternMimo = Pattern.compile("band_mimo = Array\\[(\\d*)]", Pattern.CASE_INSENSITIVE)
        while (scanUntilMatch(lines, pattern, ++index).also { matchRes = it } != null) {
            index = matchRes!!.index
            try {
                val comboNum = matchRes!!.matcher.group(1).toInt()
                var str = lines[++index].trim { it <= ' ' }.substring(19)
                val numCCs = str.toInt(16)
                // Sanity check
                if (numCCs < 1 || numCCs > 10) {
                    continue
                }
                str = lines[++index].trim { it <= ' ' }
                str = str.substring(19, str.length - 1)
                val arrayLength = str.toInt()
                val bands: MutableList<IComponent> = ArrayList()
                var i = 0
                while (i < arrayLength && i < numCCs) {
                    index++
                    do {
                        str = lines[++index].trim { it <= ' ' }
                    } while (!str.startsWith("band = "))
                    str = str.substring(9)
                    val baseBand = str.toInt(16)
                    str = lines[++index].trim { it <= ' ' }.substring(18)
                    var bwClass = 'A' + str.toInt(16)
                    var classUL = '0'
                    if (bwClass < 'F') {
                        classUL = bwClass
                    }
                    str = lines[++index].trim { it <= ' ' }.substring(18)
                    bwClass = 'A' + str.toInt(16)
                    val band = ComponentLte(baseBand, bwClass, classUL)
                    band.modDL = null
                    band.modUL = null
                    bands.add(band)
                    i++
                }
                matchRes = scanUntilMatch(lines, patternMimo, ++index)
                if (matchRes != null) {
                    index = matchRes!!.index
                    val length = matchRes!!.matcher.group(1).toInt()
                    var k = 0
                    while (k < length && k < numCCs) {
                        while (!lines[++index].trim { it <= ' ' }.startsWith("band_mimo[$k]")) ;
                        str = lines[++index].trim { it <= ' ' }.substring(18)
                        if (str.startsWith("ERRC_CAPA_CA_MIMO_CAPA_FOUR_LAYERS")) {
                            bands[k].mimoDL = 4
                        } else if (str.startsWith("ERRC_CAPA_CA_MIMO_CAPA_TWO_LAYERS")) {
                            bands[k].mimoUL = 2
                        } else {
                            if (debug) {
                                println(str)
                            }
                        }
                        k++
                    }
                }
                bands.sortWith(IComponent.defaultComparator.reversed())
                listCombos.add(ComboLte(bands.toTypedArray()))
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
            }
        }
        return Capabilities(listCombos)
    }

    private fun scanUntilMatch(lines: List<String>, pattern: Pattern, start: Int): MatchResult? {
        var i = start
        while (i < lines.size) {
            val str = lines[i].trim { it <= ' ' }
            val match = pattern.matcher(str)
            if (match.matches()) {
                return MatchResult(i, match)
            }
            i++
        }
        return null
    }

    internal class MatchResult(val index: Int, val matcher: Matcher)
}
