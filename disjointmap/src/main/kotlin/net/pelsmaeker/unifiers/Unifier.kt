package net.pelsmaeker.unifiers


/**
 * A unifier is the substitution that makes terms equal.
 *
 * @param T The type of terms.
 * @param V The type of variables.
 */
interface Unifier<T, V: T> {

//    /** The number of entries in this unifier. */
//    val size: Int

    /**
     * Determines whether this unifier is empty.
     *
     * @return `true` when this unifier is empty; otherwise, `false`.
     */
    fun isEmpty(): Boolean

    /**
     * Determines whether this unifier is not empty.
     *
     * @return `true` when this unifier is not empty; otherwise, `false`.
     */
    fun isNotEmpty(): Boolean = !isEmpty()

//    /** The entries of this unifier. */
//    val entries: Map<V, T>

    /**
     * Find the representative variable for the given variable.
     *
     * @param v The variable to find the representative for.
     * @return The representative variable; or `null` when the variable is not in the unifier.
     */
    fun find(v: V): V?

    /**
     * Find the representative term for the given term.
     *
     * @param term The term to find the representative for.
     * @return The representative term if [term] is a term variable and occurs in this unifier;
     * otherwise, [term] itself.
     */
    operator fun get(term: T): T

    /**
     * Determines whether the unifier contains the specified variable.
     *
     * @param v The variable to check.
     * @return `true` when the unifier contains the specified variable;
     * otherwise, `false`.
     */
    operator fun contains(v: V): Boolean = find(v) != null

    /**
     * Checks whether the given term is equal to another term.
     *
     * This method checks whether the two terms are equal after applying the unifier.
     *
     * @param term1 The first term to check.
     * @param term2 The second term to check.
     * @return `true` if the terms are equal after applying the unifier; otherwise, `false`.
     */
    fun same(term1: T, term2: T): Boolean

    /**
     * Recursively instantiate the given term using this unifier.
     *
     * This method replaces all variables in the term with their corresponding terms
     * from the unifier.
     *
     * @param term The term to instantiate.
     * @return The recursively instantiated term.
     * @throws UnsupportedOperationException If the term is cyclic.
     */
    fun instantiate(term: T): T

    /**
     * Gets the set of free variables in the given term relative to this unifier.
     *
     * This method returns the set of variables that are not fully instantiated
     * by this unifier.
     *
     * @param term The term to check.
     * @return The set of variables in the term.
     */
    fun getFreeVars(term: T): Set<V>

    /**
     * Checks whether the given term is a ground term relative to this unifier.
     *
     * A ground term is a term that contains no variables.
     *
     * @param term The term to check.
     * @return `true` if the term is a ground term; otherwise, `false`.
     */
    fun isGround(term: T): Boolean

    /**
     * Checks whether the given term is cyclic relative to this unifier.
     *
     * A cyclic term is a term that contains itself as a subterm.
     *
     * @param term The term to check.
     * @return `true` if the term is cyclic; otherwise, `false`.
     */
    fun isCyclic(term: T): Boolean

    // TODO: Add empty() unifier
}