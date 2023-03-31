package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import java.util.WeakHashMap

sealed interface BCS {
    fun toCompactStr(): String

    companion object {
        // Cache used by fromBinaryString
        private val cacheBinary = WeakHashMap<String, BCS>()
        // Cache used by fromQualcommCP
        private val cacheCP = WeakHashMap<String, BCS>()

        /**
         * Converts the given binaryString to an instance of [BCS]
         * - If binaryString has no bit with value 1 return [EmptyBCS]
         * - If binaryString has only one bit with value 1 return [SingleBCS]
         * - otherwise it returns a [MultiBCS]
         */
        fun fromBinaryString(binaryString: String): BCS {
            val cachedResult = cacheBinary[binaryString]
            if (cachedResult != null) {
                return cachedResult
            }

            val bcsList = mutableListWithCapacity<Int>(binaryString.length)
            for (x in binaryString.indices) {
                if (binaryString[x] == '1') {
                    bcsList.add(x)
                }
            }

            val result =
                when (bcsList.size) {
                    0 -> EmptyBCS
                    1 -> SingleBCS(bcsList.first())
                    else -> MultiBCS(bcsList.toIntArray())
                }
            cacheBinary[binaryString] = result
            return result
        }

        /**
         * Convert the given bcsString to an instance of [BCS]
         * - If bcsString is empty return [EmptyBCS]
         * - If bcsString is empty or "mAll" return [AllBCS]
         * - If bcsString starts with m return [MultiBCS]
         * - otherwise it returns a [SingleBCS]
         */
        @Throws(NumberFormatException::class)
        fun fromQualcommCP(bcsString: String): BCS {
            val cachedResult = cacheCP[bcsString]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                when {
                    bcsString.isEmpty() -> EmptyBCS
                    bcsString == "mAll" -> AllBCS
                    bcsString.startsWith('m') -> {
                        val number = bcsString.substring(1).toInt(16)
                        val bcsBinaryString = Integer.toBinaryString(number)
                        fromBinaryString(bcsBinaryString)
                    }
                    else -> SingleBCS(bcsString.toInt())
                }
            cacheCP[bcsString] = result
            return result
        }
    }
}

object EmptyBCS : BCS {
    override fun toCompactStr(): String = ""
    override fun toString(): String = ""
}

object AllBCS : BCS {
    override fun toCompactStr(): String = "mAll"
    override fun toString(): String = "all"
}

data class SingleBCS(private val bcs: Int) : BCS {
    override fun toCompactStr(): String = bcs.toString()
    override fun toString(): String = bcs.toString()
}

data class MultiBCS(private val bcsArray: IntArray) : BCS {
    private var compactText: String? = null
    override fun toString(): String = bcsArray.joinToString(", ")

    private fun calculateCompactStr(): String {
        var count = 0
        for (i in bcsArray) {
            count += 1 shl i
        }
        val hex = count.toString(16).uppercase()
        val result = "m$hex"
        // Cache result
        compactText = result
        return result
    }

    override fun toCompactStr(): String {
        return compactText ?: calculateCompactStr()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MultiBCS) return false

        if (!bcsArray.contentEquals(other.bcsArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return bcsArray.contentHashCode()
    }
}
