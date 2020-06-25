package com.virtlink.collections2

/**
 * A data structure with disjoint sets.
 */
interface DisjointSet<E> {

    /**
     * Gets the number of elements in the data structure.
     */
    val size: Int

    /**
     * Determines whether the data structure is empty.
     */
    fun isEmpty(): Boolean = (size == 0)

    /**
     * Finds the representative element of the set that includes the given element.
     *
     * @param element the element to look for
     * @return the representative element of the set that includes the given element;
     * which may be the element itself; or `null` when the element is not in the set
     */
    fun find(element: E): E?

    /**
     * Whether the data structure contains the specified element.
     *
     * @param element the element to look for
     * @return `true` when the data structure contains the specified element;
     * otherwise, `false`
     */
    operator fun contains(element: E): Boolean {
        return find(element) != null
    }

    /**
     * Determines whether the given elements are in the same set.
     *
     * @param element1 one element
     * @param element2 another element
     * @return `true` when the elements are present and part of the same set;
     * otherwise, `false`
     */
    fun same(element1: E, element2: E): Boolean {
        val rep1 = find(element1)
        val rep2 = find(element2)
        return rep1 != null && rep1 == rep2
    }

    /**
     * Copies the sets from this data structure to a new set of sets.
     *
     * @return the new set of sets
     */
    fun toSets(): Set<Set<E>>

}