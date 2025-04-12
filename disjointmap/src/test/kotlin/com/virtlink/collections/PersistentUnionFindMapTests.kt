package com.virtlink.collections

import kotlinx.collections.immutable.persistentMapOf

@Suppress("unused", "ClassName")
open class PersistentUnionFindMapTests : PersistentDisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): PersistentUnionFindMap<K, V> {
        val roots = persistentMapOf<K, V>().builder()
        val parents = persistentMapOf<K, K>().builder()
        val ranks = persistentMapOf<K, Int>().builder()

        populate(initial, roots, parents, ranks)

        return PersistentUnionFindMap(roots.build(), parents.build(), ranks.build())
    }

    // @formatter:off
    class `set()`               : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`set()`
    class `remove()`            : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`remove()`
    class `clear()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`clear()`
    class `union()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`union()`
    class `disunion()`          : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`disunion()`
    class `compute()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`compute()`
    class `computeIfPresent()`  : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfPresent()`
    class `computeIfAbsent()`   : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfAbsent()`
    class `builder()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`builder()`
    // @formatter:on

}