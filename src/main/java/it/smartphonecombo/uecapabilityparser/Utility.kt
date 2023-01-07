package it.smartphonecombo.uecapabilityparser

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.CompactedCapabilities
import it.smartphonecombo.uecapabilityparser.bean.ICombo
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.CompactedCombo
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import kotlinx.serialization.json.*
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

/**
 * The Class Utility.
 */
object Utility {
    /**
     * Compact. This works only for ordered ComboList!
     *
     * @param list the list
     * @return the compacted combo list
     */
    fun compact(list: Capabilities): CompactedCapabilities {
        val lteCombos = list.lteCombos
        if (lteCombos != null) {
            // Try to reduce re-hashing
            val initialCapacity = maxOf(lteCombos.size / 2, 16)
            val map: MutableMap<List<Pair<Int, Char>>, MutableList<CompactedCombo>> =
                HashMap(initialCapacity)

            lteCombos.forEach { combo: ComboLte ->
                val compactBands: MutableList<Pair<Int, Char>> = ArrayList()
                val mimoConf = StringBuilder()
                val uplinkConf = StringBuilder()
                var keepMimo = false

                combo.masterComponents.forEach { band ->
                    val bandNumber = band.band
                    compactBands.add(Pair(bandNumber, band.classDL))
                    mimoConf.append(band.mimoDL)
                    if (!keepMimo && band.mimoDL > 2) {
                        keepMimo = true
                    }
                    uplinkConf.append(band.classUL)
                }

                //Flag 3 = mixed
                val mimo =
                    if ((list.flags != 3 || mimoConf.contains("0")) && !keepMimo) {
                        mutableListOf()
                    } else {
                        mutableListOf(mimoConf.toString())
                    }

                val compactCombo = CompactedCombo(
                    compactBands.toTypedArray(),
                    mimo,
                    mutableListOf(uplinkConf.toString())
                )

                map.getOrPut(compactBands) {
                    mutableListOf()
                }.add(compactCombo)
            }

            val flatMap = map.values.flatMap { items ->
                val mimoMap: MutableMap<String?, CompactedCombo> = HashMap()
                items.forEach { combo ->
                    val key = combo.mimo.getOrNull(0)
                    mimoMap.putIfAbsent(key, combo)
                        ?.addUpload(combo.upload[0])
                }

                val ulMap: MutableMap<List<String>, CompactedCombo> = HashMap()
                mimoMap.values.forEach { combo ->
                    val key = combo.upload
                    val res = ulMap.putIfAbsent(key, combo)
                    if (res != null && combo.mimo.isNotEmpty()) {
                        res.addMimo(combo.mimo[0])
                    }
                }
                ulMap.values
            }

            val compareMimoUpload = { s1: List<String>, s2: List<String> ->
                var result = s1.size.compareTo(s2.size)
                if (result == 0) {
                    for (i in s1.indices) {
                        result = s1[i].compareTo(s2[i])
                        if (result != 0) {
                            break
                        }
                    }
                }
                result * -1
            }
            val compactedCombos = flatMap.sortedWith(
                Comparator.comparing(CompactedCombo::bands) { s1: Array<Pair<Int, Char>>, s2: Array<Pair<Int, Char>> ->
                    var result: Int
                    val min = minOf(s1.size, s2.size)

                    for (i in 0 until min) {
                        result = s1[i].first.compareTo(s2[i].first)
                        if (result != 0) {
                            return@comparing result
                        }
                    }

                    result = s1.size.compareTo(s2.size)

                    if (result == 0) {
                        for (i in 0 until min) {
                            result = s1[i].second.compareTo(s2[i].second)
                            if (result != 0) {
                                break
                            }
                        }
                    }

                    result
                }.thenComparing(CompactedCombo::mimo, compareMimoUpload)
                    .thenComparing(CompactedCombo::upload, compareMimoUpload)
            ).toTypedArray()
            return CompactedCapabilities(list.flags, compactedCombos)
        }
        return CompactedCapabilities(list.flags, emptyArray())
    }


    @Throws(IOException::class)
    fun readFile(path: String, encoding: Charset): String {
        val encoded = Files.readAllBytes(Paths.get(path))
        return String(encoded, encoding)
    }

    /**
     *  outputs lteCombos or enDcCombos or nrCombos, the first non-null and non-empty
     **/
    fun toCsv(list: Capabilities): String {
        val lteCombos = list.lteCombos
        val enDcCombos = list.enDcCombos
        val nrCombos = list.nrCombos

        return if (!lteCombos.isNullOrEmpty())  {
            toCsv(lteCombos)
        } else if (!enDcCombos.isNullOrEmpty()) {
            toCsv(enDcCombos)
        } else if(!nrCombos.isNullOrEmpty()) {
            toCsv(nrCombos)
        } else {
            ""
        }
    }

