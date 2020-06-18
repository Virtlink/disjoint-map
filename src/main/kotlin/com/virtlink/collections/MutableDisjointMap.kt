package com.virtlink.collections

/**
 * A transient disjoint map.
 */
interface MutableDisjointMap<K, V> : DisjointMap<K, V>, MutableMap<K, V> {

    // MutableMap<K, V>
    /**
     * Makes a new component with the specified key and value.
     *
     * If the key is part of an existing component, it is disunioned.
     * If the key is not part of the map, it is added.
     *
     * @param key the key to set
     * @param value the value to set
     * @return the value associated with the previous component that contained the key,
     * or `null` if the key was not present in the map
     */
    override fun put(key: K, value: V): V?

    /**
     * Makes new components with the specified keys and values.
     *
     * If a key is part of an existing component, it is disunified.
     * If a key is not part of the map, it is added.
     *
     * @param from the map of keys and values to set
     */
    override fun putAll(from: Map<out K, V>)

    /**
     * Removes the specified key from this map.
     *
     * If the key is the last key of a component,
     * the component is removed as well.
     *
     * @return the value associated with the previous component that contained the key,
     * or `null` if the key was not present in the map
     */
    override fun remove(key: K): V?

    /**
     * Removes the specified key from this map only if the component to which it belongs
     * has the specified value.
     *
     * If the key is the last key of a component,
     * the component is removed as well.
     *
     * @return `true` if entry was removed; otherwise, `false`
     */
    override fun remove(key: K, value: V): Boolean {
        // See default implementation in JDK sources
        return true
    }

    /**
     * Removes all components from this map.
     */
    override fun clear()



    // MutableDisjointMap<K, V>
    /**
     * Gets a mutable set of components in this map.
     *
     * Changes to the mutable set or its components are reflected in this disjoint map.
     *
     * @return the components in this map
     */
    override val components: MutableSet<MutableComponent<K, V>>

    /**
     * Gets the mutable component that includes the given key.
     *
     * @param key the key to look for
     * @return the mutable component that includes the given key;
     * or `null` when the key is not in the map
     */
    override fun getComponent(key: K): MutableComponent<K, V>?

    /**
     * Puts the given component.
     *
     * Any keys in the component that are already part of other components
     * are removed from those components.
     *
     * @param keys the keys of the component to put
     * @param value the value of the component to put
     */
    fun putComponent(keys: Iterable<K>, value: V)

    /**
     * Puts the given components.
     *
     * Any keys in a component that are already part of other components
     * are removed from those components.
     *
     * @param map the map of components to put
     */
    fun putAllComponents(map: DisjointMap<K, V>)

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
     * Disunifies the given key from its component.
     *
     * This will create a new component with the given key and the value of the original component.
     * When the key doesn't exist, nothing happens.
     *
     * @param key the key to disunify
     */
    fun disunion(key: K)

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



    /**
     * Represents a mutable component held by a [MutableDisjointMap].
     */
    interface MutableComponent<K, V> : DisjointMap.Component<K, V> {

        /**
         * Gets the keys of this component.
         *
         * @return the keys of this component
         */
        override val keys: MutableSet<K>

        /**
         * Changes the value associated with this component.
         *
         * @return the previous value of the component
         */
        fun setValue(newValue: V): V
    }

}