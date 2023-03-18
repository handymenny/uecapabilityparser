package it.smartphonecombo.uecapabilityparser

import com.ericsson.mts.asn1.ASN1Converter
import com.ericsson.mts.asn1.ASN1Translator
import com.ericsson.mts.asn1.KotlinJsonFormatWriter
import com.ericsson.mts.asn1.PERTranslatorFactory
import com.ericsson.mts.asn1.converter.AbstractConverter
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.ICombo
import it.smartphonecombo.uecapabilityparser.bean.Rat
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.system.exitProcess
import kotlinx.serialization.json.*

/** The Class Utility. */
object Utility {
    @Throws(IOException::class)
    fun readFile(path: String, encoding: Charset): String {
        return File(path).readText(encoding)
    }

    /** outputs lteCombos or enDcCombos or nrCombos, the first non-null and non-empty */
    fun toCsv(list: Capabilities): String {
        val lteCombos = list.lteCombos
        val enDcCombos = list.enDcCombos
        val nrCombos = list.nrCombos

        return if (!lteCombos.isNullOrEmpty()) {
            toCsv(lteCombos)
        } else if (!enDcCombos.isNullOrEmpty()) {
            toCsv(enDcCombos)
        } else if (!nrCombos.isNullOrEmpty()) {
            toCsv(nrCombos)
        } else {
            ""
        }
    }

    fun toCsv(lists: List<ICombo>): String {
        if (lists.isEmpty()) return ""
        val standalone = lists.none { (it as? ComboNr)?.componentsLte?.isNotEmpty() ?: false }
        val nrDc = lists.any { (it as? ComboNr)?.componentsNrDc?.isNotEmpty() ?: false }
        val contentFile: StringBuilder =
            if (lists[0] is ComboNr) {
                StringBuilder(getNrCsvHeader(standalone, nrDc))
            } else {
                StringBuilder(getLteCsvHeader())
            }
        for (x in lists) {
            contentFile.append(x.toCsv(";", standalone, nrDc)).append("\n")
        }
        return contentFile.toString()
    }

    private fun getNrCsvHeader(standalone: Boolean, nrDc: Boolean): String {
        val separator = ";"
        val header = StringBuilder("combo;")
        val lteDlCC =
            if (standalone || nrDc) {
                0
            } else {
                ImportCapabilities.lteDlCC
            }
        val lteUlCC =
            if (standalone || nrDc) {
                0
            } else {
                ImportCapabilities.lteUlCC
            }
        val nrDlCC = ImportCapabilities.nrDlCC
        val nrUlCC = ImportCapabilities.nrUlCC
        val nrDlCCfr2 =
            if (nrDc) {
                ImportCapabilities.nrDlCC
            } else {
                0
            }
        val nrUlCCfr2 =
            if (nrDc) {
                ImportCapabilities.nrUlCC
            } else {
                0
            }

        for (i in 1..lteDlCC) {
            header.append("DL").append(i).append(separator)
        }
        for (i in 1..lteUlCC) {
            header
                .append("UL")
                .append(i)
                .append(separator)
                .append("MOD UL")
                .append(i)
                .append(separator)
        }
        for (i in 1..nrDlCC) {
            header
                .append("NR DL")
                .append(i)
                .append(separator)
                .append("NR BW")
                .append(i)
                .append(separator)
                .append("NR SCS")
                .append(i)
                .append(separator)
        }
        for (i in 1..nrDlCCfr2) {
            header
                .append("FR2 DL")
                .append(i)
                .append(separator)
                .append("FR2 BW")
                .append(i)
                .append(separator)
                .append("FR2 SCS")
                .append(i)
                .append(separator)
        }
        for (i in 1..nrUlCC) {
            header
                .append("NR UL")
                .append(i)
                .append(separator)
                .append("NR UL MOD")
                .append(i)
                .append(separator)
        }
        for (i in 1..nrUlCCfr2) {
            header
                .append("FR2 UL")
                .append(i)
                .append(separator)
                .append("FR2 UL MOD")
                .append(i)
                .append(separator)
        }
        for (i in 1..lteDlCC) {
            header.append("mimo DL").append(i).append(separator)
        }
        for (i in 1..nrDlCC) {
            header.append("mimo NR DL").append(i).append(separator)
        }
        for (i in 1..nrDlCCfr2) {
            header.append("mimo FR2 DL").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("mimo NR UL").append(i).append(separator)
        }
        for (i in 1..nrUlCCfr2) {
            header.append("mimo FR2 UL").append(i).append(separator)
        }
        header.append("\n")

        return if (nrDc) {
            header.toString().replace("NR", "FR1")
        } else {
            header.toString()
        }
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
        var i = 0

        try {
            val len = s.length
            val data = ByteArray(len / 2)
            while (i < len) {
                data[i / 2] = ((s[i].digitToInt(16) shl 4) + s[i + 1].digitToInt(16)).toByte()
                i += 2
            }
            return data
        } catch (err: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid hexdump: invalid char at position $i of whitespace-trimmed input file.\n\nUse flag '--multiple0xB826' if you are parsing multiple hexdumps.",
                err
            )
        }
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

