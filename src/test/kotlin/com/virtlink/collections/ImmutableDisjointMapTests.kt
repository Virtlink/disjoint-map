package com.virtlink.collections

/**
 * Tests the [ImmutableDisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface ImmutableDisjointMapTests: DisjointMapTests {

    override fun <K, V> create(initial: Collection<Component<K, V>>): ImmutableDisjointMap<K, V>

}