@file:Suppress("unused")

package net.pelsmaeker.collections

/**
 * Returns an empty persistent disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return An empty persistent disjoint map.
 */
fun <K, V> persistentDisjointMapOf(): PersistentDisjointMap<K, V> = PersistentUnionFindMap.emptyOf()

/**
 * Returns a persistent disjoint map with the given sets.
 *
 * @param pairs The pairs of sets, consisting of a set of keys and an associated value.
 * @param K The type of keys.
 * @param V The type of values.
 * @return The created persistent disjoint map.
 */
fun <K, V> persistentDisjointMapOf(vararg pairs: Pair<Set<K>, V>): PersistentDisjointMap<K, V> {
    return pairs.asIterable().toPersistentDisjointMap()
}

/**
 * Returns an empty mutable disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return An empty mutable disjoint map.
 */
fun <K, V> mutableDisjointMapOf(): MutableDisjointMap<K, V> = MutableUnionFindMap()

/**
 * Returns a mutable disjoint map with the given sets.
 *
 * @param pairs The pairs of sets, consisting of a set of keys and an associated value.
 * @param K The type of keys.
 * @param V The type of values.
 * @return The created mutable disjoint map.
 */
fun <K, V> mutableDisjointMapOf(vararg pairs: Pair<Set<K>, V>): MutableDisjointMap<K, V> {
    return pairs.asIterable().toMutableDisjointMap()
}

/**
 * Returns the given disjoint map as an immutable disjoint map.
 *
 * If the receiver is already an immutable disjoint map, it is returned as-is.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting immutable disjoint map.
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
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting persistent disjoint map.
 */
fun <K, V> DisjointMap<K, V>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return this as? PersistentDisjointMap<K, V>
        ?: (this as? PersistentDisjointMap.Builder<K, V>)?.build()
        ?: this.toMap().toPersistentDisjointMap()
}

/**
 * Returns the given map of sets as a persistent disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting persistent disjoint map.
 */
fun <K, V> Map<Set<K>, V>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return this.entries.map { it.toPair() }.toPersistentDisjointMap()
}

/**
 * Returns the given map of sets as a mutable disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting mutable disjoint map.
 */
private fun <K, V> Map<Set<K>, V>.toMutableDisjointMap(): MutableDisjointMap<K, V> {
    return this.entries.map { it.toPair() }.toMutableDisjointMap()
}

/**
 * Returns the given iterable of pairs of a set of keys and a value as a persistent disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting persistent disjoint map.
 */
fun <K, V> Iterable<Pair<Set<K>, V>>.toPersistentDisjointMap(): PersistentDisjointMap<K, V> {
    return PersistentUnionFindMap.emptyOf<K, V>().builder().addSets(this).build()
}

/**
 * Returns the given iterable of pairs of a set of keys and a value as a mutable disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @return The resulting mutable disjoint map.
 */
private fun <K, V> Iterable<Pair<Set<K>, V>>.toMutableDisjointMap(): MutableDisjointMap<K, V> {
    return mutableDisjointMapOf<K, V>().addSets(this)
}

/**
 * Adds the given sets to the mutable disjoint map.
 *
 * @param sets The sets to add, an iterable of pairs of a set of keys and a value.
 * @param K The type of keys.
 * @param V The type of values.
 * @return The receiver.
 */
private fun <K, V, T: MutableDisjointMap<K, V>> T.addSets(sets: Iterable<Pair<Set<K>, V>>): T {
    for ((keys, value) in sets) {
        val rep = keys.firstOrNull() ?: continue
        for (key in keys) {
            union(rep, key, { value }, { _, _ -> 0 }) { _, _ -> value }
        }
    }
    return this
}

/**
 * Applies the provided modifications to this persistent disjoint map.
 *
 * The mutable disjoint map passed to [mutator] had the same contents as the persistent disjoint map.
 *
 * @param mutator The closure that mutates the map.
 * @param K The type of keys.
 * @param V The type of values.
 * @return A persistent disjoint map with the modifications applied.
 */
inline fun <K, V> PersistentDisjointMap<K, V>.mutate(mutator: (MutableDisjointMap<K, V>) -> Unit): PersistentDisjointMap<K, V>
        = this.builder().apply(mutator).build()