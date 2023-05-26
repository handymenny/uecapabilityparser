package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object Output {

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
                println(text)
            }
        } catch (ex: Exception) {
            System.err.println("Error ${ex.localizedMessage}")
        }
    }

    /** Output the given [byteArray] to [outputFile] */
    fun outputFile(byteArray: ByteArray, outputFile: String) {
        try {
            File(outputFile).writeBytes(byteArray)
        } catch (ex: Exception) {
            System.err.println("Error ${ex.localizedMessage}")
        }
    }

    /** See [Files.createDirectories] */
    fun createDirectories(path: String) {
        try {
            Files.createDirectories(Path.of(path))
        } catch (ex: Exception) {
            System.err.println("Error ${ex.localizedMessage}")
        }
    }

    /** outputs lteCombos or enDcCombos or nrCombos, the first non-null and non-empty */
    fun toCsv(list: Capabilities): String {
        val lteCombos = list.lteCombos
        val enDcCombos = list.enDcCombos
        val nrCombos = list.nrCombos

        return if (lteCombos.isNotEmpty()) {
            toCsv(lteCombos)
        } else if (enDcCombos.isNotEmpty()) {
            toCsv(enDcCombos)
        } else if (nrCombos.isNotEmpty()) {
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
        for (i in 1..lteUlCC) {
            header.append("mimo LTE UL").append(i).append(separator)
        }
        for (i in 1..nrUlCC) {
            header.append("mimo NR UL").append(i).append(separator)
        }
        for (i in 1..nrDcUlCC) {
            header.append("mimo FR2 UL").append(i).append(separator)
        }

        if (lteDlCC > 0) {
            header.append("BCS NR", separator, "BCS LTE", separator, "BCS intraENDC")
        } else {
            header.append("BCS")
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
}
