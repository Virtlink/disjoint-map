package com.virtlink.collections

/**
 * A transient disjoint map.
 */
interface TransientDisjointMap<K, V> : DisjointMap<K, V>, MutableMap<K, V> {

    /**
     * Unifies the components that include the given keys.
     *
     * @param key1 one key
     * @param key2 another key
     * @param default the function that provides a default value to use when no value is specified
     * @param unify the function that unifies the associated values of each of the components
     */
    fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V)

    /**
     * Disunifies the given key from the given component.
     *
     * This will create a new component with the given key and the value of the original component.
     *
     * @param key the key to disunify
     * @param component the component from which to disunify the key
     */
    fun disunion(key: K, component: K)

    /**
     * Sets the value associated with the component that includes the given key.
     *
     * @param key the key to look for
     * @param value the value to associate with the component
     */
    fun setComponent(key: K, value: V)

    /**
     * Removes the given element from the map.
     *
     * When the removed key was the final key in the component, the component is removed.
     *
     * @param key the element to remove
     * @return a pair of the new representative key (or `null` when the component was removed) and its associated value
     */
    fun removeKey(key: K): Pair<K?, V>

    /**
     * Computes the value associated with the component that includes the given key.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value (or `null` when it doesn't exist) to a new value
     * @return the computed value of the component that includes the given key
     */
    fun compute(key: K, mapping: (K, V?) -> V): V

    /**
     * Computes the value if the component that includes the given key does already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value to a new value
     * @return the computed value of the component that includes the given key; or `null` when it was not present
     */
    fun computeIfPresent(key: K, mapping: (K, V) -> V): V?

    /**
     * Computes the value if the component that includes the given key does not already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key to a value
     * @return the computed value of the component that includes the given element
     */
    fun computeIfAbsent(key: K, mapping: (K) -> V): V

}