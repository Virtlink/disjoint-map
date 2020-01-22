package com.virtlink.collections

import kotlinx.collections.immutable.ImmutableMap

/**
 * A map of elements in disjoint components.
 */
interface DisjointMap<K, V>: Map<K, V> {

    /**
     * Gets a set of components in this map.
     */
    val components: ImmutableMap<Set<K>, V>

    /**
     * Finds the representative of the component that includes the given key.
     *
     * @param key the key to look for
     * @return the representative of the component that includes the given key;
     * which may be the key itself
     */
    fun find(key: K): K

    /**
     * Determines whether the given keys are in the same component.
     *
     * @param key1 one key
     * @param key2 another key
     * @return `true` when the keys are part of the same component;
     * otherwise, `false`
     */
    fun same(key1: K, key2: K): Boolean

    /**
     * Gets the keys of the component that includes the given key.
     *
     * @param key the key to look for
     * @return the keys of the component that includes the given key,
     * including the key itself; or an empty set when the key is not in the map
     */
    fun getComponent(key: K): Set<K>

    /**
     * Gets the size of the component that includes the given key.
     *
     * @param key the key to look for
     * @return the number of keys in the component that includes the given key,
     * including the key itself; or 0 when the key is not in the map
     */
    fun getComponentSize(key: K): Int

}