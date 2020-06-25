package com.virtlink.collections2

import kotlinx.collections.immutable.ImmutableSet

/**
 * An immutable data structure with disjoint sets.
 */
interface ImmutableDisjointSet<E> : DisjointSet<E> {

    /**
     * Copies the sets from this data structure to a new set of sets.
     *
     * @return the new set of sets
     */
    override fun toSets(): ImmutableSet<Set<E>>

}