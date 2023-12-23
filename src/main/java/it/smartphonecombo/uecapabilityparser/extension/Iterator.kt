package it.smartphonecombo.uecapabilityparser.extension

/**
 * Return the first element matching the given predicate or null if not found.
 *
 * NB: This function will update iterator cursor.
 */
internal inline fun Iterator<String>.firstOrNull(predicate: (String) -> Boolean): String? {
    for (item in this) {
        if (predicate(item)) {
            return item
        }
    }
    return null
}

internal infix fun IntRange.step(next: (Int) -> Int) =
    generateSequence(first, next).takeWhile { if (first < last) it <= last else it >= last }
