package it.smartphonecombo.uecapabilityparser.extension

internal infix fun IntRange.step(next: (Int) -> Int) =
    generateSequence(first, next).takeWhile { if (first < last) it <= last else it >= last }
