package com.virtlink.collections2

/**
 * A mutable data structure with disjoint sets.
 */
interface MutableDisjointSet<E> : DisjointSet<E> {

    /**
     * Adds an element to this set.
     *
     * When the element is already in the set, nothing happens.
     *
     * @param element the element to add
     */
    fun add(element: E)

    /**
     * Unifies the sets that include the given elements.
     *
     * When one or both of the element don't exist in the data structure, they are added.
     *
     * @param element1 one element
     * @param element2 another element
     */
    fun union(element1: E, element2: E)

    /**
     * Copies the sets from this data structure to a new set of sets.
     *
     * @return the new set of sets
     */
    override fun toSets(): MutableSet<Set<E>>
}