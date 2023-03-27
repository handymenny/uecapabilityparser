package it.smartphonecombo.uecapabilityparser.extension

/**
 * Return:
 * - 1 if [string] contains "one"
 * - 2 if [string] contains "two"
 * - 4 if [string] contains "four"
 * - 8 if [string] contains "eight"
 * - 0 otherwise
 */
internal fun MimoCompanion.fromLiteral(string: String?): Mimo {
    return when {
        string == null -> 0
        "one" in string -> 1
        "two" in string -> 2
        "four" in string -> 4
        "eight" in string -> 8
        else -> 0
    }
}
