package com.virtlink.collections2

import kotlinx.collections.immutable.PersistentSet

/**
 * A persistent data structure with disjoint sets.
 */
interface PersistentDisjointSet<E> : ImmutableDisjointSet<E> {

    /**
     * Unifies the sets that include the given elements.
     *
     * When one or both of the element don't exist in the data structure, they are added.
     *
     * @param element1 one element
     * @param element2 another element
     * @return the resulting persistent data structure
     */
    fun union(element1: E, element2: E): PersistentDisjointSet<E>

    /**
     * Copies the sets from this data structure to a new set of sets.
     *
     * @return the new set of sets
     */
    override fun toSets(): PersistentSet<Set<E>>

    /**
     * Returns a builder that can be used to perform efficient mutations on the map.
     *
     * @return the builder
     */
    fun builder(): Builder<E>

    /**
     * A generic builder for this persistent data structure.
     * The builder exposes its modification operations through the [MutableDisjointSet] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, modifications applied do not affect previously built persistent set instances.
     */
    interface Builder<E>: MutableDisjointSet<E> {

        /**
         * Returns a persistent disjoint map with the same contents as this builder.
         *
         * This method can be called multiple times.
         * @return the built persistent set
         */
        fun build(): PersistentDisjointSet<E>

    }

}