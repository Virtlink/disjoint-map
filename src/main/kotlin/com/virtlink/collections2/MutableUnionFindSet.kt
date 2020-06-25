package com.virtlink.collections2

class MutableUnionFindSet<E>: MutableDisjointSet<E> {
    private val innerSet: IntUnionFindSet = IntUnionFindSet()
    private val elementToIndex: MutableMap<E, Int> = mutableMapOf()
    private val indexToElement: MutableList<E> = mutableListOf()

    override val size: Int
        get() = innerSet.size

    override fun find(element: E): E? {
        val index = elementToIndex[element] ?: return null
        val repIndex = innerSet.find(index) ?: return null
        return indexToElement[repIndex]
    }

    override fun add(element: E) {
        findOrAdd(element)
    }

    override fun union(element1: E, element2: E) {
        val index1 = elementToIndex[element1] ?: return
        val index2 = elementToIndex[element2] ?: return
        innerSet.union(index1, index2)
    }

    override fun toSets(): MutableSet<Set<E>> {
        TODO("Not yet implemented")
    }

    private fun findOrAdd(element: E): Int {
        val index = elementToIndex[element]
        if (index != null) return index

        val newIndex = size
        innerSet.add(newIndex)
        elementToIndex[element] = size
        indexToElement.add(element)
        return newIndex
    }

}