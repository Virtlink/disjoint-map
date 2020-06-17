package com.virtlink.collections

import com.virtlink.N
import com.virtlink.N.assumeNotNull

/**
 * Union-find map base class.
 */
abstract class UnionFindMap<K, V> : DisjointMap<K, V> {
//
//    override val keys: ImmutableSet<K> = Keys()
//    override val values: ImmutableCollection<V> = Values()
//    override val entries: ImmutableSet<Map.Entry<K, V>> = Entries()

    // Map<K, V>
    override fun isEmpty(): Boolean = size == 0

    override fun containsKey(key: K): Boolean {
        return this.keys.contains(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.values.contains(value)
    }

    abstract override fun get(key: K): V?

    // DisjointMap<K, V>
    abstract override fun find(key: K): K?

    override fun same(key1: K, key2: K): Boolean {
        val repA = find(key1) ?: return false
        val repB = find(key2) ?: return false
        return repA == repB
    }

    override fun getComponent(key: K): Set<K> {
        val rep = find(key) ?: return emptySet()
        return this.keys.filter { find(it) == rep }.toSet()
    }

    override fun getComponentSize(key: K): Int {
        val rep = find(key) ?: return 0
        return this.keys.count { find(it) == rep }
    }
//
//    override fun put(key: K, value: V): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun putAll(m: Map<out K, V>): PersistentDisjointMap<K, V> {
////        val parents = this._parents.builder()
////        val values = this._values.builder()
////        val ranks = this._ranks.builder()
////
////        for ((elements, v) in m) {
////            if (elements.isEmpty()) continue
////            val rep = elements.first()
////            require(rep !in values && rep !in ranks) { "Element $rep was already in the map." }
////            values[rep] = v
////            ranks[rep] = elements.size
////            for (e in elements.drop(1)) {
////                require(e !in values && e !in ranks) { "Element $e was already in the map." }
////                parents[e] = rep
////            }
////        }
////        return UnionFindMap(
////            parents.build(),
////            values.build(),
////            ranks.build()
////        )
//    }
//
//    override fun remove(key: K): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun remove(key: K, value: V): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun clear(): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun putComponent(keys: Set<K>, value: V): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun disunion(key: K): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun setComponent(key: K, value: V): PersistentDisjointMap<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeKey(key: K): PersistentDisjointMap.Result<K, V, Pair<K?, V>?> {
//        TODO("Not yet implemented")
//    }
//
//    override fun compute(key: K, mapping: (K, V?) -> V): PersistentDisjointMap.Result<K, V, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun computeIfPresent(key: K, mapping: (K, V) -> V): PersistentDisjointMap.Result<K, V, V?> {
//        TODO("Not yet implemented")
//    }
//
//    override fun computeIfAbsent(key: K, mapping: (K) -> V): PersistentDisjointMap.Result<K, V, V> {
//        TODO("Not yet implemented")
//    }
//
//    override fun builder(): PersistentDisjointMap.Builder<K, V> {
//        TODO("Not yet implemented")
//    }
//
//    override val components: ImmutableMap<Set<K>, V>
//        get() = TODO("Not yet implemented")

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
     * @param values the mutable map from components to their values
     * @param parents the mutable map from keys to their parent key
     * @param ranks the mutable map from keys to their ranks
     * @return the representative key, or the given key when it's
     * its own representative; or `null` when the key was not found
     */
    protected fun findMutable(key: K, values: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): K? {
        // Is the key its own representative?
        if (values.containsKey(key)) return key
        // If not, do we know the parent key of this key?
        val parent = parents[key] ?: return null
        assert(parent != key) { "The representative was found in the parents map, which is incorrect." }

        // TODO: Can we do this iteratively, to avoid updating the parents and ranks maps so much?
        // Find the representative of the parent key
        val representative = assumeNotNull(findMutable(parent, values, parents, ranks))
        if (parent != representative) {
            // Path compression:
            // Update the key to point directly to the representative key
            parents.put(key, representative)
            // Remove one from the rank of the parent
            ranks.compute(parent) { _, oldRank ->
                val newRank = (oldRank ?: 1) - 1
                if (newRank > 1) newRank else null
            }
            // Add one to the rank of the new parent
            ranks.compute(representative) { _, oldRank -> (oldRank ?: 1) + 1 }
        }
        return representative
    }

    /**
     * Unions the components that include the given elements.
     *
     * When one or both of the keys don't exist in the map, they are added.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @param default the function that provides a default value
     * @param unify the function that unifies the associated values of each of the components
     * @param values the mutable map from components to their values
     * @param parents the mutable map from keys to their parent key
     * @param ranks the mutable map from keys to their ranks
     */
    protected fun unionMutable(key1: K, key2: K, default: () -> V, unify: (V, V) -> V, values: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>) {
        val leftRep = findMutable(key1, values, parents, ranks) ?: key1
        val rightRep = findMutable(key2, values, parents, ranks) ?: key2
        if (leftRep == rightRep) return

        // Decide which element is eliminated, and which is the new representative.
        // The higher-ranked element is chosen as the representative.
        val leftRank = ranks.remove(leftRep) ?: 1
        val rightRank = ranks.remove(rightRep) ?: 1
        val leftHasHigherRank = leftRank >= rightRank
        val rep = if (leftHasHigherRank) leftRep else rightRep
        val element = if (leftHasHigherRank) rightRep else leftRep
        ranks[rep] = leftRank + rightRank

        // Determine the new value associated with the representative.
        // It's either the value associated with the non-representative element,
        // the value associated with the representative element,
        // the unified value of both elements,
        // or the default value when none of the elements has an associated value.
        val newValue = when {
            element !in values.keys && rep !in values.keys -> default()
            element !in values.keys -> N.of(values.remove(rep))
            rep !in values.keys -> N.of(values.remove(element))
            else -> {
                // NOTE: We know both `element` and `rep` are in the values map,
                // so values.remove should only return null when their value happens to be null.
                val elemValue = N.of(values.remove(element))
                val repValue = N.of(values.remove(rep))
                unify(repValue, elemValue)
            }
        }
        // Set the new unified value
        values[rep] = newValue

        // Remove the representative from the parents map and
        // make the eliminated element point to the new representative
        parents[element] = rep
    }

    /**
     * Sets the value associated with the component that includes the given element.
     *
     * When the key doesn't exist in the map, it is added.
     *
     * @param key the key of the component to set
     * @param value the value to associate with the component
     * @param values the mutable map from components to their values
     * @param parents the mutable map from keys to their parent key
     * @param ranks the mutable map from keys to their ranks
     */
    protected fun setComponentMutable(key: K, value: V, values: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>) {
        val rep = findMutable(key, values, parents, ranks) ?: key
        setComponentMutableRep(rep, value, values)
    }

    /**
     * Sets the value associated with the component that includes the given element.
     *
     * When the key doesn't exist in the map, it is added.
     *
     * @param rep the representative element of the component to set
     * @param value the value to associate with the component
     * @param values the mutable map from components to their values
     */
    protected fun setComponentMutableRep(rep: K, value: V, values: MutableMap<K, V>) {
        values[rep] = value
    }

    /**
     * Removes a key from the map.
     *
     * When the key is the last key of a component, the component is removed.
     *
     * @param key the key to remove
     * @param values the mutable map from components to their values
     * @param parents the mutable map from keys to their parent key
     * @param ranks the mutable map from keys to their ranks
     * @return a pair of a different key of the same component (or `null` when the component was removed),
     * and the value of the component from which the key was removed; or `null` when the key was not found
     */
    protected fun removeMutable(key: K, values: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): Pair<K?, V>? {
        val rank = ranks.remove(key) ?: return null                 // Non-null if the key is known
        val root = findMutable(key, values, parents, ranks) ?: key  // The representative of the component
        val value = values.getValue(root)                           // The value of the component
        var rep: K? = root

        val isRep = parents.remove(key) == null             // Non-null when the key is not the component's representative
        assert((rep == key) xor !isRep)                     // Either the key is a rep (and therefore not in parents); or the key is in parents (and therefore not a rep).

        if (isRep) {
            // Apparently the key was itself a representative, so we have to find another

            // Pick the highest ranking key from the other elements as the new rep
            val invRep = parents.asSequence().filter { (_, r) -> r == key }.map { (k, _) -> k }
            rep = invRep.maxWith(Comparator { k1, k2 -> (ranks[k1] ?: 1).compareTo(ranks[k2] ?: 1) })   // Non-null if invRep is not empty
        }

        if (rep != null) {
            // Apparently we found a new representative (the component is not empty)

            // Set the representative of keys to `rep` when their representative is `key`; otherwise leave unchanged
            replaceAllStub(parents) { _, r -> if (r == key) rep else r }
            // Store the new rank of the new representative
            ranks[rep] = (ranks[rep] ?: 1) + rank

            // Move the value from the old to the new representative, if any
            if (key in values && rep != key) {
                values.remove(key)
                values[rep] = value
            }
        }

        return rep to value
    }

    private fun <K, V> replaceAllStub(map: MutableMap<K, V>, f: (K, V) -> V) {
        // TODO: Replace this function with Map.replaceAll() once it's fixed for PersistentMap.builder()
        val entries = mapOf(*map.entries.map { (e, v) -> e to v }.toTypedArray())
        for (e in entries) {
            map.replace(e.key, f(e.key, e.value))
        }
    }

//    /**
//     * Inner class used to give a view of the entries in the map.
//     */
//    private inner class Entries: ImmutableSet<Map.Entry<K, V>> {
//
//        override val size: Int
//            get() = this@UnionFindMap.size
//
//        override fun isEmpty(): Boolean = this.size == 0
//
//        override fun contains(element: Map.Entry<K, V>): Boolean {
//            val value = this@UnionFindMap[element.key]
//            return value == element.value
//        }
//
//        override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean {
//            return elements.all { contains(it) }
//        }
//
//        override fun iterator(): Iterator<Map.Entry<K, V>> = iterator {
//            yieldAll(this@UnionFindMap.keys.asSequence().map {
//                Entry(it, N.of(this@UnionFindMap[it]))
//            })
//        }
//
//        override fun equals(other: Any?): Boolean {
//            if (other !is Set<*>) return false
//            return setEquals(this, other)
//        }
//
//        override fun hashCode(): Int = setHashCode(this)
//
//        override fun toString(): String = listToString(this)
//
//    }
//
//    /**
//     * Inner class used to give a view of the keys in the map.
//     */
//    private inner class Keys: ImmutableSet<K> {
//
//        override val size: Int
//            get() = this@UnionFindMap.size
//
//        override fun isEmpty(): Boolean = this.size == 0
//
//        override fun contains(element: K): Boolean {
//            // @formatter:off
//            return this@UnionFindMap._values.containsKey(element)
//                || this@UnionFindMap._parents.containsKey(element)
//            // @formatter:on
//        }
//
//        override fun containsAll(elements: Collection<K>): Boolean {
//            return elements.all { contains(it) }
//        }
//
//        override fun iterator(): Iterator<K> = iterator {
//            yieldAll(this@UnionFindMap._values.keys)
//            yieldAll(this@UnionFindMap._parents.keys)
//        }
//
//        override fun equals(other: Any?): Boolean {
//            if (other !is Set<*>) return false
//            return setEquals(this, other)
//        }
//
//        override fun hashCode(): Int = setHashCode(this)
//
//        override fun toString(): String = listToString(this)
//    }
//
//    /**
//     * Inner class used ot give a view of the values in the map.
//     */
//    private inner class Values: ImmutableCollection<V> {
//
//        override val size: Int
//            get() = this@UnionFindMap._values.size
//
//        override fun contains(element: V): Boolean {
//            return this@UnionFindMap._values.values.contains(element)
//        }
//
//        override fun containsAll(elements: Collection<V>): Boolean {
//            return this@UnionFindMap._values.values.containsAll(elements)
//        }
//
//        override fun isEmpty(): Boolean {
//            return this@UnionFindMap._values.values.isEmpty()
//        }
//
//        override fun iterator(): Iterator<V> {
//            return this@UnionFindMap._values.values.iterator()
//        }
//
//        override fun equals(other: Any?): Boolean {
//            if (other !is Iterable<*>) return false
//            return listEquals(this, other)
//        }
//
//        override fun hashCode(): Int = listHashCode(this)
//
//        override fun toString(): String = listToString(this)
//
//    }
//
//    /**
//     * A key-value entry.
//     */
//    private data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
//        override fun toString(): String = "$key=$value"
//    }

}