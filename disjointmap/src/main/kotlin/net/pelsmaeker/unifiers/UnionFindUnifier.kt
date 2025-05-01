package net.pelsmaeker.unifiers

import net.pelsmaeker.collections.MutableDisjointMap
import net.pelsmaeker.collections.MutableUnionFindMap

abstract class UnionFindUnifier<T, V: T>: MutableUnifier<T, V> {
    private val unionFind: MutableDisjointMap<V, T> = MutableUnionFindMap()

    override fun composeWith(other: Unifier<T, V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun add(v: V, term: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(map: Map<V, T>): Boolean {
        TODO()
    }

    override fun unify(term1: T, term2: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean = unionFind.isEmpty()

    override fun find(v: V): V? = unionFind.find(v)

    override fun get(term: T): T {
        val v = asVar(term) ?: return term
        return unionFind.getOrDefault(v, v)
    }

    override fun same(term1: T, term2: T): Boolean {
        return term1 == term2
            || unionFind.same(asVar(term1) ?: return false, asVar(term2) ?: return false)
    }

    override fun instantiate(term: T): T {
        val t = asVar(term)?.let { unionFind[it] ?: return it } ?: term
        val newSubterms = getSubterms(t).map { instantiate(it) }
        return withSubterms(t, newSubterms)
    }

    override fun getFreeVars(term: T): Set<V> {
        val t = asVar(term)?.let { unionFind[it] ?: return setOf(it) } ?: term
        val freeVars = getSubterms(t).map { getFreeVars(it) }
            return freeVars.fold(setOf()) { acc, set -> acc + set }
    }

    override fun isGround(term: T): Boolean = getFreeVars(term).isEmpty()

    override fun isCyclic(term: T): Boolean {
        TODO()
    }

    override fun equals(other: Any?): Boolean {
        if (other is UnionFindUnifier<*, *>) {
            return this.unionFind == other.unionFind
        } else if (other is MutableUnifier<*, *>) {
            TODO()
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return this.unionFind.hashCode()
    }

    /**
     * Returns the given term as a variable, if it is one.
     *
     * @param term The term to check.
     * @return The term as a variable, or `null` if the term is not a variable.
     */
    protected abstract fun asVar(term: T): V?

    /**
     * Determines whether the two terms are equal, ignoring their subterms.
     *
     * @param term1 The first term.
     * @param term2 The second term.
     * @return `true` if the two terms are equal, ignoring their subterms; otherwise, `false`.
     */
    protected abstract fun isEqualWithoutSubterms(term1: T, term2: T): Boolean

    /**
     * Gets the subterms of the given term.
     *
     * @param term The term for which to get the subterms.
     * @return The subterms of the term.
     */
    protected abstract fun getSubterms(term: T): List<T>

    /**
     * Creates a new term with the given subterms.
     *
     * @param term The term to copy.
     * @param subterms The subterms to use.
     * @return The new term with the given subterms.
     */
    protected abstract fun withSubterms(term: T, subterms: List<T>): T
}