package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.io.BwClassSerializer
import kotlinx.serialization.Serializable

/**
 * Represents a bandwidth class, it's backed by a char.
 *
 * Use [valueOf] to create an instance of this class
 *
 * BwClass of '0' has a special meaning, it's [NONE]. BwClass of '?' has a special meaning, it's
 * [INVALID].
 */
@Serializable
@JvmInline
value class BwClass
private constructor(@Serializable(with = BwClassSerializer::class) private val bwClass: Char) :
    Comparable<BwClass> {
    companion object {
        val NONE = BwClass('0')
        val INVALID = BwClass('?')

        /**
         * Return a BwClass whose value is the first character (uppercase) of [s].
         *
         * If [s] is empty or null return [NONE].
         */
        fun valueOf(s: String?): BwClass {
            val char = s?.firstOrNull()?.uppercaseChar() ?: '0'

            return safeValueOf(char)
        }

        /**
         * Return a BwClass whose value is [i] + 0x40.
         *
         * If [i] = 0 return [NONE].
         */
        fun valueOf(i: Int): BwClass {
            val char = if (i != 0) (i + 0x40).toChar() else '0'

            return safeValueOf(char)
        }

        /**
         * Return a BwClass whose value is [i] + 0x41
         *
         * If [i] = 6 return [NONE].
         */
        fun valueOfMtkIndex(i: Int): BwClass {
            return when (i) {
                in 0 until 6 -> BwClass((i + 0x41).toChar())
                6 -> NONE
                else -> INVALID
            }
        }

        /** Return BwClass(char) for A-Z, [NONE] for '0', [INVALID] otherwise. */
        private fun safeValueOf(char: Char): BwClass {
            return when (char) {
                in 'A'..'Z' -> BwClass(char)
                '0' -> NONE
                else -> INVALID
            }
        }
    }

    override fun compareTo(other: BwClass): Int {
        return bwClass.compareTo(other.bwClass)
    }

    override fun toString(): String {
        return if (bwClass != NONE.bwClass) bwClass.toString() else ""
    }
}

fun String.toBwClass(): BwClass = BwClass.valueOf(this)
