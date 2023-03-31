package it.smartphonecombo.uecapabilityparser.util

import com.ericsson.mts.asn1.ASN1Converter
import com.ericsson.mts.asn1.ASN1Translator
import com.ericsson.mts.asn1.KotlinJsonFormatWriter
import com.ericsson.mts.asn1.PERTranslatorFactory
import com.ericsson.mts.asn1.converter.AbstractConverter
import it.smartphonecombo.uecapabilityparser.extension.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
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
        val standalone = lists.any { it is ComboNr }
        val nrDc = lists.any { it is ComboNrDc }
        val enDc = lists.any { it is ComboEnDc }
        val isNr = standalone || nrDc || enDc

        var lteDlCC = 0
        var lteUlCC = 0
        var nrDlCC = 0
        var nrUlCC = 0
        var nrDcDlCC = 0
        var nrDcUlCC = 0

        if (!isNr || enDc) {
            lteDlCC = maxDlCC(lists)
        } else {
            nrDlCC = maxDlCC(lists)
            nrUlCC = maxUlCC(lists)
            if (nrDc) {
                nrDcDlCC = maxDlCC(lists, true)
                nrDcUlCC = maxUlCC(lists, true)
            }
        }

        if (enDc) {
            // LTE csv doesn't use UL CC
            lteUlCC = maxUlCC(lists)
            nrDlCC = maxDlCC(lists, true)
            nrUlCC = maxUlCC(lists, true)
        }

        val contentFile: StringBuilder =
            if (isNr) {
                StringBuilder(getNrCsvHeader(lteDlCC, lteUlCC, nrDlCC, nrUlCC, nrDcDlCC, nrDcUlCC))
            } else {
                StringBuilder(getLteCsvHeader(lteDlCC))
            }

        for (x in lists) {
            contentFile
                .append(x.toCsv(";", lteDlCC, lteUlCC, nrDlCC, nrUlCC, nrDcDlCC, nrDcUlCC))
                .append("\n")
        }
        return contentFile.toString()
    }

    private fun getNrCsvHeader(
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int
    ): String {
        val separator = ";"
        val header = StringBuilder("combo;")

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
        for (i in 1..nrDcDlCC) {
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
        for (i in 1..nrDcUlCC) {
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
        for (i in 1..nrDcDlCC) {
            header.append("mimo FR2 DL").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("mimo NR UL").append(i).append(separator)
        }
        for (i in 1..nrDcUlCC) {
            header.append("mimo FR2 UL").append(i).append(separator)
        }
        header.append("\n")

        return if (nrDcDlCC > 0) {
            header.toString().replace("NR", "FR1")
        } else {
            header.toString()
        }
    }

    private fun getLteCsvHeader(lteDlCC: Int): String {
        val separator = ";"
        val header = StringBuilder("combo;")
        val columns = arrayOf("band", "class", "mimo", "ul", "DLmod", "ULmod")
        for (column in columns) {
            for (i in 1..lteDlCC) {
                header.append(column).append(i).append(separator)
            }
        }
        header.append("bsc\n")
        return header.toString()
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

    fun multipleParser(input: String, split: Boolean, importer: ImportCapabilities): Capabilities {
        val inputArray =
            if (split) {
                split0xB826hex(input)
            } else {
                listOf(input)
            }
        val list = mutableListWithCapacity<Capabilities>(inputArray.size)
        inputArray.forEach {
            val inputStream = hexStringToByteArray(preformatHexData(it)).inputStream()
            list.add(importer.parse(inputStream))
        }
        val enDcCombos =
            list.fold(mutableListOf<ComboEnDc>()) { sum, x ->
                x.enDcCombos?.let { sum.addAll(it) }
                sum
            }
        val nrCombos =
            list.fold(mutableListOf<ComboNr>()) { sum, x ->
                x.nrCombos?.let { sum.addAll(it) }
                sum
            }
        val nrDcCombos =
            list.fold(mutableListOf<ComboNrDc>()) { sum, x ->
                x.nrDcCombos?.let { sum.addAll(it) }
                sum
            }

        return Capabilities().also {
            it.enDcCombos = enDcCombos
            it.nrCombos = nrCombos
            it.nrDcCombos = nrDcCombos
        }
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
            if (rat == Rat.EUTRA) {
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
                Rat.EUTRA -> {
                    asn1TranslatorLte.decode(
                        rat.ratCapabilityIdentifier,
                        bytes.inputStream(),
                        jsonWriter
                    )
                    jsonWriter.jsonNode?.let { put(rat.toString(), it) }
                }
                Rat.EUTRA_NR -> {
                    asn1TranslatorNr.decode(
                        rat.ratCapabilityIdentifier,
                        bytes.inputStream(),
                        jsonWriter
                    )
                    jsonWriter.jsonNode?.let { put(rat.toString(), it) }
                }
                Rat.NR -> {
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

    private fun maxDlCC(list: List<ICombo>, secondary: Boolean = false): Int {
        return if (secondary) {
            list.fold(0) { acc, it -> maxOf(acc, it.secondaryComponents.size) }
        } else {
            list.fold(0) { acc, it -> maxOf(acc, it.masterComponents.size) }
        }
    }

    private fun maxUlCC(list: List<ICombo>, secondary: Boolean = false): Int {
        return if (secondary) {
            list.fold(0) { acc, it ->
                maxOf(acc, it.secondaryComponents.filter { it.classUL != BwClass.NONE }.size)
            }
        } else {
            list.fold(0) { acc, it ->
                maxOf(acc, it.masterComponents.filter { it.classUL != BwClass.NONE }.size)
            }
        }
    }

    fun appendSeparator(separator: String, vararg strings: StringBuilder) {
        strings.forEach { it.append(separator) }
    }
}
