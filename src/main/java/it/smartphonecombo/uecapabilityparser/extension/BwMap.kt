package it.smartphonecombo.uecapabilityparser.extension

fun MutableBwMap.merge(other: BwMap) {
    for ((key, value) in other.entries) {
        this[key] = this[key]?.plus(value) ?: value
    }
}