    fun qcomBcsToArray(bcs: Int): IntArray {
        var bcs = bcs
        if (bcs == -1) return IntArray(0)
        if (bcs == 0) return intArrayOf(0)
        var x = 0
        var y = 0
        val bcsArray = IntArray(Integer.bitCount(bcs))
        while (bcs > 0) {
            if (bcs and 1 == 1) {
                bcsArray[y++] = x
            }
            bcs = bcs shr 1
            x++
        }
        return bcsArray
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
        val hex = count.toString(16).uppercase()
        return "m$hex"
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
                preformatHexData(x.substring(0, minOf(x.emptyLineIndex(), x.notHexLineIndex())))
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
        val fr1v1590 = intArrayOf(70, 45, 35, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
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
            if (contains("win")) OsTypes.WINDOWS
            else if (listOf("nix", "nux", "aix").any { contains(it) }) OsTypes.LINUX
            else if (contains("mac")) OsTypes.MAC
            else if (contains("sunos")) OsTypes.SOLARIS else OsTypes.OTHER
        }
    }

    /**
     * Appends the given string before the last dot in the filename. If there isn't any dot, it
     * appends it to the end of the string.
     */
    fun appendBeforeExtension(fileName: String, stringToAppend: String): String {
        val split = fileName.split(".")
        return if (split.size < 2) {
            fileName + stringToAppend
        } else {
            split
                .dropLast(1)
                .joinToString(separator = ".", postfix = stringToAppend + "." + split.last())
        }
    }

