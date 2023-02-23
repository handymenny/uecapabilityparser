package it.smartphonecombo.uecapabilityparser.bean.lte

data class CompactedCombo(
    val bands: Array<Pair<Int, Char>>,
    private val _mimo: MutableList<String> = ArrayList(),
    private val _upload: MutableList<String> = ArrayList(),
) {
    private var mimoSorted: Boolean = false
    private var uploadSorted: Boolean = false

    val mimo: List<String>
        get() {
            if (!mimoSorted) {
                _mimo.sortWith(naturalOrder<String>().reversed())
                mimoSorted = true
            }
            return _mimo
        }

    val upload: List<String>
        get() {
            if (!uploadSorted) {
                _upload.sortWith(naturalOrder<String>().reversed())
                uploadSorted = true
            }
            return _upload
        }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object.toString
     */
    override fun toString(): String {
        return "{" +
            "\"dl\":" +
            bandToString() +
            "," +
            "\"mimo\":" +
            listToString(mimo) +
            "," +
            "\"ul\":" +
            listToString(upload) +
            "}"
    }

    /**
     * Band to string.
     *
     * @return the string
     */
    private fun bandToString(): String {
        val max = bands.size - 1
        if (max == -1) {
            return "[]"
        }
        val b = StringBuilder()
        b.append("[\"")
        var i = 0
        while (true) {
            val band = bands[i]
            b.append(band.first).append(band.second)
            if (i == max) {
                return b.append("\"]").toString()
            }
            b.append("\",\"")
            i++
        }
    }

    /**
     * List to string.
     *
     * @param <E> the element type
     * @param a the a
     * @return the string </E>
     */
    private fun <E> listToString(a: List<E>?): String {
        if (a == null) {
            return "null"
        }
        val max = a.size - 1
        if (max == -1) {
            return "[]"
        }
        val b = StringBuilder()
        b.append('[')
        var i = 0
        while (true) {
            b.append('"')
            b.append(a[i])
            b.append('"')
            if (i == max) {
                break
            }
            b.append(',')
            i++
        }
        return b.append(']').toString()
    }

    fun addMimo(mimo: String) {
        if (!_mimo.contains(mimo)) {
            _mimo.add(mimo)
            mimoSorted = false
        }
    }

    fun addUpload(upload: String) {
        if (!_upload.contains(upload)) {
            _upload.add(upload)
            uploadSorted = false
        }
    }
}
