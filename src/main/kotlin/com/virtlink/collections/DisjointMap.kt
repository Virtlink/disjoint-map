package com.virtlink.collections

/**
 * A map of key in disjoint sets with a value.
 *
 * @param K the type of keys
 * @param V the type of values
 */
interface DisjointMap<K, out V> {

    /** The number of sets in the collection. */
    val size: Int

    /** Determines whether the collection is empty. */
    fun isEmpty(): Boolean = (size == 0)

    /** Determines whether the collection is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /**
     * Gets the value associated with the set that contains the specified key;
     * or `null` when the key is not in the map.
     *
     * @param key the key whose associated value to get
     * @return the associated value; or `null` when the key is not in the map
     */
    operator fun get(key: K): V?

    /**
     * Gets the value associated with the set that contains the specified key;
     * or the default value when the key is not in the map.
     *
     * @param key the key whose associated value to get
     * @return the associated value; or [defaultValue] when the key is not in the map
     */
    fun getOrDefault(key: K, defaultValue: @UnsafeVariance V): V

    /**
     * Finds the representative of the set that includes the given key.
     *
     * @param key the key to look for
     * @return the representative of the set that includes the given key;
     * which may be the key itself; or `null` when the key is not in the map
     */
    fun find(key: K): K?

    /**
     * Whether the collection contains the specified key.
     *
     * @return `true` when the collection contains the specified key;
     * otherwise, `false`
     */
    operator fun contains(key: K): Boolean {
        return find(key) != null
    }

    /**
     * Determines whether the given keys are in the same set.
     *
     * @param key1 one key
     * @param key2 another key
     * @return `true` when the keys are present and part of the same set;
     * otherwise, `false`
     */
    fun same(key1: K, key2: K): Boolean {
        val rep1 = find(key1)
        val rep2 = find(key2)
        return rep1 != null && rep1 == rep2
    }

    /**
     * Copies the sets from this disjoint map to a new map.
     *
     * @return the new map
     */
    fun toMap(): Map<Set<K>, V>

}