@file:Suppress("NOTHING_TO_INLINE")

package it.smartphonecombo.uecapabilityparser.extension

import java.util.Collections
import java.util.Enumeration
import kotlin.collections.ArrayList

internal fun <E : Comparable<E>> Array<E>.indexOfMin(): Int {
    if (isEmpty()) {
        return -1
    }
    var index = 0
    var min = this[0]

    for (i in 1 until size) {
        if (this[i] < min) {
            min = this[i]
            index = i
        }
    }

    return index
}

internal fun IntArray.indexOfMin(): Int {
    if (isEmpty()) {
        return -1
    }
    var index = 0
    var min = this[0]

    for (i in 1 until size) {
        if (this[i] < min) {
            min = this[i]
            index = i
        }
    }

    return index
}

internal inline operator fun <T> List<T>.component6(): T = get(5)

internal inline operator fun <T> List<T>.component7(): T = get(6)

internal inline fun <T> mutableListWithCapacity(capacity: Int): MutableList<T> = ArrayList(capacity)

/** Return this list as List<[T]>. It's an unchecked cast, no check is done. */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified T : Any> List<*>.typedList() = this as List<T>

internal inline fun <T> List<T>.toEnumeration(): Enumeration<T> = Collections.enumeration(this)

internal fun BwMap.merge(other: BwMap) {
    for ((key, value) in other.entries) {
        this[key] = this[key]?.plus(value) ?: value
    }
}

internal fun <T> List<T>.trimToSize() {
    if (this is ArrayList) {
        this.trimToSize()
    }
}
