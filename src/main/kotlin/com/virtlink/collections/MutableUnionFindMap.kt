package com.virtlink.collections

import com.virtlink.N
import com.virtlink.collections.MutableDisjointMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import java.lang.IllegalArgumentException

/**
 * A mutable union-find map.
 *
 * @property _roots maps each key to its value. This map contains only entries
 * for those keys that are root keys.
 * @property _parents maps each key to its parent key. This map contains only entries
 * for those keys that are not root keys.
 * @property _ranks maps a key to its rank, which is the number of keys it represents, including itself.
 * This map contains only entries for those keys that have a rank greater than one.
 */
class MutableUnionFindMap<K, V> private constructor(
    private val _roots: MutableMap<K, V>,
    private val _parents: MutableMap<K, K>,
    private val _ranks: MutableMap<K, Int>
): MutableDisjointMap<K, V> {

    constructor(): this(mutableMapOf(), mutableMapOf(), mutableMapOf())

    override val size: Int get() = _roots.size + _parents.size

    override operator fun get(key: K): V? {
        val rep = find(key) ?: return null
        return this._roots[rep]
    }

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V): V {
        val rep = find(key) ?: return defaultValue
        return this._roots[rep] ?: defaultValue
    }

    override fun find(key: K): K? {
        return findMutable(key, this._roots, this._parents, this._ranks)
    }

    override fun getSetSize(key: K): Int {
        val rep = find(key) ?: return 0
        return _ranks[rep] ?: 1
    }

    override operator fun set(key: K, value: V): V? {
        return setMutable(key, value, this._roots, this._parents, this._ranks)
    }

    override fun remove(key: K): V? {
        val (_, oldValue) = removeMutable(key, null, false, this._roots, this._parents, this._ranks) ?: return null
        return oldValue
    }

    override fun clear() {
        _roots.clear()
        _parents.clear()
        _ranks.clear()
    }

    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V) {
        unionMutable(key1, key1, default, unify, this._roots, this._parents, this._ranks)
    }

    override fun disunion(key: K) {
        disunionMutable(key, this._roots, this._parents, this._ranks)
    }

    override fun compute(key: K, mapping: (K, V?) -> V): V {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        val newValue = mapping(rep, oldValue)
        setMutableRep(rep, newValue, this._roots)
        return newValue
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
        val rep = find(key) ?: key
        val oldValue = get(rep) ?: return null
        val newValue = mapping(rep, oldValue)
        setMutableRep(rep, newValue, this._roots)
        return newValue
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        if (oldValue != null) return oldValue
        val newValue = mapping(rep)
        setMutableRep(rep, newValue, this._roots)
        return newValue
    }

    override fun toMap(): MutableMap<Set<K>, V> {
        // Maps each representative key to a set of keys
        val mapping = mutableMapOf<K, MutableSet<K>>()
        this._roots.keys.forEach { k -> mapping[k] = mutableSetOf(k) }
        this._parents.keys.forEach { k -> mapping[find(k)]!!.add(k) }

        return mapping.map { (rep, keys) -> keys.toMutableSet() to N.of(this._roots[rep]) }
            .toMap<Set<K>, V>().toMutableMap()
    }

}