package com.virtlink.collections

import com.virtlink.collections.DisjointMapTests

/**
 * Tests the [ImmutableDisjointMap] interface.
 */
@Suppress("ClassName")
interface ImmutableDisjointMapTests: DisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): ImmutableDisjointMap<K, V>

}