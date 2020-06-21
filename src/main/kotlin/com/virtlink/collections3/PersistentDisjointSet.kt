package com.virtlink.collections3

/**
 * A persistent collection of elements in disjoint sets.
 */
interface PersistentDisjointSet<E> : com.virtlink.collections2.DisjointMap<E> {

    /**
     * Returns a new collection with the specified element added.
     *
     * If it is already in the collection, this collection is returned.
     * Otherwise, the element is added to its own set.
     *
     * @param element the element to add
     * @return the new collection
     */
    fun put(element: E): PersistentDisjointSet<E>

    /**
     * Returns a new collection with the specified element removed.
     *
     * If it is not in the collection, this collection is returned.
     * If the element is the last element of a set,
     * the set is removed as well.
     *
     * @return the new collection
     */
    fun remove(element: E): PersistentDisjointSet<E>

    /**
     * Returns an empty collection.
     *
     * @return the empty collection
     */
    fun clear(): PersistentDisjointSet<E>

    /**
     * Returns a new collection with the sets that include the given elements unified.
     *
     * When either element is not present, it is added.
     *
     * @param element1 one element
     * @param element2 another element
     * @return the new collection
     */
    fun union(element1: E, element2: E): PersistentDisjointSet<E>

    /**
     * Returns a new collection with the specified element disunified.
     *
     * This will create a new set with the given element.
     * When the element is not present, it is added.
     *
     * @param element the element to disunify
     * @return the new collection
     */
    fun disunion(element: E): PersistentDisjointSet<E>

}