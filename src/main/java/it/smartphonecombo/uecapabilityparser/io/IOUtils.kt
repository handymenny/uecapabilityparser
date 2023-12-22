package it.smartphonecombo.uecapabilityparser.io

import it.smartphonecombo.uecapabilityparser.cli.Cli.echo
import it.smartphonecombo.uecapabilityparser.extension.gzipCompress
import it.smartphonecombo.uecapabilityparser.extension.gzipDecompress
import it.smartphonecombo.uecapabilityparser.extension.moveTo
import it.smartphonecombo.uecapabilityparser.extension.readText
import it.smartphonecombo.uecapabilityparser.extension.typedList
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object IOUtils {

    /** Call [echo] or [println] if Cli isn't initialized * */
    fun echoSafe(message: Any?, err: Boolean = false) {
        try {
            echo(message, err = err)
        } catch (_: Exception) {
            // Context not initialized
            if (err) {
                System.err.println(message)
            } else {
                println(message)
            }
        }
    }

    /**
     * Output the given [text] to [outputFile]
     *
     * if outputFile is null the text is sent to stdout.
     */
    fun outputFileOrStdout(text: String, outputFile: String?) {
        try {
            if (!outputFile.isNullOrBlank()) {
                File(outputFile).writeText(text)
            } else {
                echoSafe(text)
            }
        } catch (ex: Exception) {
            echoSafe("Error ${ex.localizedMessage}", true)
        }
    }

    /**
     * Output the given [byteArray] to [outputFile]
     *
     * if [compress] is true ".gz" is automatically appended to [outputFile]
     */
    fun outputFile(byteArray: ByteArray, outputFile: String, compress: Boolean) {
        var path = outputFile
        var bytes = byteArray

        if (compress) {
            path += ".gz"
            bytes = bytes.gzipCompress()
        }

        try {
            File(path).writeBytes(bytes)
        } catch (ex: Exception) {
            echoSafe("Error ${ex.localizedMessage}", true)
        }
    }

    /** See [Files.createDirectories] */
    fun createDirectories(path: String) {
        try {
            Files.createDirectories(Paths.get(path))
        } catch (ex: Exception) {
            echoSafe("Error ${ex.localizedMessage}", true)
        }
    }

    fun toCsv(lists: List<ICombo>, newLteCaFormat: Boolean = false): String {
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
            lteUlCC = maxUlCC(lists)
        } else {
            nrDlCC = maxDlCC(lists)
            nrUlCC = maxUlCC(lists)
            if (nrDc) {
                nrDcDlCC = maxDlCC(lists, true)
                nrDcUlCC = maxUlCC(lists, true)
            }
        }

        if (enDc) {
            nrDlCC = maxDlCC(lists, true)
            nrUlCC = maxUlCC(lists, true)
        }

        val contentFile: StringBuilder =
            if (isNr || newLteCaFormat) {
                StringBuilder(getCsvHeader(lteDlCC, lteUlCC, nrDlCC, nrUlCC, nrDcDlCC, nrDcUlCC))
            } else {
                StringBuilder(getOldLteCsvHeader(lteDlCC))
            }

        if (isNr || newLteCaFormat) {
            for (x in lists) {
                contentFile
                    .append(x.toCsv(";", lteDlCC, lteUlCC, nrDlCC, nrUlCC, nrDcDlCC, nrDcUlCC))
                    .append("\n")
            }
        } else {
            val lteCaList = lists.typedList<ComboLte>()
            for (x in lteCaList) {
                contentFile.append(x.toCsvOld(";", lteDlCC)).append("\n")
            }
        }
        return contentFile.toString()
    }

    private fun getCsvHeader(
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
        if (nrDlCC == 0) {
            for (i in 1..lteDlCC) {
                header.append("MOD DL").append(i).append(separator)
            }
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
                .append("NR UL BW")
                .append(i)
                .append(separator)
                .append("NR UL SCS")
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
                .append("FR2 UL BW")
                .append(i)
                .append(separator)
                .append("FR2 UL SCS")
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
        for (i in 1..lteUlCC) {
            header.append("mimo LTE UL").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("mimo NR UL").append(i).append(separator)
        }
        for (i in 1..nrDcUlCC) {
            header.append("mimo FR2 UL").append(i).append(separator)
        }

        if (lteDlCC > 0 && nrDlCC > 0) {
            header.append("BCS NR", separator, "BCS LTE", separator, "BCS intraENDC")
        } else {
            header.append("BCS")
        }

        header.append("\n")

        return if (nrDcDlCC > 0) {
            header.toString().replace("NR", "FR1")
        } else if (nrDlCC == 0) {
            header.toString().replace(" LTE", "")
        } else {
            header.toString()
        }
    }

    private fun getOldLteCsvHeader(lteDlCC: Int): String {
        val separator = ";"
        val header = StringBuilder("combo;")
        val columns = arrayOf("band", "class", "mimo", "ul", "ULmimo", "DLmod", "ULmod")
        for (column in columns) {
            for (i in 1..lteDlCC) {
                header.append(column).append(i).append(separator)
            }
        }
        header.append("bcs\n")
        return header.toString()
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

    /** if [compressed] is true, automatically appends ".gz". Return null if file doesn't exist */
    fun readTextFromFile(filePath: String, compressed: Boolean): String? {
        val addExtension = if (compressed) ".gz" else ""

        return readTextFromFile(File(filePath + addExtension), compressed)
    }

    /** Return null if file doesn't exist */
    fun readTextFromFile(file: File, compressed: Boolean): String? {
        if (!file.exists()) return null

        return if (compressed) file.gzipDecompress().readText() else file.readText()
    }

    /** if [compressed] is true, automatically appends ".gz". Return null if file doesn't exist */
    fun readBytesFromFile(filePath: String, compressed: Boolean): ByteArray? {
        val addExtension = if (compressed) ".gz" else ""

        return readBytesFromFile(File(filePath + addExtension), compressed)
    }

    /** Return null if file doesn't exist */
    fun readBytesFromFile(file: File, compressed: Boolean): ByteArray? {
        if (!file.exists()) return null

        return if (compressed) {
            file.gzipDecompress().use { it.readBytes() }
        } else {
            file.readBytes()
        }
    }

    /**
     * if [compressed] is true, automatically appends ".gz". Return null if file doesn't exist. File
     * is moved [srcPath] to [dstPath] after a successful read.
     */
    fun readAndMove(srcPath: String, dstPath: String, compressed: Boolean): ByteArray? {
        val addExtension = if (compressed) ".gz" else ""

        val inputFile = File(srcPath + addExtension)
        val bytes = readBytesFromFile(inputFile, compressed)
        inputFile.moveTo(dstPath + addExtension)

        return bytes
    }

    /**
     * if [compressed] is true, automatically appends ".gz". Return null if file doesn't exist. File
     * is moved from [srcPath] to [dstPath] and it's inputSource is returned.
     */
    fun inputSourceAndMove(srcPath: String, dstPath: String, compressed: Boolean): InputSource? {
        val addExtension = if (compressed) ".gz" else ""
        val inputFile = File(srcPath + addExtension)
        if (!inputFile.exists()) return null

        val dstFile = File(dstPath + addExtension)
        inputFile.moveTo(dstFile.path)
        return dstFile.toInputSource(compressed)
    }
}
