package it.smartphonecombo.uecapabilityparser.util

import java.util.WeakHashMap

/**
 * This map is backed by [WeakHashMap]. Write operations are protected by a shared lock. [entries],
 * [keys], [values] aren't views, but copies.
 */
class WeakConcurrentHashMap<K : Any, V : Any> : MutableMap<K, V> {
    private val internalMap = WeakHashMap<K, V>()
    private val writeLock = Any()

    /** A copy of [WeakHashMap.entries] */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = internalMap.entries.toMutableSet()

    /** A copy of [WeakHashMap.keys] */
    override val keys: MutableSet<K>
        get() = internalMap.keys.toMutableSet()
    override val size: Int
        get() = internalMap.size

    /** A copy of [WeakHashMap.values] */
    override val values: MutableCollection<V>
        get() = internalMap.values.toMutableList()

    override fun isEmpty(): Boolean = internalMap.isEmpty()

    override fun get(key: K): V? = internalMap[key]

    override fun containsValue(value: V): Boolean = internalMap.containsValue(value)

    override fun containsKey(key: K): Boolean = internalMap.containsKey(key)

    override fun clear() = synchronized(writeLock) { internalMap.clear() }

    override fun remove(key: K): V? {
        synchronized(writeLock) {
            return internalMap.remove(key)
        }
    }

    override fun putAll(from: Map<out K, V>) {
        synchronized(writeLock) {
            return internalMap.putAll(from)
        }
    }

    override fun put(key: K, value: V): V? {
        synchronized(writeLock) {
            return internalMap.put(key, value)
        }
    }
}
