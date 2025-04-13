package net.pelsmaeker.collections

/**
 * A transient disjoint map.
 *
 * @param K The type of keys.
 * @param V The type of values.
 */
interface MutableDisjointMap<K, V> : DisjointMap<K, V> {

    /**
     * Sets the [value] of the set that contains the specified [key].
     *
     * If the key is not part of this map, it is added.
     *
     * @param key The key.
     * @param value The value to associate with the set that contains the specified [key].
     * @return The value associated with the previous set that contained the key,
     * or `null` if the key was not present in the map.
     */
    operator fun set(key: K, value: V): V?

    /**
     * Removes the specified [key] from this map.
     *
     * When the removed key was the final key in the set,
     * the set is removed as well.
     *
     * @return The value associated with the previous set that contained the key,
     * or `null` if the key was not present in the map.
     */
    fun remove(key: K): V?

    /**
     * Removes all sets from this map.
     */
    fun clear()

    /**
     * Unifies the sets that include the given keys.
     *
     * When one or both of the keys don't exist in the map, they are added.
     *
     * @param key1 One key.
     * @param key2 Another key.
     * @param default Function that provides a default value to use when no value is specified.
     * @param compare Function that compares the keys: the higher is used as the new representative;
     * or 0 to use the rank to determine this.
     * @param unify Function that unifies the associated values of each of the sets,
     * where the first value is from the representative.
     */
    fun union(key1: K, key2: K, default: () -> V, compare: Comparator<K> = Comparator { _, _ -> 0 }, unify: (V, V) -> V)

    /**
     * Disunifies the given key from its set.
     *
     * This will create a new set with the given key and the value of the original set.
     *
     * @param key The key to disunify.
     * @throws NoSuchElementException the key is not in the map
     */
    fun disunion(key: K)

    /**
     * Computes the value associated with the set that includes the given key.
     *
     * @param key The key to find.
     * @param mapping The mapping from the representative key and its existing value (or `null` when it doesn't exist) to a new value.
     * @return The computed value of the set that includes the given key.
     */
    fun compute(key: K, mapping: (K, V?) -> V): V

    /**
     * Computes the value if the set that includes the given key does already have an associated value.
     *
     * @param key The key to find.
     * @param mapping The mapping from the representative key and its existing value to a new value.
     * @return The computed value of the set that includes the given key; or `null` when it was not present.
     */
    fun computeIfPresent(key: K, mapping: (K, V) -> V): V?

    /**
     * Computes the value if the set that includes the given key does not already have an associated value.
     *
     * @param key The key to find.
     * @param mapping The mapping from the representative key to a value.
     * @return The computed value of the set that includes the given element.
     */
    fun computeIfAbsent(key: K, mapping: (K) -> V): V

    /**
     * Copies the sets from this disjoint map to a new map.
     *
     * @return The new map.
     */
    override fun toMap(): MutableMap<Set<K>, V>
}