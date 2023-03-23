package it.smartphonecombo.uecapabilityparser.extension

/** Convert BW index to BW Class */
internal fun Int.toBwClass(): Char {
    if (this <= 0) {
        return '0'
    }
    return (this + 0x40).toChar()
}
