package com.virtlink.collections

/**
 * Tests the [ImmutableDisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface ImmutableDisjointMapTests: DisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): ImmutableDisjointMap<K, V>

}