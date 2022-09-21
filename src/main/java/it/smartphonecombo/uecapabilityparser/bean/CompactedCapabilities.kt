package it.smartphonecombo.uecapabilityparser.bean

import it.smartphonecombo.uecapabilityparser.bean.lte.CompactedCombo

// TODO: Auto-generated Javadoc
/**
 * The Class CompactedComboList.
 */
data class CompactedCapabilities(
    /**
     * The flags.
     */
    val flags: Int = 0,
    val combos: Array<CompactedCombo>? = null
) {

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object.toString
     */
    override fun toString(): String {
        return ("{" + "\"combo\":" + combosToString()
                + "," + "\"flags\":" + flags + "}")
    }

    fun combosToString(): String {
        if (combos == null) {
            return "null"
        }
        val max = combos.size - 1
        if (max == -1) {
            return "[]"
        }
        val b = StringBuilder()
        b.append('[')
        var i = 0
        while (true) {
            b.append(combos[i])
            if (i == max) {
                return b.append(']').toString()
            }
            b.append(',')
            i++
        }
    }
}