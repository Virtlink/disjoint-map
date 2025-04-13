package net.pelsmaeker.collections

import kotlinx.collections.immutable.ImmutableMap

/**
 * An immutable disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 */
interface ImmutableDisjointMap<K, out V> : DisjointMap<K, V> {

    /**
     * Copies the sets from this disjoint map to a new map.
     *
     * @return the new map
     */
    override fun toMap(): ImmutableMap<Set<K>, V>

}

