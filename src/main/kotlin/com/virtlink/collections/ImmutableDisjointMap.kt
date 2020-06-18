package com.virtlink.collections

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

/**
 * An immutable disjoint map.
 */
interface ImmutableDisjointMap<K, out V> : DisjointMap<K, V>, ImmutableMap<K, V> {

    /**
     * Gets a set of components in this map.
     *
     * @return the components in this map
     */
    override val components: ImmutableSet<DisjointMap.Component<K, V>>

}