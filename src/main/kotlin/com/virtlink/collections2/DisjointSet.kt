package com.virtlink.collections2

/**
 * A collection of elements in disjoint sets.
 */
interface DisjointSet<E> {

    /**
     * Gets the number of sets in the collection.
     */
    val size: Int

    /**
     * Determines whether the collection is empty.
     */
    fun isEmpty(): Boolean

    /**
     * Finds the representative element of the set that includes the given element.
     *
     * @param element the element to look for
     * @return the representative element of the component that includes the given element;
     * which may be the element itself; or `null` when the element is not in the collection
     */
    fun find(element: E): E?

    /**
     * Whether the collection contains the specified elemnet.
     *
     * @return `true` when the collection contains the specified element;
     * otherwise, `false`
     */
    fun contains(element: E): Boolean

    /**
     * Determines whether the given elements are in the same set.
     *
     * @param element1 one element
     * @param element2 another element
     * @return `true` when the elements are present and part of the same set;
     * otherwise, `false`
     */
    fun same(element1: E, element2: E): Boolean

    /**
     * Gets the size of the set that includes the given element.
     *
     * @param element the element to look for
     * @return the number of elements in the set that includes the given element,
     * including the element itself; or 0 when the element is not in the collection
     */
    fun getSize(element: E): Int

}