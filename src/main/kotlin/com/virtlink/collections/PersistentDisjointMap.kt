package com.virtlink.collections

import kotlinx.collections.immutable.PersistentMap

/**
 * A persistent disjoint map.
 */
interface PersistentDisjointMap<K, V> : ImmutableDisjointMap<K, V>, PersistentMap<K, V> {

    // PersistentMap<K, V>
    /**
     * Associates the specified [key] with a new component with the specified [value].
     *
     * If this map already contains a component with the key,
     * the key is removed from the component and added to a new one.
     *
     * @param key the key
     * @param value the value to associate with the specified [key]
     * @return the resulting persistent map
     */
    override fun put(key: K, value: V): PersistentDisjointMap<K, V>

    /**
     * Associates the entries in the specified map [m] with this map.
     *
     * If the given map is a [DisjointMap], the components from the map are added to this
     * map. Any existing keys in this map are reassociated with the new components.
     * If the given map is an ordinary [Map], this call is equivalent to calling [put]
     * once for each mapping in the specified map.
     *
     * @param m the map to associate
     * @return the resulting persistent map
     */
    override fun putAll(m: Map<out K, V>): PersistentDisjointMap<K, V>

    /**
     * Removes the specified [key] from this map.
     *
     * When the removed key was the final key in the component, the component is removed as well.
     *
     * @param key the key to remove
     * @return the resulting persistent map
     */
    override fun remove(key: K): PersistentDisjointMap<K, V>

    /**
     * Removes the specified [key] that maps to the specified [value].
     *
     * When the removed key was the final key in the component, the component is removed as well.
     *
     * @param key the key to remove
     * @param value the value of the key to remove
     * @return the resulting persistent map
     */
    override fun remove(key: K, value: V): PersistentDisjointMap<K, V>

    /**
     * Removes all associations from this map.
     *
     * @return the resulting persistent map
     */
    override fun clear(): PersistentDisjointMap<K, V>


    // PersistentDisjointMap<K, V>
    /**
     * Sets the value associated with the component that includes the given key.
     *
     * @param key the key to look for
     * @param value the value to associate with the component
     * @return the resulting persistent map
     */
    fun setComponent(key: K, value: V): PersistentDisjointMap<K, V>

    /**
     * Puts the given component.
     *
     * Any keys in the component that are already part of other components
     * are removed from those components.
     *
     * @param keys the keys of the component to put
     * @param value the value of the component to put
     * @return the resulting persistent map
     */
    fun putComponent(keys: Iterable<K>, value: V): PersistentDisjointMap<K, V>

    /**
     * Puts the given components.
     *
     * Any keys in a component that are already part of other components
     * are removed from those components.
     *
     * @param map the map of components to put
     * @return the resulting persistent map
     */
    fun putAllComponents(map: DisjointMap<K, V>): PersistentDisjointMap<K, V>

    /**
     * Unifies the components that include the given keys.
     *
     * @param key1 one key
     * @param key2 another key
     * @param default the function that provides a default value to use when no value is specified
     * @param unify the function that unifies the associated values of each of the components
     * @return the resulting persistent map
     */
    fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V): PersistentDisjointMap<K, V>

    /**
     * Disunifies the given key from its component.
     *
     * This will create a new component with the given key and the value of the original component.
     *
     * @param key the key to disunify
     * @return the resulting persistent map
     * @throws NoSuchElementException the key is not in the map
     */
    fun disunion(key: K): PersistentDisjointMap<K, V>

    /**
     * Removes the specified [key] from this map.
     *
     * When the removed key was the final key in the component, the component is removed as well.
     *
     * @param key the key to remove
     * @return a result object with the resulting persistent map,
     * and a pair of the new representative key (or `null` when the component was removed) and its associated value
     */
    fun removeKey(key: K): Result<K, V, Pair<K?, V>?>

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
     * The builder exposes its modification operations through the [MutableDisjointMap] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, modifications applied do not affect previously built persistent map instances.
     */
    interface Builder<K, V>: MutableDisjointMap<K, V>, PersistentMap.Builder<K, V> {

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