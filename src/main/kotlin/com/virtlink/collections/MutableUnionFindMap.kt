package com.virtlink.collections

import com.virtlink.N
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap

/**
 * A mutable union-find map.
 *
 * @property _values maps each key to its value. This map contains only entries
 * for those keys that are root keys.
 * @property _parents maps each key to its parent key. This map contains only entries
 * for those keys that are not root keys.
 * @property _ranks maps a key to its rank, which is the number of keys it represents, including itself.
 * This map contains only entries for those keys that have a rank greater than one.
 */
class MutableUnionFindMap<K, V> private constructor(
    private val _values: MutableMap<K, V>,
    private val _parents: MutableMap<K, K>,
    private val _ranks: MutableMap<K, Int>
): UnionFindMap<K, V>(), MutableDisjointMap<K, V> {

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")
    override val components: ImmutableMap<Set<K>, V>
        get() = TODO("Not yet implemented")
    override val size: Int
        get() = this.keys.size

    override fun get(key: K): V? {
        val rep = find(key) ?: return null
        return this._values[rep];
    }

    override fun clear() {
        this._values.clear()
        this._parents.clear()
        this._ranks.clear()
    }

    override fun put(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    override fun putAll(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V) {
        TODO("Not yet implemented")
    }

    override fun disunion(key: K, component: K) {
        TODO("Not yet implemented")
    }

    override fun setComponent(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override fun removeKey(key: K): Pair<K?, V> {
        TODO("Not yet implemented")
    }

    override fun compute(key: K, mapping: (K, V?) -> V): V {
        TODO("Not yet implemented")
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
        TODO("Not yet implemented")
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
        TODO("Not yet implemented")
    }

    override fun find(key: K): K {
        TODO("Not yet implemented")
    }


}