    fun toCsv(lists: List<ICombo>): String {
        if (lists.isEmpty()) return ""
        val standalone = lists.none { it.secondaryComponents.isNotEmpty() }
        val contentFile: StringBuilder = if (lists[0] is ComboNr) {
            StringBuilder(getNrCsvHeader(standalone))
        } else {
            StringBuilder(getLteCsvHeader())
        }
        for (x in lists) {
            contentFile.append(x.toCsv(";", standalone)).append("\n")
        }
        return contentFile.toString()
    }

    private fun getNrCsvHeader(standalone: Boolean): String {
        val separator = ";"
        val header = StringBuilder("combo;")
        val lteDlCC = if (standalone) {
            0
        } else {
            ImportCapabilities.lteDlCC
        }
        val lteUlCC = if (standalone) {
            0
        } else {
            ImportCapabilities.lteUlCC
        }
        val nrDlCC = ImportCapabilities.nrDlCC
        val nrUlCC = ImportCapabilities.nrUlCC

        for (i in 1..lteDlCC) {
            header.append("DL").append(i).append(separator)
        }
        for (i in 1..lteUlCC) {
            header.append("UL").append(i).append(separator)
                .append("MOD UL").append(i).append(separator)
        }
        for (i in 1..nrDlCC) {
            header.append("NR DL").append(i).append(separator)
                .append("NR BW").append(i).append(separator)
                .append("NR SCS").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("NR UL").append(i).append(separator)
                .append("NR UL MOD").append(i).append(separator)
        }
        for (i in 1..lteDlCC) {
            header.append("mimo DL").append(i).append(separator)
        }
        for (i in 1..nrDlCC) {
            header.append("mimo NR DL").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("mimo NR UL").append(i).append(separator)
        }
        header.append("\n")
        return header.toString()
    }

