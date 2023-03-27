package it.smartphonecombo.uecapabilityparser.extension

internal fun String.indexOf(regex: Regex): Int = regex.find(this)?.range?.first ?: -1

internal fun String.repeat(n: Int, separator: String) =
    plus(separator).repeat(n).dropLast(separator.length)
