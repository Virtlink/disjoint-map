package com.virtlink.collections

import io.kotest.core.spec.style.funSpec

interface ImmutableDisjointMapFactory {
    fun <K, V> create(initial: Map<Set<K>, V> = emptyMap()): ImmutableDisjointMap<K, V>
}

fun testImmutableDisjointMap(
    factory: ImmutableDisjointMapFactory,
) = funSpec {
    include(testDisjointMap(object: DisjointMapFactory {
        override fun <K, V> create(initial: Map<Set<K>, V>): DisjointMap<K, V> {
            return factory.create(initial)
        }
    }))
}