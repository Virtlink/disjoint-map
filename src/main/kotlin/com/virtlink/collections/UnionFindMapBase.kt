package com.virtlink.collections

import com.virtlink.N
import kotlinx.collections.immutable.*

/**
 * Base class for a union-find map.
 */
@Suppress("PropertyName")
abstract class UnionFindMapBase<K, V> internal constructor() : DisjointMap<K, V> {

    // There are as many components as there are entries in _values, where the key in _values
    // is the representative key of each component.
    // Non-representative keys have a reference to their parent key in _parents,
    // which may or may not be the representative key.

    /** Maps each key to its parent key.
     *  This map contains only entries for those keys that are not root keys. */
    protected abstract val _parents: Map<K, K>
    /** Maps each key to its value, if any.
     *  This map contains only entries for those keys that are root keys. */
    protected abstract val _values: Map<K, V>
    /** Maps each key to its rank, which is the number of keys it has under it. */
    protected abstract val _ranks: Map<K, Int>

    override val entries: ImmutableSet<Map.Entry<K, V>> = Entries()
    override val keys: ImmutableSet<K> = Keys()
    override val values: ImmutableCollection<V> = Values()

    override val size: Int
        get() = this.keys.size

    override fun isEmpty(): Boolean {
        return this.size == 0
    }

    override fun containsKey(key: K): Boolean {
        return this.keys.contains(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.values.contains(value)
    }

    override fun same(a: K, b: K): Boolean {
        val repA = find(a)
        val repB = find(b)
        return repA == repB
    }

    override fun get(key: K): V? {
        val rep = find(key)
        return this._values[rep]
    }

    override fun getComponent(key: K): Set<K> {
        val rep = find(key)
        return this.keys.filter { find(it) == rep }.ifEmpty { listOf(key) } .toSet()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Set<*>) return false
        return setEquals(this.entries, other)
    }

    override fun hashCode(): Int = setHashCode(this.entries)

    override fun toString(): String = listToString(this.entries)

    /**
     * Finds the representative key for the given key,
     * and performs path compression.
     *
     * @param key the key for which to find the representative key
     * @param parents the map from keys to their parent key
     * @return the representative key, or the given key when it's its own representative
     */
    protected fun findMutable(key: K, parents: MutableMap<K, K>): K {
        val parent = parents[key] ?: return key
        assert(parent != key) { "The representative must not be in the parents map." }
        val rep = findMutable(parent, parents)
        // Update the key to point directly to the representative key
        parents[key] = rep
        // TODO: Update ranks
        return rep
    }

    /**
     * Unions the components that include the given elements.
     *
     * @param a one element
     * @param b another element
     * @param default the function that provides a default value
     * @param unify the function that unifies the associated values of each of the components
     * @param parents the map from keys to their parent key
     * @param values the value associated with each component
     * @param ranks the map from keys to their ranks
     * @return the resulting map
     */
    protected fun unionMutable(a: K, b: K, default: () -> V, unify: (V, V) -> V, parents: MutableMap<K, K>, values: MutableMap<K, V>, ranks: MutableMap<K, Int>) {
        val leftRep = findMutable(a, parents)
        val rightRep = findMutable(b, parents)
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
     * @param key the key of the component to set
     * @param value the value to associate with the component
     * @param values the value associated with each component
     * @return the mutated union-find map
     */
    protected fun setComponentMutable(key: K, value: V, values: MutableMap<K, V>) {
        val rep = find(key)
        setComponentMutableRep(rep, value, values)
    }

    /**
     * Sets the value associated with the component that includes the given element.
     *
     * @param rep the representative element of the component to set
     * @param value the value to associate with the component
     * @param values the value associated with each component
     * @return the mutated union-find map
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
     * @param parents the map from keys to their parent key
     * @param values the value associated with each component
     * @param ranks the map from keys to their ranks
     * @return a pair of a different key of the same component (or `null` when the component was removed),
     * and the value of the component from which the key was removed; or `null` when the key was not found
     */
    protected fun removeMutable(key: K, parents: MutableMap<K, K>, values: MutableMap<K, V>, ranks: MutableMap<K, Int>): Pair<K?, V>? {
        val rank = ranks.remove(key) ?: return null         // Non-null if the key is known
        val root = findMutable(key, parents)                 // The representative of the component
        val value = values.getValue(root)                    // The value of the component
        var rep: K? = root

        val isRep = parents.remove(key) == null         // Non-null when the key is not the component's representative
        assert((rep == key) xor !isRep)                         // Either the key is a rep (and therefore not in parents); or the key is in parents (and therefore not a rep).

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

    /**
     * Inner class used to give a view of the entries in the map.
     */
    protected inner class Entries: ImmutableSet<Map.Entry<K, V>> {

        override val size: Int
            get() = this@UnionFindMapBase.size

        override fun contains(element: Map.Entry<K, V>): Boolean {
            val value = this@UnionFindMapBase[element.key]
            return value == element.value
        }

        override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean {
            return elements.all { contains(it) }
        }

        override fun isEmpty(): Boolean {
            return this.size == 0
        }

        override fun iterator(): Iterator<Map.Entry<K, V>> = iterator {
            yieldAll(this@UnionFindMapBase.keys.asSequence().map {
                Entry(
                    it,
                    N.of(this@UnionFindMapBase[it])
                )
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
    protected inner class Keys: ImmutableSet<K> {

        override val size: Int
            get() = this@UnionFindMapBase._parents.size + this@UnionFindMapBase._values.size

        override fun contains(element: K): Boolean {
            // @formatter:off
            return this@UnionFindMapBase._values.containsKey(element)
                    || this@UnionFindMapBase._parents.containsKey(element)
            // @formatter:on
        }

        override fun containsAll(elements: Collection<K>): Boolean {
            return elements.all { contains(it) }
        }

        override fun isEmpty(): Boolean {
            return this.size == 0
        }

        override fun iterator(): Iterator<K> = iterator {
            yieldAll(this@UnionFindMapBase._values.keys)
            yieldAll(this@UnionFindMapBase._parents.keys)
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
    protected inner class Values: ImmutableCollection<V> {

        override val size: Int
            get() = this@UnionFindMapBase._values.values.size

        override fun contains(element: V): Boolean {
            return this@UnionFindMapBase._values.values.contains(element)
        }

        override fun containsAll(elements: Collection<V>): Boolean {
            return this@UnionFindMapBase._values.values.containsAll(elements)
        }

        override fun isEmpty(): Boolean {
            return this@UnionFindMapBase._values.values.isEmpty()
        }

        override fun iterator(): Iterator<V> {
            return this@UnionFindMapBase._values.values.iterator()
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
    protected data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        override fun toString(): String = "$key=$value"
    }

}