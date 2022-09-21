package it.smartphonecombo.uecapabilityparser.importer.nr

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImportMTKNr : ImportCapabilities {
    @Throws(IndexOutOfBoundsException::class)
    override fun parse(filename: String): Capabilities {
        var matcher: Matcher
        var matchRes: MatchResult?
        val path = FileSystems.getDefault().getPath(filename)
        val listCombos: MutableList<ComboNr> = ArrayList()
        val total: Int
        val arraySize: Int
        var index = 0
        try {
            val lines = Files.readAllLines(path, StandardCharsets.UTF_8)
            val length = lines.size
            var pattern = Pattern.compile("bc_num = 0x(\\d*)", Pattern.CASE_INSENSITIVE)
            matchRes = scanUntilMatch(lines, pattern, index)
            if (matchRes != null) {
                index = matchRes.index
                total = try {
                    matchRes.matcher.group(1).toInt(16)
                } catch (ex: NumberFormatException) {
                    0
                }
            }
            pattern = Pattern.compile("bc = Array\\[(\\d*)]", Pattern.CASE_INSENSITIVE)
            matchRes = scanUntilMatch(lines, pattern, ++index)
            if (matchRes != null) {
                index = matchRes.index
                arraySize = try {
                    matchRes.matcher.group(1).toInt()
                } catch (ex: NumberFormatException) {
                    0
                }
            }
            pattern = Pattern.compile("bc\\[(\\d*)] = \\(struct\\)", Pattern.CASE_INSENSITIVE)
            val integer = Pattern.compile("\\(enum (\\d*)\\)", Pattern.CASE_INSENSITIVE)
            val bandPattern = Pattern.compile("(LTE)*_Band_?(\\d+)\\s\\(enum (\\d+)\\)", Pattern.CASE_INSENSITIVE)
            while (scanUntilMatch(lines, pattern, ++index).also { matchRes = it } != null) {
                index = matchRes!!.index
                try {
                    val comboNum = matchRes!!.matcher.group(1).toInt()
                    if (++index >= length) {
                        continue
                    }
                    var str = lines[index].trim { it <= ' ' }
                    if (str.length < 20) continue
                    str = str.substring(20)
                    val numCCs: Int = try {
                        str.toInt(16)
                    } catch (ex: NumberFormatException) {
                        System.err.println("error at index: $index")
                        //ex.printStackTrace();
                        continue
                    }

                    //Sanity check
                    if (numCCs < 1 || numCCs > 10) {
                        continue
                    }
                    str = lines[++index].trim { it <= ' ' }
                    str = str.substring(20, str.length - 1)
                    val arrayLength = str.toInt()
                    val lteComponents: MutableList<IComponent> = ArrayList()
                    val componentNrs: MutableList<IComponent> = ArrayList()
                    var i = 0
                    while (i < arrayLength && i < numCCs) {
                        index++

                        //Check if NR or LTE
                        str = lines[++index].trim { it <= ' ' }
                        matcher = integer.matcher(str)
                        matcher.find()
                        str = matcher.group(1)
                        val rat = str.toInt()
                        if (rat == 0) {
                            i++
                            continue
                        }
                        index += 2
                        //Band
                        str = lines[++index].trim { it <= ' ' }
                        matcher = bandPattern.matcher(str)
                        if (!matcher.find()) {
                            i++
                            continue
                        }
                        //Check if rat != type of band
                        str = if (rat == 1 && matcher.group(1) != null) {
                            //if rat != type of band use raw data
                            matcher.group(3)
                        } else {
                            matcher.group(2)
                        }
                        val band = str.toInt()

                        //DL BW Class
                        str = lines[++index].trim { it <= ' ' }
                        matcher = integer.matcher(str)
                        matcher.find()
                        str = matcher.group(1)
                        val dlBWClass = 'A' + str.toInt()

                        //UL BW Class
                        str = lines[++index].trim { it <= ' ' }
                        matcher = integer.matcher(str)
                        matcher.find()
                        str = matcher.group(1)
                        var ulBWClass = 'A' + str.toInt()
                        if (ulBWClass > 'F') {
                            ulBWClass = '0'
                        }
                        if (rat == 1) {
                            val componentNr = ComponentNr(band)
                            componentNr.classDL = dlBWClass
                            componentNr.classUL = ulBWClass
                            componentNrs.add(componentNr)
                        } else if (rat == 2) {
                            val componentLte = ComponentLte()
                            componentLte.band = band
                            componentLte.classDL = dlBWClass
                            componentLte.classUL = ulBWClass
                            lteComponents.add(componentLte)
                        }
                        i++
                    }
                    lteComponents.sortWith(IComponent.defaultComparator.reversed())
                    componentNrs.sortWith(IComponent.defaultComparator.reversed())
                    val bandArray = lteComponents.toTypedArray()
                    val nrbandsArray = componentNrs.toTypedArray()
                    val newCombo = if (bandArray.isEmpty()) {
                        ComboNr(nrbandsArray)
                    } else {
                        ComboNr(bandArray, nrbandsArray)
                    }
                    listCombos.add(newCombo)
                } catch (ex: NumberFormatException) {
                    System.err.println("error at index: $index")
                    ex.printStackTrace()
                }
            }
        } catch (e: Exception) {
            System.err.println("error at index: $index")
            e.printStackTrace()
        }
        return Capabilities(null, listCombos, null, null, null, 0, 0)
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