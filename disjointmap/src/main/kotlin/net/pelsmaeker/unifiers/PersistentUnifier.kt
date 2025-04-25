package net.pelsmaeker.unifiers


/**
 * A persistent unifier.
 */
interface PersistentUnifier<T, V: T>: Unifier<T, V> {

    /**
     * Returns a new unifier that is the composition of this unifier with another unifier.
     *
     * @param other The other unifier to compose with.
     * @return The new unifier if the composition was successful; otherwise, `null`.
     * @throws CyclicTermException If the composition created a cyclic term.
     */
    fun composeWith(other: Unifier<T, V>): PersistentUnifier<T, V>?
            = mutate { composeWith(other) }

    /**
     * Returns a new unifier that includes the given substitution.
     *
     * @param v The variable to add.
     * @param term The term to add.
     * @return The new unifier if the addition was successful; otherwise, `null`.
     * @throws CyclicTermException If the addition created a cyclic term.
     */
    fun add(v: V, term: T): PersistentUnifier<T, V>?
            = mutate { add(v, term) }

    /**
     * Returns a new unifier that includes the given substitutions.
     *
     * @param map The map of substitutions to add.
     * @return The new unifier if the additions were successful; otherwise, `null`.
     * @throws CyclicTermException If the addition created a cyclic term.
     */
    fun addAll(map: Map<V, T>): PersistentUnifier<T, V>?
            = mutate { addAll(map) }

    /**
     * Returns a new unifier such that the given terms are equal.
     *
     * @param term1 The first term to unify.
     * @param term2 The second term to unify.
     * @return The new unifier if the unification was successful; otherwise, `false`.
     * @throws CyclicTermException If the unification created a cyclic term.
     */
    fun unify(term1: T, term2: T): Unifier<T, V>?
            = mutate { unify(term1, term2) }

    /**
     * Obtain a builder with the same contents as this unifier.
     *
     * The builder can be used to efficiently perform multiple modification operations.
     * Call the builder's [build] method to create a new unifier with the modifications.
     *
     * @return A new builder with the same contents as this unifier.
     */
    fun builder(): Builder<T, V>

    /**
     * A generic builder of a persistent unifier.
     * The builder exposes its modification operations through the [MutableUnifier] interface.
     *
     * Builders are reusable, that is, its [build] method can be called multiple times with modifications
     * between these calls. However, applied modifications do not affect previously built instances.
     */
    interface Builder<T, V: T>: MutableUnifier<T, V> {
        fun build(): PersistentUnifier<T, V>?
    }
}
