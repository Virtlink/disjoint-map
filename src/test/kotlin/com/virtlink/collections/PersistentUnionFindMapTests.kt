package com.virtlink.collections

@Suppress("unused", "ClassName")
open class PersistentUnionFindMapTests : PersistentDisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): PersistentUnionFindMap<K, V> {
//        initial.forEach {
//            val duplicateKeys = it.keys.intersect(initial.filter { other -> it != other }.flatMap { other -> other.keys })
//            if (duplicateKeys.isNotEmpty())
//                throw IllegalArgumentException("Duplicate keys: $duplicateKeys")
//        }
//
//        return PersistentUnionFindMap.emptyOf<K, V>().putAllComponents(initial)
//        PersistentUnionFindMap.emptyOf<K, V>().union()
        TODO()
    }

    // @formatter:off
    class `put()`               : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`set()`
    class `remove1()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`remove()`
    class `clear()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`clear()`
    class `union()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`union()`
    class `disunion()`          : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`disunion()`
    class `compute()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`compute()`
    class `computeIfPresent()`  : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfPresent()`
    class `computeIfAbsent()`   : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfAbsent()`
    class `builder()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`builder()`
    // @formatter:on

}