    private fun getLteCsvHeader(): String {
        val separator = ";"
        val header = StringBuilder("combo;")
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("band").append(i).append(separator)
        }
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("class").append(i).append(separator)
        }
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("mimo").append(i).append(separator)
        }
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("ul").append(i).append(separator)
        }
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("DLmod").append(i).append(separator)
        }
        for (i in 1..ImportCapabilities.lteDlCC) {
            header.append("ULmod").append(i).append(separator)
        }
        header.append("bsc\n")
        return header.toString()
    }

    fun convertNumber(numberAsString: String?): Int {
        return when (numberAsString?.lowercase(Locale.getDefault())) {
            "one" -> 1
            "two" -> 2
            "three" -> 3
            "four" -> 4
            "eight" -> 8
            else -> 0
        }
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((s[i].digitToInt(16) shl 4) + s[i + 1].digitToInt(16)).toByte()
            i += 2
        }
        return data
    }

    fun preformatHexData(strEncodedData: String?): String {
        var t = strEncodedData
        t = t!!.uppercase()
        if (t.contains(" ")) t = t.replace(" ", "")
        if (t.contains("\t")) t = t.replace("\t", "")
        if (t.contains("\r")) t = t.replace("\r", "")
        if (t.contains("\n")) t = t.replace("\n", "")
        if (t.contains("0x")) t = t.replace("0x", "")
        if (t.contains(",")) t = t.replace(",", "")
        return if (t.length % 2 == 1) {
            t + '0'
        } else t
    }

    fun bcsToArray(bcs: Int, qualcomm: Boolean): IntArray {
        var bcs = bcs
        if (bcs == -1) return IntArray(0)
        if (bcs == 0) return intArrayOf(0)
        var x = 0
        var y = 0
        return if (qualcomm) {
            val bcsArray = IntArray(Integer.bitCount(bcs))
            while (bcs > 0) {
                if (bcs and 1 == 1) {
                    bcsArray[y++] = x
                }
                bcs = bcs shr 1
                x++
            }
            bcsArray
        } else {
            val binary = Integer.toBinaryString(bcs)
            binaryStringToBcsArray(binary)
        }
    }

    fun binaryStringToBcsArray(bcs: String): IntArray {
        val bcsArray = mutableListOf<Int>()
        for (x in bcs.indices) {
            if (bcs[x] == '1') {
                bcsArray.add(x)
            }
        }
        return bcsArray.toIntArray()
    }

    fun arrayToQcomBcs(bcs: IntArray): String {
        if (bcs.isEmpty()) return "mAll"
        if (bcs.size == 1) return bcs[0].toString() + ""
        var count = 0
        for (i in bcs) {
            count += 1 shl i
        }
        return "m$count"
    }

    fun convertBCStoInt(bcsString: String, isHex: Boolean): Int {
        try {
            if (isHex) {
                if (bcsString.isNotEmpty()) {
                    return bcsString.substring(0, 1).toInt(16)
                }
            } else {
                return if (bcsString.length > 7) {
                    bcsString.substring(0, 8).toInt(2)
                } else {
                    bcsString.toInt(2)
                }
            }
        } catch (ignored: NumberFormatException) {
        }
        return 0
    }

    fun split0xB826hex(input: String): List<String> {
        fun String.emptyLineIndex(): Int {
            return Regex("^\\s*$", RegexOption.MULTILINE).find(this)?.range?.first ?: this.length
        }

        fun String.notHexLineIndex(): Int {
            return Regex("[G-Z]", RegexOption.IGNORE_CASE).find(this)?.range?.first ?: this.length
        }

        return if (input.contains("Payload:")) {
            input.split("Payload:").drop(1).map { x ->
                preformatHexData(
                    x.substring(0, minOf(x.emptyLineIndex(), x.notHexLineIndex()))
                )
            }
        } else {
            input.split(Regex("^\\s*$", RegexOption.MULTILINE))
        }
    }

    fun bwStringToArray(bwString: String, FR2: Boolean, v1590: Boolean): IntArray {
        var bws = bwString.replace(" ", "").toInt(2)
        val bwsArray = IntArray(Integer.bitCount(bws))
        val fr2 = intArrayOf(50, 100, 200)
        val fr1 = intArrayOf(5, 10, 15, 20, 25, 30, 40, 50, 60, 80)
        val fr1v1590 = intArrayOf(70, 45, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var x = 1
        var y = 0
        var arrayMap = fr1
        if (FR2) {
            arrayMap = fr2
        } else if (v1590) {
            arrayMap = fr1v1590
        }
        while (bws > 0) {
            if (bws and 1 == 1) {
                bwsArray[y++] = arrayMap[arrayMap.size - x]
            }
            bws = bws shr 1
            x++
        }
        return bwsArray
    }

    val osType: OsTypes by lazy {
        with(System.getProperty("os.name").lowercase(Locale.getDefault())) {
            if (contains("win"))
                OsTypes.WINDOWS
            else if (listOf("nix", "nux", "aix").any { contains(it) })
                OsTypes.LINUX
            else if (contains("mac"))
                OsTypes.MAC
            else if (contains("sunos"))
                OsTypes.SOLARIS
            else
                OsTypes.OTHER
        }
    }

    /**
     * Appends the given string before the last dot in the filename.
     * If there isn't any dot, it appends it to the end of the string.
     *
     **/
    fun appendBeforeExtension(fileName: String, stringToAppend: String): String {
        val split = fileName.split(".")
        return if (split.size < 2) {
            fileName + stringToAppend
        } else {
            split.dropLast(1).joinToString(separator = ".", postfix = stringToAppend + "." + split.last())
        }
    }

    fun outputFile(text: String, outputFile: String?) {
        var writer: PrintWriter? = null
        try {
            if (!outputFile.isNullOrBlank()) {
                writer = PrintWriter(
                    BufferedWriter(FileWriter(outputFile))
                )
                writer.write(text)
            } else {
                println(text)
            }
        } catch (ex: Exception) {
            System.err.println("Error ${ex.localizedMessage}")
            exitProcess(1)
        } finally {
            writer?.close()
        }
    }

    private fun outputBinFile(text: ByteArray, outputFile: String?) {
        var writer: FileOutputStream? = null
        try {
            if (!outputFile.isNullOrBlank()) {
                writer = FileOutputStream(outputFile)
            }
            writer?.write(text)
        } catch (ex: Exception) {
            System.err.println("Error ${ex.localizedMessage}")
            exitProcess(1)
        } finally {
            writer?.close()
        }
    }

    fun multipleParser(input: String, split: Boolean, importer: ImportCapabilities): Capabilities {
        val tempfile = File.createTempFile("0xB826-", ".bin")
        val inputArray = if (split) {
            split0xB826hex(input)
        } else {
            listOf(input)
        }
        val list = mutableListOf<Capabilities>()
        inputArray.forEach {
            outputBinFile(hexStringToByteArray(preformatHexData(it)), tempfile.path)
            list.add(importer.parse(tempfile.path))
            tempfile.delete()
        }
        val enDcCombos = list.fold(mutableListOf<ComboNr>()) { sum, x ->
            x.enDcCombos?.let { sum.addAll(it) }
            sum
        }
        val nrCombos = list.fold(mutableListOf<ComboNr>()) { sum, x ->
            x.nrCombos?.let { sum.addAll(it) }
            sum
        }

        return Capabilities().also {
            it.enDcCombos = enDcCombos
            it.nrCombos = nrCombos
        }
    }

    enum class OsTypes {
        WINDOWS, LINUX, MAC, SOLARIS, OTHER
    }
}