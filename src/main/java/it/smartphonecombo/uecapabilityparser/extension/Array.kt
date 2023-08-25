package it.smartphonecombo.uecapabilityparser.extension

fun <E : Comparable<E>> Array<E>.indexOfMin(): Int {
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
