package com.virtlink.collections2

import com.virtlink.N

/**
 * An implementation of union-find that works on integers.
 */
class IntUnionFindSet: MutableDisjointSet<Int> {

    /**
     * The zero-based index of the parent element for the given element.
     * Or its own index for root elements.
     * Or -1 for non-existing elements.
     * The size of this list is the same as [ranks].
     */
    private val parents: MutableList<Int> = mutableListOf()
    /**
     * The ranks of the elements.
     * The size of this list is the same as [parents].
     */
    private val ranks: MutableList<Int> = mutableListOf()

    override val size: Int
        get() = parents.size

    override fun find(element: Int): Int? {
        if (element < 0 || element >= size) return null

        val parent = this.parents[element]
        if (parent == -1) return null   // Not found

        return if (parent == element) {
            // Element is representative
            element
        } else {
            // Element is not representative
            val rep = N.assumeNotNull(find(parent))
            if (rep != parent) {
                // Update the parent to point to the representative directly
                this.parents[element] = rep
            }
            rep
        }
    }

    override fun add(element: Int) {
        findOrAdd(element)
    }

    override fun union(element1: Int, element2: Int) {
        val rep1 = findOrAdd(element1)
        val rep2 = findOrAdd(element2)

        val rank1 = this.ranks[rep1]
        val rank2 = this.ranks[rep2]
        if (rank1 < rank2) {
            this.parents[rep1] = rep2
        } else {
            this.parents[rep2] = rep1
            if (rank1 == rank2) {
                this.ranks[rep2] += 1
            }
        }
    }

    override fun toSets(): MutableSet<Set<Int>> {
        TODO()
    }

    private fun findOrAdd(element: Int): Int {
        val rep = find(element)
        if (rep == null) {
            // Add extra empty elements
            for (i in this.parents.size..element) {
                this.parents.add(-1)
                this.ranks.add(0)
            }
            this.parents[element] = element
            this.ranks[element] = 0
            return element
        }
        return rep
    }

}