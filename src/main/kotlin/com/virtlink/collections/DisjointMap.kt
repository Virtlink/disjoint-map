package com.virtlink.collections

/**
 * A map of elements in disjoint components.
 */
interface DisjointMap<K, out V>: Map<K, V> {

    /**
     * Gets a set of components in this map.
     *
     * @return the components in this map
     */
    val components: Set<Component<K, V>>

    /**
     * Finds the representative of the component that includes the given key.
     *
     * @param key the key to look for
     * @return the representative of the component that includes the given key;
     * which may be the key itself; or `null` when the key is not in the map
     */
    fun find(key: K): K?

    /**
     * Determines whether the given keys are in the same component.
     *
     * @param key1 one key
     * @param key2 another key
     * @return `true` when the keys are present and part of the same component;
     * otherwise, `false`
     */
    fun same(key1: K, key2: K): Boolean

    /**
     * Gets the component that includes the given key.
     *
     * @param key the key to look for
     * @return the component that includes the given key;
     * or `null` when the key is not in the map
     */
    fun getComponent(key: K): Component<K, V>?

    /**
     * Gets the size of the component that includes the given key.
     *
     * @param key the key to look for
     * @return the number of keys in the component that includes the given key,
     * including the key itself; or 0 when the key is not in the map
     */
    fun getComponentSize(key: K): Int

    /**
     * Represents a component held by a [DisjointMap].
     */
    interface Component<out K, out V> {

        /**
         * Gets the keys of this component.
         *
         * @return the keys of this component
         */
        val keys: Set<K>

        /**
         * Gets the value of this component.
         *
         * @return the value of the component
         */
        val value: V

        /**
         * Gets the keys of this component.
         */
        operator fun component1() = keys

        /**
         * Gets the value of this component.
         */
        operator fun component2() = value
    }

}