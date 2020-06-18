package com.virtlink.collections.impl

import com.virtlink.N
import com.virtlink.collections.DisjointMap
import com.virtlink.collections.PersistentDisjointMap
import kotlinx.collections.immutable.*

/**
 * A persistent union-find map.
 *
 * @property _roots maps each key to its value. This map contains only entries
 * for those keys that are root keys.
 * @property _parents maps each key to its parent key. This map contains only entries
 * for those keys that are not root keys.
 * @property _ranks maps a key to its rank, which is the number of keys it represents, including itself.
 * This map contains only entries for those keys that have a rank greater than one.
 */
class PersistentUnionFindMap<K, V> private constructor(
    private val _roots: PersistentMap<K, V>,
    private var _parents: PersistentMap<K, K>,     // We replace this map when doing path compression.
    private var _ranks: PersistentMap<K, Int>      // We replace this map when doing path compression.
) : AbstractUnionFindMap<K, V>(), PersistentDisjointMap<K, V> {

    companion object {
        private val EMPTY = PersistentUnionFindMap<Nothing, Nothing>(
            persistentMapOf(),
            persistentMapOf(),
            persistentMapOf()
        )
        @Suppress("UNCHECKED_CAST")
        fun <K, V> emptyOf(): PersistentUnionFindMap<K, V> = EMPTY as PersistentUnionFindMap<K, V>
    }

    // Map<K, V>
    override val size: Int get() = keys.size

    override operator fun get(key: K): V? {
        val rep = find(key) ?: return null
        return this._roots[rep];
    }

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V): V {
        val rep = find(key) ?: return defaultValue
        return this._roots[rep] ?: defaultValue
    }


    // DisjointMap<K, V>
    override fun find(key: K): K? {
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        val rep = findMutable(key, this._roots, mutableParents, mutableRanks)

        this._parents = mutableParents.build()
        this._ranks = mutableRanks.build()

        return rep
    }

    override fun getComponent(key: K): DisjointMap.Component<K, V>? {
        TODO()
    }


    // ImmutableMap<K, V>
    private var _keys: ImmutableSet<K>? = null
    override val keys: ImmutableSet<K> get() {
        if (_keys == null) _keys = Keys()
        return _keys!!
    }

    private var _values: ImmutableCollection<V>? = null
    override val values: ImmutableCollection<V> get() {
        if (_values == null) _values = _roots.values
        return _values!!
    }

    private var _entries: ImmutableSet<Map.Entry<K, V>>? = null
    override val entries: ImmutableSet<Map.Entry<K, V>> get() {
        if (_entries == null) _entries = Entries()
        return _entries!!
    }


    // PersistentMap<K, V>
    override fun put(key: K, value: @UnsafeVariance V): PersistentDisjointMap<K, V> {
        TODO()
    }

    override fun remove(key: K): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        removeMutable(key, null, false, mutableRoots, mutableParents, mutableRanks) ?: return this

        return PersistentUnionFindMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun remove(key: K, value: @UnsafeVariance V): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        removeMutable(key, value, true, mutableRoots, mutableParents, mutableRanks) ?: return this

        return PersistentUnionFindMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun putAll(m: Map<out K, @UnsafeVariance V>): PersistentDisjointMap<K, V> {
        TODO()
    }

    override fun clear(): PersistentDisjointMap<K, V> {
        TODO()
    }

    override fun builder(): PersistentDisjointMap.Builder<K, @UnsafeVariance V> {
        TODO()
    }


    // PersistentDisjointMap<K, V>
    private var _components: ImmutableSet<DisjointMap.Component<K, V>>? = null
    override val components: ImmutableSet<DisjointMap.Component<K, V>> get() {
        if (_components == null) _components = Components()
        return _components!!
    }

    override fun setComponent(key: K, value: V): PersistentDisjointMap<K, V> {
        val rep = find(key)
        
        TODO()
    }

    override fun putComponent(component: DisjointMap.Component<K, V>): PersistentDisjointMap<K, V> {
        val keys = component.keys
        val value = component.value
        if (keys.isEmpty()) return this

        // Disunify all keys in the component
        val newMap = keys.fold<K, PersistentDisjointMap<K, V>>(this) { acc, key -> acc.disunion(key) }
        // At this point we know each key is in their own component or not in the map
        val rep = keys.first()
        // Unify all the keys into a new component with the component's value
        return keys.fold(newMap) { acc, key -> acc.union(key, rep, { value }) { _, _ -> value } }
    }

    override fun putAllComponents(components: Iterable<DisjointMap.Component<K, V>>): PersistentDisjointMap<K, V> {
        return components.fold<DisjointMap.Component<K, V>, PersistentDisjointMap<K, V>>(this) {
                acc, component -> acc.putComponent(component)
        }
//        val parents = persistentMapOf<K, K>().builder()
//        val values = persistentMapOf<K, V>().builder()
//        val ranks = persistentMapOf<K, Int>().builder()
//        for ((keys, value) in initial) {
//            if (keys.isEmpty()) continue
//            val rep = keys.first()
//            require(rep !in values && rep !in ranks) { "Element $rep was already in the map." }
//            values[rep] = value
//            ranks[rep] = keys.size
//            for (e in keys.drop(1)) {
//                require(e !in values && e !in ranks) { "Element $e was already in the map." }
//                parents[e] = rep
//            }
//        }
//        return PersistentUnionFindMap(
//            parents.build(),
//            values.build(),
//            ranks.build()
//        )
//
//        return PersistentUnionFindMap.of(initial)
    }

    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        unionMutable(key1, key2, default, unify, mutableRoots, mutableParents, mutableRanks)

        return PersistentUnionFindMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun disunion(key: K): PersistentDisjointMap<K, V> {
        val mutableRoots = this._roots.builder()
        val mutableParents = this._parents.builder()
        val mutableRanks = this._ranks.builder()

        val changed = disunionMutable(key, mutableRoots, mutableParents, mutableRanks)
        if (!changed) return this

        return PersistentUnionFindMap(mutableRoots.build(), mutableParents.build(), mutableRanks.build())
    }

    override fun removeKey(key: K): PersistentDisjointMap.Result<K, V, Pair<K?, V>?> {
        TODO()
    }

    override fun compute(key: K, mapping: (K, V?) -> V): PersistentDisjointMap.Result<K, V, V> {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        val newValue = mapping(rep, oldValue)
        val mutableRoots = this._roots.builder()

        setComponentMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(PersistentUnionFindMap(mutableRoots.build(), _parents, _ranks), newValue)
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): PersistentDisjointMap.Result<K, V, V?> {
        val rep = find(key) ?: key
        val oldValue = get(rep) ?: return PersistentDisjointMap.Result(this, null)
        val newValue = mapping(rep, oldValue)
        val mutableRoots = this._roots.builder()

        setComponentMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(PersistentUnionFindMap(mutableRoots.build(), _parents, _ranks), newValue)
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): PersistentDisjointMap.Result<K, V, V> {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        if (oldValue != null) return PersistentDisjointMap.Result(this, oldValue)
        val newValue = mapping(rep)
        val mutableRoots = this._roots.builder()

        setComponentMutableRep(rep, newValue, mutableRoots)

        return PersistentDisjointMap.Result(PersistentUnionFindMap(mutableRoots.build(), _parents, _ranks), newValue)
    }



    /**
     * Inner class used to give a view of the keys in the map.
     */
    private inner class Keys: AbstractSet<K>(), ImmutableSet<K> {

        override val size: Int
            get() = this@PersistentUnionFindMap._roots.size + this@PersistentUnionFindMap._parents.size

        override fun iterator(): Iterator<K> = iterator {
            yieldAll(this@PersistentUnionFindMap._roots.keys)
            yieldAll(this@PersistentUnionFindMap._parents.keys)
        }

    }


    /**
     * Inner class used to give a view of the entries in the map.
     */
    private inner class Entries: AbstractSet<Map.Entry<K, V>>(), ImmutableSet<Map.Entry<K, V>> {

        override val size: Int
            get() = this@PersistentUnionFindMap._roots.size + this@PersistentUnionFindMap._parents.size

        override fun iterator(): Iterator<Map.Entry<K, V>> = iterator {
            yieldAll(this@PersistentUnionFindMap.keys.asSequence().map {
                Entry(it, N.of(this@PersistentUnionFindMap[it]))
            })
        }

    }


    /**
     * Inner class used to give a view of the components in the map.
     */
    private inner class Components: AbstractSet<DisjointMap.Component<K, V>>(), ImmutableSet<DisjointMap.Component<K, V>> {

        override val size: Int
            get() = this@PersistentUnionFindMap._roots.size

        override fun iterator(): Iterator<DisjointMap.Component<K, V>> = iterator {
            yieldAll(this@PersistentUnionFindMap._roots.asSequence().map {
                buildComponent(it.key, it.value)
            })
        }

        private fun buildComponent(rep: K, value: V): Component<K, V> {
            val keys = (setOf(rep) union this@PersistentUnionFindMap._parents.keys.filter { find(it) == rep }).toImmutableSet()
            return Component(keys, value)
        }

    }


    /**
     * A key-value entry.
     */
    private data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        override fun toString(): String = "$key=$value"
    }

    /**
     * A component.
     */
    private data class Component<K, V>(override val keys: ImmutableSet<K>, override val value: V) : DisjointMap.Component<K, V> {
        override fun toString(): String = "$keys=$value"
    }

}