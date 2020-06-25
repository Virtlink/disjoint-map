package com.virtlink.collections

import kotlinx.collections.immutable.persistentMapOf

@Suppress("unused", "ClassName")
open class MutableUnionFindMapTests : MutableDisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): MutableUnionFindMap<K, V> {
        val roots = mutableMapOf<K, V>()
        val parents = mutableMapOf<K, K>()
        val ranks = mutableMapOf<K, Int>()

        populate(initial, roots, parents, ranks)

        return MutableUnionFindMap(roots, parents, ranks)
    }

    // @formatter:off
    class `set()`               : MutableUnionFindMapTests(), MutableDisjointMapTests.`set()`
    class `remove()`            : MutableUnionFindMapTests(), MutableDisjointMapTests.`remove()`
    class `clear()`             : MutableUnionFindMapTests(), MutableDisjointMapTests.`clear()`
    class `union()`             : MutableUnionFindMapTests(), MutableDisjointMapTests.`union()`
    class `disunion()`          : MutableUnionFindMapTests(), MutableDisjointMapTests.`disunion()`
    class `compute()`           : MutableUnionFindMapTests(), MutableDisjointMapTests.`compute()`
    class `computeIfPresent()`  : MutableUnionFindMapTests(), MutableDisjointMapTests.`computeIfPresent()`
    class `computeIfAbsent()`   : MutableUnionFindMapTests(), MutableDisjointMapTests.`computeIfAbsent()`
    // @formatter:on

}