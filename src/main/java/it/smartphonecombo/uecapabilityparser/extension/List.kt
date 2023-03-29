@file:Suppress("NOTHING_TO_INLINE")

package it.smartphonecombo.uecapabilityparser.extension

internal inline operator fun <T> List<T>.component6(): T = get(5)

internal inline operator fun <T> List<T>.component7(): T = get(6)

/** Return this list as List<[T]>. It's an unchecked cast, no check is done. */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified T : Any> List<*>.typedList() = this as List<T>
