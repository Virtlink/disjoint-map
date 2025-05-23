package net.pelsmaeker.collections

import kotlinx.collections.immutable.*
import kotlin.collections.plus

/**
 * A persistent union-find map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @property _roots Maps each key to its value. This map contains only entries
 * for those keys that are root keys.
 * @property _parents Maps each key to its parent key. This map contains only entries
 * for those keys that are not root keys.
 * @property _ranks Maps a key to its rank, which is the number of keys it represents, including itself.
 * This map contains only entries for those keys that have a rank greater than one.
 */
class PersistentUnionFindMap<K, V> internal constructor(
    private val _roots: PersistentMap<K, V>,
    private var _parents: PersistentMap<K, K>,     // We replace this map when doing path compression.
    private var _ranks: PersistentMap<K, Int>      // We replace this map when doing path compression.
) : PersistentDisjointMap<K, V> {

    companion object {
        private val EMPTY = PersistentUnionFindMap<Nothing, Nothing>(
            persistentMapOf(),
            persistentMapOf(),
            persistentMapOf()
        )
        @Suppress("UNCHECKED_CAST")
        fun <K, V> emptyOf(): PersistentUnionFindMap<K, V> = EMPTY as PersistentUnionFindMap<K, V>
    }

    override val size: Int get() = _roots.size + _parents.size

    override val keys: Set<K>
        get() = _roots.keys + _parents.keys

    override val values: Collection<V>
        get() = _roots.values

    override operator fun get(key: K): V? {
        val rep = find(key) ?: return null
        return this._roots[rep]
    }

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V): V {
        val rep = find(key) ?: return defaultValue
        @Suppress("UNCHECKED_CAST")
        // We know the key is in the map,
        // so this only returns `null` if the value in the map is `null`.
        return this._roots[rep] as V
    }

    override fun find(key: K): K? {
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        val rep = findMutable(key, this._roots, mutableParents, mutableRanks)

        this._parents = mutableParents.build()
        this._ranks = mutableRanks.build()

        return rep
    }

    override operator fun set(key: K, value: V): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        setMutable(key, value, mutableRoots, mutableParents, mutableRanks)

        return buildMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun remove(key: K): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        removeMutable(key, mutableRoots, mutableParents, mutableRanks) ?: return this

        return buildMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun clear(): PersistentDisjointMap<K, V> {
        return emptyOf()
    }

    override fun union(key1: K, key2: K, default: () -> V, compare: Comparator<K>, unify: (V, V) -> V): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        val changed = unionMutable(key1, key2, default, compare, unify, mutableRoots, mutableParents, mutableRanks)
        if (!changed) return this

        return buildMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun disunion(key: K): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        val success = disunionMutable(key, mutableRoots, mutableParents, mutableRanks)
        if (!success) throw NoSuchElementException()

        return buildMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun compute(key: K, mapping: (K, V?) -> V): PersistentDisjointMap.Result<K, V, V> {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        val newValue = mapping(rep, oldValue)
        val mutableRoots = this._roots.builder()

        setMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(buildMap(mutableRoots.build(), _parents, _ranks), newValue)
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): PersistentDisjointMap.Result<K, V, V?> {
        val rep = find(key) ?: key
        val oldValue = get(rep) ?: return PersistentDisjointMap.Result(this, null)
        val newValue = mapping(rep, oldValue)
        val mutableRoots = this._roots.builder()

        setMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(buildMap(mutableRoots.build(), _parents, _ranks), newValue)
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): PersistentDisjointMap.Result<K, V, V> {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        if (oldValue != null) return PersistentDisjointMap.Result(this, oldValue)
        val newValue = mapping(rep)
        val mutableRoots = this._roots.builder()

        setMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(buildMap(mutableRoots.build(), _parents, _ranks), newValue)
    }

    override fun toMap(): PersistentMap<Set<K>, V> {
        return toMapImpl(this._roots, this._parents, this._ranks).toPersistentMap()
    }

    /**
     * Builds a new map of the given inner map builders.
     *
     * @param newRoots the new roots
     * @param newParents the new parents
     * @param newRanks the new ranks
     * @return the new map; or the same map if nothing changed
     */
    private fun buildMap(
        newRoots: PersistentMap<K, V>,
        newParents: PersistentMap<K, K>,
        newRanks: PersistentMap<K, Int>
    ): PersistentUnionFindMap<K, V> {
        if (newRoots === this._roots && newParents === this._parents && newRanks === this._ranks) return this
        return PersistentUnionFindMap(newRoots, newParents, newRanks)
    }

    override fun builder(): PersistentDisjointMap.Builder<K, V> {
        return Builder(this)
    }

    private class Builder<K, V> constructor(
        private var currentMap: PersistentDisjointMap<K, V>
    ): PersistentDisjointMap.Builder<K, V> {

        override val size: Int
            get() = currentMap.size

        override val keys: Set<K>
            get() = currentMap.keys

        override val values: Collection<V>
            get() = currentMap.values

        override fun isEmpty() = currentMap.isEmpty()

        override fun get(key: K): V? = currentMap.get(key)

        override fun getOrDefault(key: K, defaultValue: V): V = currentMap.getOrDefault(key, defaultValue)

        override fun find(key: K): K? = currentMap.find(key)

        override fun contains(key: K): Boolean = currentMap.contains(key)

        override fun same(key1: K, key2: K): Boolean = currentMap.same(key1, key2)

        override fun set(key: K, value: V): V? {
            val oldValue = get(key)
            currentMap = currentMap.set(key, value)
            return oldValue
        }

        override fun remove(key: K): V? {
            val oldValue = get(key)
            currentMap = currentMap.remove(key)
            return oldValue
        }

        override fun clear() {
            currentMap = currentMap.clear()
        }

        override fun union(key1: K, key2: K, default: () -> V, compare: Comparator<K>, unify: (V, V) -> V) {
            currentMap = currentMap.union(key1, key2, default, compare, unify)
        }

        override fun disunion(key: K) {
            currentMap = currentMap.disunion(key)
        }

        override fun compute(key: K, mapping: (K, V?) -> V): V {
            val (newMap, value) = currentMap.compute(key, mapping)
            currentMap = newMap
            return value
        }

        override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
            val (newMap, value) = currentMap.computeIfPresent(key, mapping)
            currentMap = newMap
            return value
        }

        override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
            val (newMap, value) = currentMap.computeIfAbsent(key, mapping)
            currentMap = newMap
            return value
        }

        override fun toMap(): MutableMap<Set<K>, V> {
            return currentMap.toMap().toMutableMap()
        }

        override fun build(): PersistentDisjointMap<K, V> {
            return this.currentMap
        }

    }

}

