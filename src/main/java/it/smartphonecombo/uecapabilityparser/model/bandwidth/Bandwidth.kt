package it.smartphonecombo.uecapabilityparser.model.bandwidth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Bandwidth : Comparable<Bandwidth> {
    fun average(): Double

    fun max(): Int

    override fun compareTo(other: Bandwidth): Int = average().compareTo(other.average())

    companion object {
        fun from(int: Int): Bandwidth {
            val result =
                if (int == 0) {
                    EmptyBandwidth
                } else {
                    SingleBandwidth(int)
                }

            return result
        }

        fun from(list: List<Int>): Bandwidth {
            val result =
                if (list.isEmpty()) {
                    EmptyBandwidth
                } else if (list.size == 1 || list.distinct().count() == 1) {
                    from(list.first())
                } else {
                    MixedBandwidth(list.sortedDescending())
                }

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

@Serializable
@SerialName("invalid")
object InvalidBandwidth : Bandwidth {
    override fun toString(): String = "?"

    override fun average(): Double = 0.0

    override fun max(): Int = 0
}

fun Int.toBandwidth(): Bandwidth = Bandwidth.from(this)
