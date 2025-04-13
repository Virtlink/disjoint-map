package net.pelsmaeker.collections

import kotlinx.collections.immutable.PersistentMap

/**
 * A persistent disjoint map.
 *
 * @param K the type of keys
 * @param V the type of values
 */
interface PersistentDisjointMap<K, V> : ImmutableDisjointMap<K, V> {

    /**
     * Sets the [value] of the set that contains the specified [key].
     *
     * If the key is not part of this map, it is added.
     *
     * @param key the key
     * @param value the value to associate with the set that contains the specified [key]
     * @return the resulting persistent map
     */
    operator fun set(key: K, value: V): PersistentDisjointMap<K, V>

    /**
     * Removes the specified [key] from this map.
     *
     * When the removed key was the final key in the set,
     * the set is removed as well.
     *
     * @param key the key to remove
     * @return the resulting persistent map
     */
    fun remove(key: K): PersistentDisjointMap<K, V>

    /**
     * Removes all sets from this map.
     *
     * @return the resulting persistent map
     */
    fun clear(): PersistentDisjointMap<K, V>

    /**
     * Unifies the sets that include the given keys.
     *
     * When one or both of the keys don't exist in the map, they are added.
     *
     * @param key1 one key
     * @param key2 another key
     * @param default function that provides a default value to use when no value is specified
     * @param compare function that compares the keys: the higher is used as the new representative;
     * or 0 to use the rank to determine this
     * @param unify function that unifies the associated values of each of the sets,
     * where the first value is from the representative
     * @return the resulting persistent map
     */
    fun union(key1: K, key2: K, default: () -> V, compare: Comparator<K> = Comparator { _, _ -> 0 }, unify: (V, V) -> V): PersistentDisjointMap<K, V>

    /**
     * Disunifies the given key from its set.
     *
     * This will create a new set with the given key and the value of the original set.
     *
     * @param key the key to disunify
     * @return the resulting persistent map
     * @throws NoSuchElementException the key is not in the map
     */
    fun disunion(key: K): PersistentDisjointMap<K, V>

    /**
     * Computes the value associated with the set that includes the given key.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value (or `null` when it doesn't exist) to a new value
     * @return0a result object with the resulting persistent map,
     * and the computed value of the set that includes the given key
     */
    fun compute(key: K, mapping: (K, V?) -> V): Result<K, V, V>

    /**
     * Computes the value if the set that includes the given key does already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key and its existing value to a new value
     * @return a result object with the resulting persistent map,
     * and the computed value of the set that includes the given key; or `null` when it was not present
     */
    fun computeIfPresent(key: K, mapping: (K, V) -> V): Result<K, V, V?>

    /**
     * Computes the value if the set that includes the given key does not already have an associated value.
     *
     * @param key the key to find
     * @param mapping the mapping from the representative key to a value
     * @return a result object with the resulting persistent map,
     * and the computed value of the set that includes the given element
     */
    fun computeIfAbsent(key: K, mapping: (K) -> V): Result<K, V, V>

    /**
     * Copies the sets from this disjoint map to a new map.
     *
     * @return the new map
     */
    override fun toMap(): PersistentMap<Set<K>, V>

    /**
     * Returns a builder that can be used to perform efficient mutations on the map.
     *
     * @return the builder
     */
    fun builder(): Builder<K, V>

    /**
     * A generic builder of the persistent disjoint map.
     * The builder exposes its modification operations through the [MutableDisjointMap] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, modifications applied do not affect previously built persistent map instances.
     */
    interface Builder<K, V>: MutableDisjointMap<K, V> {

        /**
         * Returns a persistent disjoint map with the same contents as this builder.
         *
         * This method can be called multiple times.
         */
        fun build(): PersistentDisjointMap<K, V>

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