package it.smartphonecombo.uecapabilityparser.model

data class ByteArrayDeepEquals(val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (other !is ByteArrayDeepEquals) return false

        return byteArray.contentEquals(other.byteArray)
    }

    override fun hashCode(): Int {
        return byteArray.contentHashCode()
    }
}
