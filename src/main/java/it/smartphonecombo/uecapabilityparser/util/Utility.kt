package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.preformatHex
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc

/** The Class Utility. */
object Utility {

    fun split0xB826hex(input: String): List<String> {
        fun String.emptyLineIndex(): Int {
            return Regex("^\\s*$", RegexOption.MULTILINE).find(this)?.range?.first ?: this.length
        }

        fun String.notHexLineIndex(): Int {
            return Regex("[G-Z]", RegexOption.IGNORE_CASE).find(this)?.range?.first ?: this.length
        }

        return if (input.contains("Payload:")) {
            input.split("Payload:").drop(1).map {
                it.substring(0, minOf(it.emptyLineIndex(), it.notHexLineIndex()))
            }
        } else {
            input.split(Regex("^\\s*$", RegexOption.MULTILINE))
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
            try {
                val inputStream = it.preformatHex().decodeHex().inputStream()
                list.add(importer.parse(inputStream))
            } catch (err: IllegalArgumentException) {
                val errMessage = "Invalid hexdump"
                val multiHelp =
                    if (!split)
                        "Use flag '--multiple0xB826' if you are parsing multiple 0xB826 hexdumps."
                    else ""
                throw IllegalArgumentException(errMessage + multiHelp, err)
            }
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
}
