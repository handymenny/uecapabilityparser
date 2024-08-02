package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

open class InternMap<T>(maxCapacity: Int) {
    @Transient private val lock = Any()

    private val internalMap: LinkedHashMap<T, T> =
        object : LinkedHashMap<T, T>(computeInitialCapacity(maxCapacity)) {
            override fun removeEldestEntry(eldest: Map.Entry<T, T>): Boolean {
                return size > maxCapacity
            }
        }

    private fun put(value: T): T {
        synchronized(lock) { internalMap[value] = value }
        return value
    }

    fun intern(value: T): T = internalMap[value] ?: put(value)

    fun contains(value: T): Boolean = internalMap.contains(value)

    fun size() = internalMap.size

    companion object {
        private fun computeInitialCapacity(maxCapacity: Int): Int {
            // A value that ensures no re-hash is maxCapacity / 0.75 + 1
            // Compute that value / 2
            val initialCapacity = Math.floorDiv(maxCapacity * 2, 3) + 1

            return maxOf(16, initialCapacity)
        }
    }
}

private object IBandDetailsInternMap : InternMap<IBandDetails>(1000)

private object IComponentInternMap : InternMap<IComponent>(10000)

private object IComboInternMap : InternMap<ICombo>(100000)

internal fun IBandDetails.intern() = IBandDetailsInternMap.intern(this)

internal fun IComponent.intern() = IComponentInternMap.intern(this)

internal fun ICombo.intern() = IComboInternMap.intern(this)

internal fun ICombo.alreadyInterned() = IComboInternMap.contains(this)
