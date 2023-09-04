package it.smartphonecombo.uecapabilityparser.model.bandwidth

import it.smartphonecombo.uecapabilityparser.util.WeakConcurrentHashMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Bandwidth : Comparable<Bandwidth> {
    fun average(): Double

    fun max(): Int

    override fun compareTo(other: Bandwidth): Int = average().compareTo(other.average())

    companion object {
        private val cacheInt = WeakConcurrentHashMap<Int, Bandwidth>()
        private val cacheIntList = WeakConcurrentHashMap<List<Int>, Bandwidth>()

        fun from(int: Int): Bandwidth {
            val cachedResult = cacheInt[int]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                if (int == 0) {
                    EmptyBandwidth
                } else {
                    SingleBandwidth(int)
                }

            cacheInt[int] = result
            return result
        }

        fun from(list: List<Int>): Bandwidth {
            val cachedResult = cacheIntList[list]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                if (list.isEmpty()) {
                    EmptyBandwidth
                } else if (list.size == 1 || list.distinct().count() == 1) {
                    from(list.first())
                } else {
                    MixedBandwidth(list.sortedDescending())
                }

            cacheIntList[list] = result
            return result
        }
    }
}

@Serializable
@SerialName("empty")
object EmptyBandwidth : Bandwidth {
    override fun toString(): String = ""

    override fun average(): Double = 0.toDouble()

    override fun max(): Int = 0
}

@Serializable
@SerialName("single")
data class SingleBandwidth(@SerialName("value") private val bandwidth: Int) : Bandwidth {
    override fun toString(): String = bandwidth.toString()

    override fun average(): Double = bandwidth.toDouble()

    override fun max(): Int = bandwidth
}

@Serializable
@SerialName("mixed")
data class MixedBandwidth(@SerialName("value") private val list: List<Int>) : Bandwidth {
    override fun toString(): String = list.joinToString(", ")

    override fun average(): Double = list.average()

    override fun max(): Int = list.max()
}

fun Int.toBandwidth(): Bandwidth = Bandwidth.from(this)
