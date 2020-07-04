@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.virtlink.collections

/**
 * Returns an empty persistent disjoint map.
 *
 * @return an empty persistent disjoint map
 */
inline fun <K, V> persistentDisjointMapOf(): PersistentDisjointMap<K, V> = PersistentUnionFindMap.emptyOf()

/**
 * Returns a persistent disjoint map with the given sets.
 *
 * @param pairs the pairs of sets, consisting of a set of keys and an associated value
 * @return the created persistent disjoint map
 */
fun <K, V> persistentDisjointMapOf(vararg pairs: Pair<Set<K>, V>): PersistentDisjointMap<K, V> {
    return pairs.asIterable().toPersistentDisjointMap()
}

/**
 * Returns an empty mutable disjoint map.
 *
 * @return an empty mutable disjoint map
 */
inline fun <K, V> mutableDisjointMapOf(): MutableDisjointMap<K, V> = MutableUnionFindMap<K, V>()

/**
 * Returns a mutable disjoint map with the given sets.
 *
 * @param pairs the pairs of sets, consisting of a set of keys and an associated value
 * @return the created mutable disjoint map
 */
fun <K, V> mutableDisjointMapOf(vararg pairs: Pair<Set<K>, V>): MutableDisjointMap<K, V> {
    return pairs.asIterable().toMutableDisjointMap()
}

/**
 * Returns the given disjoint map as an immutable disjoint map.
 *
 * If the receiver is already an immutable disjoint map, it is returned as-is.
 *
 * @return the resulting immutable disjoint map
 */
fun <K, V> DisjointMap<K, V>.toImmutableDisjointMap(): ImmutableDisjointMap<K, V> {
    return this as? ImmutableDisjointMap<K, V> ?: this.toPersistentDisjointMap()
}

/**
 * Returns the given disjoint map as a persistent disjoint map.
 *
 * If the receiver is already a persistent disjoint map, it is returned as-is.
 * If the receiver is a persistent disjoint map builder, the result of calling its [PersistentDisjointMap.Builder.build] method is returned.
 *
 * @return the resulting persistent disjoint map
 */
fun <K, V> DisjointMap<K, V>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return this as? PersistentDisjointMap<K, V>
        ?: (this as? PersistentDisjointMap.Builder<K, V>)?.build()
        ?: this.toMap().toPersistentDisjointMap()
}

/**
 * Returns the given map of sets as a persistent disjoint map.
 *
 * @return the resulting persistent disjoint map
 */
fun <K, V> Map<Set<K>, V>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return this.entries.map { it.toPair() }.toPersistentDisjointMap()
}

/**
 * Returns the given map of sets as a mutable disjoint map.
 *
 * @return the resulting mutable disjoint map
 */
private fun <K, V> Map<Set<K>, V>.toMutableDisjointMap(): MutableDisjointMap<K, V> {
    return this.entries.map { it.toPair() }.toMutableDisjointMap()
}

/**
 * Returns the given iterable of pairs of a set of keys and a value as a persistent disjoint map.
 *
 * @return the resulting persistent disjoint map
 */
fun <K, V> Iterable<Pair<Set<K>, V>>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return PersistentUnionFindMap.emptyOf<K, V>().builder().addSets(this).build()
}

/**
 * Returns the given iterable of pairs of a set of keys and a value as a mutable disjoint map.
 *
 * @return the resulting mutable disjoint map
 */
private fun <K, V> Iterable<Pair<Set<K>, V>>.toMutableDisjointMap(): MutableDisjointMap<K, V> {
    return mutableDisjointMapOf<K, V>().addSets(this)
}

/**
 * Adds the given sets to the mutable disjoint map.
 *
 * @param sets the sets to add, an iterable of pairs of a set of keys and a value
 * @return the receiver
 */
private fun <K, V, T: MutableDisjointMap<K, V>> T.addSets(sets: Iterable<Pair<Set<K>, V>>): T {
    for ((keys, value) in sets) {
        val rep = keys.firstOrNull() ?: continue
        for (key in keys) {
            union(rep, key, { value }, { _, _ -> value })
        }
    }
    return this
}

/**
 * Applies the provided modifications to this persistent disjoint map.
 *
 * The mutable disjoint map passed to [mutator] had the same contents as the persistent disjoint map.
 *
 * @param mutator the closure that mutates the map
 * @return a persistent disjoint map with the modifications applied
 */
inline fun <K, V> PersistentDisjointMap<K, V>.mutate(mutator: (MutableDisjointMap<K, V>) -> Unit): PersistentDisjointMap<K, V>
        = this.builder().apply(mutator).build()
