package it.smartphonecombo.uecapabilityparser.model

import java.util.WeakHashMap

sealed interface Mimo : Comparable<Mimo> {
    fun toCompactStr(): String
    fun average(): Double
    override fun compareTo(other: Mimo): Int = average().compareTo(other.average())

    companion object {
        private val cacheInt = WeakHashMap<Int, Mimo>()
        private val cacheIntArray = WeakHashMap<IntArray, Mimo>()

        fun from(int: Int): Mimo {
            val cachedResult = cacheInt[int]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                if (int == 0) {
                    EmptyMimo
                } else if (int > 10) {
                    from(int.toString().map(Char::digitToInt).toIntArray())
                } else {
                    SingleMimo(int)
                }

            cacheInt[int] = result
            return result
        }

        fun from(intArray: IntArray): Mimo {
            val cachedResult = cacheIntArray[intArray]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                if (intArray.isEmpty()) {
                    EmptyMimo
                } else if (intArray.size == 1 || intArray.distinct().count() == 1) {
                    SingleMimo(intArray.first())
                } else {
                    MixedMimo(intArray.sortedArrayDescending())
                }

            cacheIntArray[intArray] = result
            return result
        }
    }
}

object EmptyMimo : Mimo {
    override fun toCompactStr(): String = ""
    override fun toString(): String = ""
    override fun average(): Double = 0.0
}

private data class SingleMimo(private val mimo: Int) : Mimo {
    override fun toCompactStr(): String = mimo.toString()
    override fun toString(): String = mimo.toString()
    override fun average(): Double = mimo.toDouble()
}

private data class MixedMimo(private val mimoArray: IntArray) : Mimo {
    override fun toCompactStr(): String = mimoArray.joinToString("")
    override fun toString(): String = mimoArray.joinToString(", ")
    override fun average(): Double = mimoArray.average()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MixedMimo) return false

        return mimoArray.contentEquals(other.mimoArray)
    }

    override fun hashCode(): Int = mimoArray.contentHashCode()
}

fun Int.toMimo(): Mimo = Mimo.from(this)

/**
 * Return:
 * - 1 if [string] contains "one"
 * - 2 if [string] contains "two"
 * - 4 if [string] contains "four"
 * - 8 if [string] contains "eight"
 * - 0 otherwise
 */
internal fun Int.Companion.fromLiteral(string: String?): Int {
    return when {
        string == null -> 0
        "one" in string -> 1
        "two" in string -> 2
        "four" in string -> 4
        "eight" in string -> 8
        else -> 0
    }
}
