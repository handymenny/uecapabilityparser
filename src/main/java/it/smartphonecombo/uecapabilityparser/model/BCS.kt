package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.util.WeakConcurrentHashMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface BCS : Comparable<BCS> {
    fun toCompactStr(): String

    companion object {
        // Cache used by fromBinaryString
        private val cacheBinary = WeakConcurrentHashMap<String, BCS>()
        // Cache used by fromQualcommCP
        private val cacheCP = WeakConcurrentHashMap<String, BCS>()

        /**
         * Converts the given binaryString to an instance of [BCS]
         * - If binaryString has no bit with value 1 return [EmptyBCS]
         * - If binaryString has only one bit with value 1 return [SingleBCS]
         * - if binaryString has 32 bits with value 1 return [AllBCS]
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
                    32 -> AllBCS
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

@Serializable
@SerialName("empty")
object EmptyBCS : BCS {
    override fun toCompactStr(): String = ""
    override fun toString(): String = ""
    override fun compareTo(other: BCS): Int {
        // EmptyBCS is below any BCS
        return if (other == this) 0 else -1
    }
}

@Serializable
@SerialName("all")
object AllBCS : BCS {
    override fun toCompactStr(): String = "mAll"
    override fun toString(): String = "all"
    override fun compareTo(other: BCS): Int {
        // AllBcs is above any BCS
        return if (other == this) 0 else 1
    }
}

@Serializable
@SerialName("single")
data class SingleBCS(@SerialName("value") private val bcs: Int) : BCS {
    override fun toCompactStr(): String = bcs.toString()
    override fun toString(): String = bcs.toString()
    override fun compareTo(other: BCS): Int {
        return when (other) {
            is SingleBCS -> bcs.compareTo(other.bcs)
            // SingleBCS is above EmptyBCS
            is EmptyBCS -> 1
            // SingleBCS is below MultiBCS and AllBCS
            else -> -1
        }
    }
}

@Serializable
@SerialName("multi")
data class MultiBCS(@SerialName("value") private val bcsArray: IntArray) : BCS {
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

    override fun compareTo(other: BCS): Int {
        return when (other) {
            // Compare bcs length
            is MultiBCS -> bcsArray.size.compareTo(other.bcsArray.size)
            // MultiBCS is below AllBCS
            is AllBCS -> -1
            // MultiBcs is above EmptyBCS, SingleBCS
            else -> 1
        }
    }
}
