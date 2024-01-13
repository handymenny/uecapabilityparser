package it.smartphonecombo.uecapabilityparser.util

class LruCache<K, V>(private val maxCapacity: Int? = null) {
    private val internalMap: LinkedHashMap<K, V>
    private val lock = Any()

    init {
        if (maxCapacity == null) {
            internalMap = LinkedHashMap()
        } else {
            internalMap =
                object : LinkedHashMap<K, V>(minOf(16, maxCapacity + 1), 0.75f, true) {
                    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
                        return size > maxCapacity
                    }
                }
        }
    }

    operator fun get(key: K): V? = internalMap[key]

    operator fun set(key: K, value: V) = put(key, value)

    fun put(key: K, value: V, skipIfFull: Boolean = false): Boolean {
        if (maxCapacity == 0 || skipIfFull && full()) return false
        synchronized(lock) { internalMap[key] = value }
        return true
    }

    private fun full(): Boolean = maxCapacity == internalMap.size
}
