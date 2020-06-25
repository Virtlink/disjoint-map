package com.virtlink.collections

@Suppress("unused", "ClassName")
open class MutableUnionFindMapTests : MutableDisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): MutableUnionFindMap<K, V> {
        return MutableUnionFindMap<K, V>().populate(initial)
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