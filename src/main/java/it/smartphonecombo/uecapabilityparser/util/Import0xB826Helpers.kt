package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.extension.preformatHex
import it.smartphonecombo.uecapabilityparser.importer.Import0xB826
import it.smartphonecombo.uecapabilityparser.model.Capabilities

object Import0xB826Helpers {
    private val regexEmptyLine = Regex("^\\s*$", RegexOption.MULTILINE)

    private fun String.emptyLineIndex(): Int {
        return regexEmptyLine.find(this)?.range?.first ?: this.length
    }

    private fun String.notHexLineIndex(): Int {
        val res = indexOfFirst {
            if (it.isDigit() || it.isWhitespace()) {
                false
            } else if (it.isLetter()) {
                it != 'x' && it !in 'A'..'F' && it !in 'a'..'f'
            } else {
                it != ','
            }
        }
        return if (res == -1) length else res
    }

    private fun split0xB826hex(input: String): List<String> {
        val splitByPayload = input.split("Payload:")
        return if (splitByPayload.size > 1) {
            splitByPayload.drop(1).map {
                it.substring(0, minOf(it.emptyLineIndex(), it.notHexLineIndex()))
            }
        } else {
            input.split(regexEmptyLine)
        }
    }

    fun parseMultiple0xB826(input: String, split: Boolean): Capabilities {
        val inputArray = if (split) split0xB826hex(input) else listOf(input)
        val list = mutableListWithCapacity<Capabilities>(inputArray.size)

        for (it in inputArray) {
            try {
                val inputStream = it.preformatHex().decodeHex().inputStream()
                list.add(Import0xB826.parse(inputStream))
            } catch (err: IllegalArgumentException) {
                val errMessage = "Invalid hexdump"
                val multiHelp =
                    if (!split) {
                        "Use flag '--multiple0xB826' if you are parsing multiple 0xB826 hexdumps."
                    } else {
                        ""
                    }
                throw IllegalArgumentException(errMessage + multiHelp, err)
            }
        }

        val enDcCombos = list.flatMap(Capabilities::enDcCombos)
        val nrCombos = list.flatMap(Capabilities::nrCombos)
        val nrDcCombos = list.flatMap(Capabilities::nrDcCombos)

        return Capabilities(enDcCombos = enDcCombos, nrCombos = nrCombos, nrDcCombos = nrDcCombos)
    }
}
