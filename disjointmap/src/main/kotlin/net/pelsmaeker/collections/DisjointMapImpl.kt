package net.pelsmaeker.collections

import kotlinx.collections.immutable.toPersistentSet

/**
 * Finds the representative key for the given key
 * and performs path compression.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param key The key for which to find the representative key.
 * @param roots The map from sets to their values.
 * @param parents The mutable map from keys to their parent key.
 * @param ranks The mutable map from keys to their ranks.
 * @return The representative key, or the given key when it's
 * its own representative; or `null` when the key was not found.
 */
internal fun <K, V> findMutable(
    key: K,
    roots: Map<K, V>,
    parents: MutableMap<K, K>,
    ranks: MutableMap<K, Int>
): K? {
    // Is the key its own representative?
    if (roots.containsKey(key)) return key
    // If not, do we know the parent key of this key?
    val parent = parents[key] ?: return null
    assert(parent != key) { "The representative was found in the parents map, which is incorrect." }

    // TODO: Can we do this iteratively, to avoid updating the parents and ranks maps so much?
    // Find the representative of the parent key
    val representative = findMutable(parent, roots, parents, ranks)
    if (representative != null && parent != representative) {
        // Path compression:
        // Update the key to point directly to the representative key
        parents[key] = representative
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
 * Unions the sets that include the given keys.
 *
 * When one or both of the keys don't exist in the map, they are added.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param key1 The first key.
 * @param key2 The second key.
 * @param default Function that provides a default value.
 * @param compare Function that compares the keys: the higher is used as the new representative;
 * or 0 to use the rank to determine this.
 * @param unify Function that unifies the associated values of each of the sets,
 * where the first value is from the representative.
 * @param roots The mutable map from sets to their values.
 * @param parents The mutable map from keys to their parent key.
 * @param ranks The mutable map from keys to their ranks.
 * @return Whether disjoint sets have been unified.
 */
@Suppress("UNCHECKED_CAST")
internal fun <K, V> unionMutable(
    key1: K,
    key2: K,
    default: () -> V,
    compare: Comparator<K>,
    unify: (V, V) -> V,
    roots: MutableMap<K, V>,
    parents: MutableMap<K, K>,
    ranks: MutableMap<K, Int>
): Boolean {
    val leftRep = findMutable(key1, roots, parents, ranks) ?: key1
    val rightRep = findMutable(key2, roots, parents, ranks) ?: key2
    if (leftRep == rightRep) return false

    // Decide which element is eliminated, and which is the new representative.
    // The higher-valued element is chosen as the representative.
    val leftRank = ranks[leftRep] ?: 1
    val rightRank = ranks[rightRep] ?: 1
    val leftHasHigherValue = compare.then { _, _ -> leftRank.compareTo(rightRank) }.compare(leftRep, rightRep) >= 0
    val repr = if (leftHasHigherValue) leftRep else rightRep  // the new representative
    val elem = if (leftHasHigherValue) rightRep else leftRep  // the eliminated variable

    // Determine the new value associated with the representative. It's either:
    val newValue = when {
        // - the unified value of both elements;
        elem in roots.keys && repr in roots.keys -> {
            // NOTE: We know both `element` and `rep` are in the values map,
            //  so values.get() should only return null when their value happens to be null.
            val repValue = roots[repr] as V
            val elemValue = roots[elem] as V
            unify(repValue, elemValue)
        }
        // - the value associated with the representative element;
        repr in roots.keys -> roots[repr] as V
        // - the value associated with the eliminated element; or
        elem in roots.keys -> roots[elem] as V
        // - the default value when neither element has an associated value.
        else -> default()
    }

    // NOTE: Before this point, no mutation of the disjoint map occurs.
    //  Therefore, if any of the above code fails, the map should still be in a consistent state.

    // Update the values
    roots.remove(repr)
    roots.remove(elem)
    roots[repr] = newValue

    // Update the ranks
    ranks.remove(leftRep)
    ranks.remove(rightRep)
    ranks[repr] = leftRank + rightRank

    // Remove the representative from the parents map and
    // make the eliminated element point to the new representative
    parents.remove(repr)
    parents[elem] = repr

    return true
}

/**
 * Disunifies the given key from its set.
 *
 * This will create a new set with the given key and the value of the original set.
 * When the key doesn't exist, nothing happens.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param key The key to disunify.
 * @param roots The mutable map from sets to their values.
 * @param parents The mutable map from keys to their parent key.
 * @param ranks The mutable map from keys to their ranks.
 * @return Whether the key was in the map.
 */
internal fun <K, V> disunionMutable(key: K, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): Boolean {
    var rep: K = findMutable(key, roots, parents, ranks) ?: return false

    if (rep == key) {
        // The key to disunify is the set's rep key

        // Create a new disjoint set with the highest ranking key as the rep
        val newRep = parents.asSequence().filter { (_, r) -> r == key }.map { (k, _) -> k }
            .maxWithOrNull { k1, k2 -> (ranks[k1] ?: 1).compareTo(ranks[k2] ?: 1) }
            ?: return true // The key was the only one in the disjoint set
        rep = newRep
        roots[rep] = roots[key]!!
    } else {
        // The key to disunify is not the set's root key

        // Create a new disjoint set with the key as the rep
        roots[key] = roots[rep]!!
        // Remove it from the parents set
        parents.remove(key)
    }

    // Any keys which have the old key as the parent now get the rep as the parent
    parents.replaceAllStub { _, r -> if (r == key) rep else r }
    return true
}

/**
 * Sets the value associated with the set that includes the given element.
 *
 * When the key doesn't exist in the map, it is added.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param key The key of the set to set.
 * @param value The value to associate with the set.
 * @param roots The mutable map from sets to their values.
 * @param parents The mutable map from keys to their parent key.
 * @param ranks The mutable map from keys to their ranks.
 * @return The old value associated with the key; or `null`.
 */
internal fun <K, V> setMutable(key: K, value: V, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): V? {
    val rep = findMutable(key, roots, parents, ranks) ?: key
    val oldValue = roots[rep]
    setMutableRep(rep, value, roots)
    return oldValue
}

/**
 * Sets the value associated with the set that includes the given element.
 *
 * When the key doesn't exist in the map, it is added.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param rep The representative element of the set to set.
 * @param value The value to associate with the set.
 * @param roots The mutable map from sets to their values.
 */
internal fun <K, V> setMutableRep(rep: K, value: V, roots: MutableMap<K, V>) {
    roots[rep] = value
}

/**
 * Removes a key from the map.
 *
 * When the key is the last key of a set, the set is removed.
 *
 * @param K The type of keys.
 * @param V The type of values.
 * @param key The key to remove.
 * @param roots The mutable map from sets to their values.
 * @param parents The mutable map from keys to their parent key.
 * @param ranks The mutable map from keys to their ranks.
 * @return The value of the set from which the key was removed; or `null` when the key was not found.
 */
internal fun <K, V> removeMutable(
    key: K,
    roots: MutableMap<K, V>,
    parents: MutableMap<K, K>,
    ranks: MutableMap<K, Int>
): V? {
    // Ensure the key we want to remove is in its own set
    disunionMutable(key, roots, parents, ranks)
    // Now we can remove it
    return roots.remove(key)
}

/**
 * Copies the sets from this disjoint map to a new map.
 *
 * @return The new map.
 */
internal fun <K, V> DisjointMap<K, V>.toMapImpl(
    roots: Map<K, V>,
    parents: Map<K, K>,
    ranks: Map<K, Int>,
): Map<Set<K>, V> {
    // Maps each representative key to a set of keys
    val mapping = mutableMapOf<K, MutableSet<K>>()
    roots.keys.forEach { k -> mapping[k] = mutableSetOf(k) }
    parents.keys.forEach { k -> mapping[find(k)]!!.add(k) }

    return mapping.map { (rep, keys) ->
        @Suppress("UNCHECKED_CAST")
        keys.toPersistentSet() to (roots[rep] as V)
    }
        .toMap<Set<K>, V>()
}

// TODO: Replace this function with Map.replaceAll() once it's fixed for PersistentMap.builder()
private fun <K, V> MutableMap<K, V>.replaceAllStub(f: (K, V) -> V) {
    val entries = mapOf(*this.entries.map { (e, v) -> e to v }.toTypedArray())
    for (e in entries) {
        this.replace(e.key, f(e.key, e.value))
    }
}