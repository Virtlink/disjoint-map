package com.virtlink.collections

import com.virtlink.N
import com.virtlink.N.assumeNotNull
import kotlinx.collections.immutable.*

/**
 * Union-find map.
 *
 * @property _values maps each key to its value. This map contains only entries
 * for those keys that are root keys.
 * @property _parents maps each key to its parent key. This map contains only entries
 * for those keys that are not root keys.
 * @property _ranks maps a key to its rank, which is the number of keys it has under it. This map contains only entries
 * for those keys that have a rank greater than zero.
 */
class UnionFindMap<K, V> private constructor(
    private val _values: PersistentMap<K, V>,
    private var _parents: PersistentMap<K, K>,     // We replace this map when doing path compression.
    private var _ranks: PersistentMap<K, Int>      // We replace this map when doing path compression.
): PersistentDisjointMap<K, V> {

    companion object {
        private val EMPTY = UnionFindMap<Nothing, Nothing>(
            persistentMapOf(),
            persistentMapOf(),
            persistentMapOf()
        )

        /**
         * Returns an empty persistent union-find map.
         */
        @Suppress("UNCHECKED_CAST")
        fun <K, V> emptyOf(): UnionFindMap<K, V> = EMPTY as UnionFindMap<K, V>
    }

    override val size: Int
        get() = _parents.size + _values.size

    override val keys: ImmutableSet<K> = Keys()
    override val values: ImmutableCollection<V> = Values()
    override val entries: ImmutableSet<Map.Entry<K, V>> = Entries()

    override fun isEmpty(): Boolean = size == 0

    override fun getComponent(key: K): Set<K> {
        val rep = find(key)
        return this.keys.filter { find(it) == rep }.ifEmpty { listOf(key) }.toSet()
    }

    override fun getComponentSize(key: K): Int {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        val rep = find(key)
        return this._values[rep]
    }

    override fun put(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun putAll(m: Map<out K, V>): PersistentDisjointMap<K, V> {
//        val parents = this._parents.builder()
//        val values = this._values.builder()
//        val ranks = this._ranks.builder()
//
//        for ((elements, v) in m) {
//            if (elements.isEmpty()) continue
//            val rep = elements.first()
//            require(rep !in values && rep !in ranks) { "Element $rep was already in the map." }
//            values[rep] = v
//            ranks[rep] = elements.size
//            for (e in elements.drop(1)) {
//                require(e !in values && e !in ranks) { "Element $e was already in the map." }
//                parents[e] = rep
//            }
//        }
//        return UnionFindMap(
//            parents.build(),
//            values.build(),
//            ranks.build()
//        )
    }

    override fun remove(key: K): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun remove(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun clear(): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun putComponent(keys: Set<K>, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun disunion(key: K): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun setComponent(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun removeKey(key: K): PersistentDisjointMap.Result<K, V, Pair<K?, V>?> {
        TODO("Not yet implemented")
    }

    override fun compute(key: K, mapping: (K, V?) -> V): PersistentDisjointMap.Result<K, V, V> {
        TODO("Not yet implemented")
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): PersistentDisjointMap.Result<K, V, V?> {
        TODO("Not yet implemented")
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): PersistentDisjointMap.Result<K, V, V> {
        TODO("Not yet implemented")
    }

    override fun builder(): PersistentDisjointMap.Builder<K, V> {
        TODO("Not yet implemented")
    }

    override val components: ImmutableMap<Set<K>, V>
        get() = TODO("Not yet implemented")

    override fun find(key: K): K {
        TODO("Not yet implemented")
    }

    override fun same(key1: K, key2: K): Boolean {
        val repA = find(key1)
        val repB = find(key2)
        return repA == repB
    }

    override fun containsKey(key: K): Boolean {
        return this.keys.contains(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.values.contains(value)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Set<*>) return false
        return setEquals(this.entries, other)
    }

    override fun hashCode(): Int = setHashCode(this.entries)

    override fun toString(): String = listToString(this.entries)

    /**
     * Finds the representative key for the given key
     * and performs path compression.
     *
     * @param key the key for which to find the representative key
     * @return the representative key, or the given key when it's
     * its own representative; or `null` when the key was not found
     */
    private fun findRepresentative(key: K): K? {
        // Is the key its own representative?
        if (this._values.containsKey(key)) return key
        // If not, do we know the parent key of this key?
        val parent = this._parents[key] ?: return null
        assert(parent != key) { "The representative was found in the parents map, which is incorrect." }
        // TODO: Can we do this iteratively, to avoid updating the parents and ranks maps so much?
        // Find the representative of the parent key
        val representative = assumeNotNull(findRepresentative(parent))
        if (parent != representative) {
            // Path compression:
            // Update the key to point directly to the representative key
            this._parents = this._parents.put(key, representative)
            this._ranks = this._ranks.builder().apply {
                // Remove one from the rank of the parent
                compute(parent) { _, oldRank ->
                    val newRank = (oldRank ?: 0) - 1
                    if (newRank >= 1) newRank else null
                }
                // Add one to the rank of the new parent
                compute(representative) { _, oldRank -> (oldRank ?: 0) + 1 }
            }.build()
        }
        return representative
    }

    /**
     * Inner class used to give a view of the entries in the map.
     */
    private inner class Entries: ImmutableSet<Map.Entry<K, V>> {

        override val size: Int
            get() = this@UnionFindMap.size

        override fun isEmpty(): Boolean = this.size == 0

        override fun contains(element: Map.Entry<K, V>): Boolean {
            val value = this@UnionFindMap[element.key]
            return value == element.value
        }

        override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean {
            return elements.all { contains(it) }
        }

        override fun iterator(): Iterator<Map.Entry<K, V>> = iterator {
            yieldAll(this@UnionFindMap.keys.asSequence().map {
                Entry(it, N.of(this@UnionFindMap[it]))
            })
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Set<*>) return false
            return setEquals(this, other)
        }

        override fun hashCode(): Int = setHashCode(this)

        override fun toString(): String = listToString(this)

    }

    /**
     * Inner class used to give a view of the keys in the map.
     */
    private inner class Keys: ImmutableSet<K> {

        override val size: Int
            get() = this@UnionFindMap.size

        override fun isEmpty(): Boolean = this.size == 0

        override fun contains(element: K): Boolean {
            // @formatter:off
            return this@UnionFindMap._values.containsKey(element)
                || this@UnionFindMap._parents.containsKey(element)
            // @formatter:on
        }

        override fun containsAll(elements: Collection<K>): Boolean {
            return elements.all { contains(it) }
        }

        override fun iterator(): Iterator<K> = iterator {
            yieldAll(this@UnionFindMap._values.keys)
            yieldAll(this@UnionFindMap._parents.keys)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Set<*>) return false
            return setEquals(this, other)
        }

        override fun hashCode(): Int = setHashCode(this)

        override fun toString(): String = listToString(this)
    }

    /**
     * Inner class used ot give a view of the values in the map.
     */
    private inner class Values: ImmutableCollection<V> {

        override val size: Int
            get() = this@UnionFindMap._values.size

        override fun contains(element: V): Boolean {
            return this@UnionFindMap._values.values.contains(element)
        }

        override fun containsAll(elements: Collection<V>): Boolean {
            return this@UnionFindMap._values.values.containsAll(elements)
        }

        override fun isEmpty(): Boolean {
            return this@UnionFindMap._values.values.isEmpty()
        }

        override fun iterator(): Iterator<V> {
            return this@UnionFindMap._values.values.iterator()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Iterable<*>) return false
            return listEquals(this, other)
        }

        override fun hashCode(): Int = listHashCode(this)

        override fun toString(): String = listToString(this)

    }

    /**
     * A key-value entry.
     */
    private data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        override fun toString(): String = "$key=$value"
    }

}