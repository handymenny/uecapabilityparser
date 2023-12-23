@file:Suppress("NOTHING_TO_INLINE")

package it.smartphonecombo.uecapabilityparser.extension

internal inline fun Int.isEven() = this and 1 == 0

internal inline fun Int.isOdd() = !isEven()

internal fun Int.insert(value: Int, offset: Int): Int = this or (value shl offset)

internal fun Int.readNBits(count: Int): Int = this and ((1 shl count) - 1)

internal fun Int.readNBits(count: Int, offset: Int): Int =
    (this ushr offset) and ((1 shl count) - 1)
