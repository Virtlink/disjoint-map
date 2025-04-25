package net.pelsmaeker.unifiers


/**
 * A mutable unifier.
 */
interface MutableUnifier<T, V: T>: Unifier<T, V> {

    /**
     * Changes this unifier to be the composition of this unifier with another unifier.
     *
     * @param other The other unifier to compose with.
     * @return `true` if the composition was successful; otherwise, `false`.
     * @throws CyclicTermException If the composition created a cyclic term.
     */
    fun composeWith(other: Unifier<T, V>): Boolean

    /**
     * Changes this unifier to include the given substitution.
     *
     * @param v The variable to add.
     * @param term The term to add.
     * @return `true` if the addition was successful; otherwise, `false`.
     * @throws CyclicTermException If the addition created a cyclic term.
     */
    fun add(v: V, term: T): Boolean

    /**
     * Changes this unifier to include the given substitutions.
     *
     * @param map The map of substitutions to add.
     * @return `true` if the additions were successful; otherwise, `false`.
     * @throws CyclicTermException If the addition created a cyclic term.
     */
    fun addAll(map: Map<V, T>): Boolean

    /**
     * Changes this unifier such that the given terms are equal.
     *
     * @param term1 The first term to unify.
     * @param term2 The second term to unify.
     * @return `true` if the unification was successful; otherwise, `false`.
     * @throws CyclicTermException If the unification created a cyclic term.
     */
    fun unify(term1: T, term2: T): Boolean
}
