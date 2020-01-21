package com.virtlink.collections

import kotlinx.collections.immutable.PersistentMap

/**
 * A persistent disjoint map.
 */
interface PersistentDisjointMap<K, V> : DisjointMap<K, V>, PersistentMap<K, V> {

    /**
     * Unifies the components that include the given keys.
     *
     * @param key1 one key
     * @param key2 another key
     * @param unify the function that unifies the associated values of each of the components
     * @param default the function that provides a default value to use when no value is specified
     * @return the resulting persistent map
     */
    fun union(key1: K, key2: K, unify: (V, V) -> V, default: () -> V): PersistentDisjointMap<K, V>

    /**
     * Disunifies the given key from the given component.
     *
     * This will create a new component with the given key and the value of the original component.
     *
     * @param key the key to disunify
     * @param component the component from which to disunify the key
     * @return the resulting persistent map
     */
    fun disunion(key: K, component: K): PersistentDisjointMap<K, V>

    /**
     * Sets the value associated with the component that includes the given key.
     *
     * @param key the key to look for
     * @param value the value to associate with the component
     * @return the resulting persistent map
     */
    fun setComponent(key: K, value: V): PersistentDisjointMap<K, V>

    /**
     * Removes the given key from the map.
     *
     * When the removed key was the final key in the component, the component is removed.
     *
     * @param key the key to remove
     * @return a result object with the resulting persistent map,
     * and a pair of the new representative key (or `null` when the component was removed) and its associated value
     */
    fun removeKey(key: K): Result<K, V, Pair<K?, V>>

    /**
     * Computes the value associated with the component that includes the given key.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value (or `null` when it doesn't exist) to a new value
     * @return0a result object with the resulting persistent map,
     * and the computed value of the component that includes the given key
     */
    fun compute(key: K, mapping: (K, V?) -> V): Result<K, V, V>

    /**
     * Computes the value if the component that includes the given key does already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value to a new value
     * @return a result object with the resulting persistent map,
     * and the computed value of the component that includes the given key; or `null` when it was not present
     */
    fun computeIfPresent(key: K, mapping: (K, V) -> V): Result<K, V, V?>

    /**
     * Computes the value if the component that includes the given key does not already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key to a value
     * @return a result object with the resulting persistent map,
     * and the computed value of the component that includes the given element
     */
    fun computeIfAbsent(key: K, mapping: (K) -> V): Result<K, V, V>

    /**
     * Returns a builder that can be used to perform efficient mutations on the map.
     *
     * @return the builder
     */
    override fun builder(): Builder<K, V>

    /**
     * A generic builder of the persistent disjoint map.
     * The builder exposes its modification operations through the [TransientDisjointMap] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, modifications applied do not affect previously built persistent map instances.
     */
    interface Builder<K, V>: TransientDisjointMap<K, V>, PersistentMap.Builder<K, V> {

        /**
         * Returns a persistent disjoint map with the same contents as this builder.
         *
         * This method can be called multiple times.
         */
        override fun build(): PersistentDisjointMap<K, V>

    }

    /**
     * A result object.
     *
     * @property map the modified map
     * @property value the return value of the method
     */
    data class Result<K, V, T>(
        val map: PersistentDisjointMap<K, V>,
        val value: T
    )

}