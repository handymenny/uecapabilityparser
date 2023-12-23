package it.smartphonecombo.uecapabilityparser.extension

internal fun String.indexOf(regex: Regex): Int = regex.find(this)?.range?.first ?: -1

internal fun String.repeat(n: Int, separator: String) =
    plus(separator).repeat(n).dropLast(separator.length)

@Throws(IllegalArgumentException::class)
internal fun String.decodeHex(): ByteArray {
    return ByteArray(length / 2) {
        val i = it * 2
        this[i + 1].digitToInt(16).insert(this[i].digitToInt(16), 4).toByte()
    }
}

internal fun String.preformatHex(): String {
    return filterNot(Char::isWhitespace).replace(",", "").replace("0x", "").let {
        if (it.length.isEven()) it else "${it}0"
    }
}

/**
 * Appends the given string before the last dot in the filename. If there isn't any dot, it appends
 * it to the end of the string.
 */
internal fun String.appendBeforeExtension(strToAppend: String): String {
    val split = split(".")

    if (split.size < 2) {
        return this + strToAppend
    }

    return split.dropLast(1).joinToString(".", postfix = "$strToAppend.${split.last()}")
}

internal fun List<String>.commonPrefix(ignoreCase: Boolean): String {
    val indexOfDiff = indexOfDiff(ignoreCase)

    return if (indexOfDiff > 0) first().substring(0, indexOfDiff) else ""
}

private fun List<String>.indexOfDiff(ignoreCase: Boolean): Int {
    if (isEmpty()) return 0
    if (size == 1) return this[0].length

    val minLen = minOf(String::length)

    for (charIndex in 0 until minLen) {
        val charFirst = this[0][charIndex]

        for (arrayIndex in 1 until size) {
            val charCurrent = this[arrayIndex][charIndex]
            if (!charCurrent.equals(charFirst, ignoreCase)) {
                return charIndex
            }
        }
    }

    return minLen
}