    fun outputFile(text: String, outputFile: String?) {
        var writer: PrintWriter? = null
        try {
            if (!outputFile.isNullOrBlank()) {
                writer = PrintWriter(BufferedWriter(FileWriter(outputFile, Charsets.UTF_8)))
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
        val inputArray =
            if (split) {
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
        val enDcCombos =
            list.fold(mutableListOf<ComboNr>()) { sum, x ->
                x.enDcCombos?.let { sum.addAll(it) }
                sum
            }
        val nrCombos =
            list.fold(mutableListOf<ComboNr>()) { sum, x ->
                x.nrCombos?.let { sum.addAll(it) }
                sum
            }

        return Capabilities().also {
            it.enDcCombos = enDcCombos
            it.nrCombos = nrCombos
        }
    }

    enum class OsTypes {
        WINDOWS,
        LINUX,
        MAC,
        SOLARIS,
        OTHER
    }

    fun String.repeat(n: Int, separator: String) =
        plus(separator).repeat(n).dropLast(separator.length)

    fun JsonElement.getInt(key: String) =
        ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.intOrNull

    fun JsonElement.getString(key: String) =
        ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.contentOrNull

    fun JsonElement.getObject(key: String) = (this as? JsonObject)?.get(key) as? JsonObject

    fun JsonElement.getArray(key: String) = (this as? JsonObject)?.get(key) as? JsonArray

    fun JsonElement.getObjectAtPath(path: String): JsonObject? {
        var obj = this as? JsonObject
        path.split(".").forEach { obj = obj?.getObject(it) }
        return obj
    }

    fun JsonElement.getArrayAtPath(path: String): JsonArray? {
        val split = path.split(".")
        var obj = this as? JsonObject
        for (i in 0 until split.size - 1) {
            obj = obj?.getObject(split[i])
        }
        return obj?.getArray(split.last())
    }

    fun ComponentNr.toBwString(): String {
        val dlString =
            bandwidthsDL
                ?.entries
                ?.filter { it.value.isNotEmpty() }
                ?.joinToString(
                    prefix = "BwDL:[",
                    postfix = "]",
                    transform = { "${it.key}kHz: ${it.value.joinToString()}" },
                    separator = "; ",
                )
        val ulString =
            bandwidthsUL
                ?.entries
                ?.filter { it.value.isNotEmpty() }
                ?.joinToString(
                    prefix = "BwUL:[",
                    postfix = "]",
                    transform = { "${it.key}kHz: ${it.value.joinToString()}" },
                    separator = "; ",
                )
        return "n$band $dlString $ulString"
    }

    private fun getResourceAsStream(path: String): InputStream? =
        object {}.javaClass.getResourceAsStream(path)

    val asn1TranslatorLte by lazy {
        val definition = getResourceAsStream("/definition/EUTRA-RRC-Definitions.asn")!!
        ASN1Translator(PERTranslatorFactory(false), listOf(definition))
    }

    val asn1TranslatorNr by lazy {
        val definition = getResourceAsStream("/definition/NR-RRC-Definitions.asn")!!
        ASN1Translator(PERTranslatorFactory(false), listOf(definition))
    }

    fun getAsn1Converter(rat: Rat, converter: AbstractConverter): ASN1Converter {
        val definition =
            if (rat == Rat.eutra) {
                getResourceAsStream("/definition/EUTRA-RRC-Definitions.asn")!!
            } else {
                getResourceAsStream("/definition/NR-RRC-Definitions.asn")!!
            }
        return ASN1Converter(converter, listOf(definition))
    }

    private fun ratContainerToJson(rat: Rat, bytes: ByteArray): JsonObject {
        val jsonWriter = KotlinJsonFormatWriter()
        val json = buildJsonObject {
            when (rat) {
                Rat.eutra -> {
                    asn1TranslatorLte.decode(
                        rat.ratCapabilityIdentifier,
                        bytes.inputStream(),
                        jsonWriter
                    )
                    jsonWriter.jsonNode?.let { put(rat.toString(), it) }
                }
                Rat.eutra_nr -> {
                    asn1TranslatorNr.decode(
                        rat.ratCapabilityIdentifier,
                        bytes.inputStream(),
                        jsonWriter
                    )
                    jsonWriter.jsonNode?.let { put(rat.toString(), it) }
                }
                Rat.nr -> {
                    asn1TranslatorNr.decode(
                        rat.ratCapabilityIdentifier,
                        bytes.inputStream(),
                        jsonWriter
                    )
                    jsonWriter.jsonNode?.let { put(rat.toString(), it) }
                }
                else -> {}
            }
        }
        return json
    }

    fun getUeCapabilityJsonFromHex(defaultRat: Rat, hexString: String): JsonObject {
        if (hexString.length < 2) {
            return buildJsonObject {}
        }

        val isLteCapInfo = hexString[0] == '3' && hexString[1] in '8'..'E'
        val isNrCapInfo = hexString[0] == '4' && hexString[1] in '8'..'E'

        if (!isLteCapInfo && !isNrCapInfo) {
            return ratContainerToJson(defaultRat, hexStringToByteArray(hexString))
        }

        val jsonWriter = KotlinJsonFormatWriter()
        val translator: ASN1Translator
        val ratContainerListPath: String
        val octetStringKey: String

        if (isLteCapInfo) {
            translator = asn1TranslatorLte
            ratContainerListPath =
                "message.c1.ueCapabilityInformation.criticalExtensions.c1.ueCapabilityInformation-r8.ue-CapabilityRAT-ContainerList"
            octetStringKey = "ueCapabilityRAT-Container"
        } else {
            translator = asn1TranslatorNr
            ratContainerListPath =
                "message.c1.ueCapabilityInformation.criticalExtensions.ueCapabilityInformation.ue-CapabilityRAT-ContainerList"
            octetStringKey = "ue-CapabilityRAT-Container"
        }

        translator.decode(
            "UL-DCCH-Message",
            hexStringToByteArray(hexString).inputStream(),
            jsonWriter
        )

        val ueCap = jsonWriter.jsonNode?.getArrayAtPath(ratContainerListPath)
        val map = mutableMapOf<String, JsonElement>()
        if (ueCap != null) {
            for (ueCapContainer in ueCap) {
                val ratType = Rat.of(ueCapContainer.getString("rat-Type"))
                val octetString = ueCapContainer.getString(octetStringKey)
                if (ratType != null && octetString != null) {
                    map += ratContainerToJson(ratType, hexStringToByteArray(octetString))
                }
            }
        }
        return JsonObject(map)
    }

    fun String.indexOf(regex: Regex): Int {
        return regex.find(this)?.range?.first ?: -1
    }
}
