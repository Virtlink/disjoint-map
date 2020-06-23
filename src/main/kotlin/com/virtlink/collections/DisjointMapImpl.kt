package com.virtlink.collections

import com.virtlink.N

/**
 * Finds the representative key for the given key
 * and performs path compression.
 *
 * @param key the key for which to find the representative key
 * @param roots the map from sets to their values
 * @param parents the mutable map from keys to their parent key
 * @param ranks the mutable map from keys to their ranks
 * @return the representative key, or the given key when it's
 * its own representative; or `null` when the key was not found
 */
internal fun <K, V> findMutable(key: K, roots: Map<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): K? {
    // Is the key its own representative?
    if (roots.containsKey(key)) return key
    // If not, do we know the parent key of this key?
    val parent = parents[key] ?: return null
    assert(parent != key) { "The representative was found in the parents map, which is incorrect." }

    // TODO: Can we do this iteratively, to avoid updating the parents and ranks maps so much?
    // Find the representative of the parent key
    val representative = N.assumeNotNull(findMutable(parent, roots, parents, ranks))
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
 * Unions the sets that include the given keys.
 *
 * When one or both of the keys don't exist in the map, they are added.
 *
 * @param key1 the first key
 * @param key2 the second key
 * @param default the function that provides a default value
 * @param unify the function that unifies the associated values of each of the sets
 * @param roots the mutable map from sets to their values
 * @param parents the mutable map from keys to their parent key
 * @param ranks the mutable map from keys to their ranks
 */
internal fun <K, V> unionMutable(key1: K, key2: K, default: () -> V, unify: (V, V) -> V, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>) {
    val leftRep = findMutable(key1, roots, parents, ranks) ?: key1
    val rightRep = findMutable(key2, roots, parents, ranks) ?: key2
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
        element !in roots.keys && rep !in roots.keys -> default()
        element !in roots.keys -> N.of(roots.remove(rep))
        rep !in roots.keys -> N.of(roots.remove(element))
        else -> {
            // NOTE: We know both `element` and `rep` are in the values map,
            // so values.remove should only return null when their value happens to be null.
            val elemValue = N.of(roots.remove(element))
            val repValue = N.of(roots.remove(rep))
            unify(repValue, elemValue)
        }
    }
    // Set the new unified value
    roots[rep] = newValue

    // Remove the representative from the parents map and
    // make the eliminated element point to the new representative
    parents[element] = rep
}

/**
 * Disunifies the given key from its set.
 *
 * This will create a new set with the given key and the value of the original set.
 * When the key doesn't exist, nothing happens.
 *
 * @param key the key to disunify
 * @param roots the mutable map from sets to their values
 * @param parents the mutable map from keys to their parent key
 * @param ranks the mutable map from keys to their ranks
 * @return `true` when this changed the disjoint map; otherwise, false
 */
internal fun <K, V> disunionMutable(key: K, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): Boolean {
    val rep = findMutable(key, roots, parents, ranks) ?: return false

    if (rep == key) {
        // The key to disunify is the set's root key
        TODO("Not implemented")
    } else {
        // The key to disunify is not the set's root key
        TODO("Not implemented")
    }
}

/**
 * Sets the value associated with the set that includes the given element.
 *
 * When the key doesn't exist in the map, it is added.
 *
 * @param key the key of the set to set
 * @param value the value to associate with the set
 * @param roots the mutable map from sets to their values
 * @param parents the mutable map from keys to their parent key
 * @param ranks the mutable map from keys to their ranks
 * @return the old value associated with the key; or `null`
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
 * @param rep the representative element of the set to set
 * @param value the value to associate with the set
 * @param roots the mutable map from sets to their values
 */
internal fun <K, V> setMutableRep(rep: K, value: V, roots: MutableMap<K, V>) {
    roots[rep] = value
}

/**
 * Removes a key from the map.
 *
 * When the key is the last key of a set, the set is removed.
 *
 * @param key the key to remove
 * @param expectedValue the value to remove; or `null` when it could be any value
 * @param checkValue whether to check the value before removing
 * @param roots the mutable map from sets to their values
 * @param parents the mutable map from keys to their parent key
 * @param ranks the mutable map from keys to their ranks
 * @return a pair of a different key of the same set (or `null` when the set was removed),
 * and the value of the set from which the key was removed; or `null` when the key was not found
 */
internal fun <K, V> removeMutable(key: K, expectedValue: V?, checkValue: Boolean, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>): Pair<K?, V>? {
    val root = findMutable(key, roots, parents, ranks) ?: return null   // The representative of the set
    val value = roots.getValue(root)                                    // The value of the set
    var rep: K? = root

    if (checkValue && value != expectedValue) return null               // Value mismatch

    val rank = ranks.remove(key) ?: return null                         // Non-null if the key is known
    val isRep = parents.remove(key) == null                             // Non-null when the key is not the set's representative
    assert((rep == key) xor !isRep)                                     // Either the key is a rep (and therefore not in parents); or the key is in parents (and therefore not a rep).

    if (isRep) {
        // Apparently the key was itself a representative, so we have to find another

        // Pick the highest ranking key from the other elements as the new rep
        val invRep = parents.asSequence().filter { (_, r) -> r == key }.map { (k, _) -> k }
        rep = invRep.maxWith(Comparator { k1, k2 -> (ranks[k1] ?: 1).compareTo(ranks[k2] ?: 1) })   // Non-null if invRep is not empty
    }

    if (rep != null) {
        // Apparently we found a new representative (the set is not empty)

        // Set the representative of keys to `rep` when their representative is `key`; otherwise leave unchanged
        replaceAllStub(parents) { _, r -> if (r == key) rep else r }
        // Store the new rank of the new representative
        ranks[rep] = (ranks[rep] ?: 1) + rank

        // Move the value from the old to the new representative, if any
        if (key in roots && rep != key) {
            roots.remove(key)
            roots[rep] = value
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
