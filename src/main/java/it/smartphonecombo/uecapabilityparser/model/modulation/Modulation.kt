package it.smartphonecombo.uecapabilityparser.model.modulation

import java.util.WeakHashMap

sealed interface Modulation : Comparable<Modulation> {
    fun average(): Double
    fun maxModulationOrder(): ModulationOrder
    override fun compareTo(other: Modulation): Int = average().compareTo(other.average())

    companion object {
        // pre-compute all SingleModulations
        private val singleModulations =
            ModulationOrder.values().map {
                // SingleModulation(ModulationOrder.NONE) == EmptyModulation
                if (it == ModulationOrder.NONE) {
                    EmptyModulation
                } else {
                    SingleModulation(it)
                }
            }
        private val cacheModulationArray = WeakHashMap<List<ModulationOrder>, Modulation>()

        fun from(modulationOrder: ModulationOrder) = singleModulations[modulationOrder.ordinal]

        fun from(modulationList: List<ModulationOrder>): Modulation {
            val cachedResult = cacheModulationArray[modulationList]
            if (cachedResult != null) {
                return cachedResult
            }

            val result =
                if (modulationList.isEmpty()) {
                    EmptyModulation
                } else if (modulationList.size == 1 || modulationList.distinct().count() == 1) {
                    from(modulationList.first())
                } else {
                    MixedModulation(modulationList.sortedDescending())
                }

            cacheModulationArray[modulationList] = result
            return result
        }
    }
}

object EmptyModulation : Modulation {
    override fun toString(): String = ModulationOrder.NONE.toString()
    override fun average(): Double = ModulationOrder.NONE.ordinal.toDouble()
    override fun maxModulationOrder(): ModulationOrder = ModulationOrder.NONE
}

data class SingleModulation(private val modulation: ModulationOrder) : Modulation {
    override fun toString(): String = modulation.toString()
    override fun average(): Double = modulation.ordinal.toDouble()
    override fun maxModulationOrder(): ModulationOrder = modulation
}

data class MixedModulation(private val list: List<ModulationOrder>) : Modulation {
    override fun toString(): String = list.joinToString(", ")
    override fun average(): Double = list.map(ModulationOrder::ordinal).average()
    override fun maxModulationOrder(): ModulationOrder = list.max()
}

fun ModulationOrder.toModulation(): Modulation = Modulation.from(this)
