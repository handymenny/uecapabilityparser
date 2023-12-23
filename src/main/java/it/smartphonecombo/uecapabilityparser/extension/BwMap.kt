package it.smartphonecombo.uecapabilityparser.extension

internal fun BwMap.merge(other: BwMap) {
    for ((key, value) in other.entries) {
        this[key] = this[key]?.plus(value) ?: value
    }
}
