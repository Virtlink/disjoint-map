package com.virtlink.collections3

/**
 * A mutable collection of elements in disjoint sets.
 */
interface MutableDisjointSet<E> : com.virtlink.collections2.DisjointMap<E> {

    /**
     * Puts the specified element in the collection.
     *
     * If it is already in the collection, nothing happens.
     * Otherwise, the element is added to its own set.
     *
     * @param element the element to add
     * @return `true` when the element was added;
     * otherwise, `false` when it was already present
     */
    fun put(element: E): Boolean

    /**
     * Removes the specified element from this collection.
     *
     * If the element is the last element of a set,
     * the set is removed as well.
     * If it is not in the collection, nothing happens.
     *
     * @return `true` when the element was removed;
     * otherwise, `false` when it was not present
     */
    fun remove(element: E): Boolean

    /**
     * Removes all sets from this collection.
     */
    fun clear()

    /**
     * Unifies the sets that include the given elements.
     *
     * When either element is not present, it is added.
     *
     * @param element1 one element
     * @param element2 another element
     * @return the representative element of the set
     */
    fun union(element1: E, element2: E)

    /**
     * Disunifies the given element from its set.
     *
     * This will create a new set with the given element
     * When the element is not present, it is added.
     *
     * @param element the element to disunify
     * @return `true` when the element was present;
     * otherwise, `false` when it was added
     */
    fun disunion(element: E)

}