package com.virtlink.collections

@Suppress("unused", "ClassName")
open class PersistentUnionFindMapTests : PersistentDisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): PersistentDisjointMap<K, V> {
        return PersistentUnionFindMap.of(initial)
    }

    // @formatter:off
    class `put()`               : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`put()`
    class `putComponent()`      : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`putComponent()`
    class `putAll()`            : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`putAll()`
    class `remove1()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`remove1()`
    class `remove2()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`remove2()`
    class `removeKey()`         : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`removeKey()`
    class `clear()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`clear()`
    class `union()`             : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`union()`
    class `disunion()`          : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`disunion()`
    class `setComponent()`      : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`setComponent()`
    class `compute()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`compute()`
    class `computeIfPresent()`  : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfPresent()`
    class `computeIfAbsent()`   : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`computeIfAbsent()`
    class `builder()`           : PersistentUnionFindMapTests(), PersistentDisjointMapTests.`builder()`
    // @formatter:on

}