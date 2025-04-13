package com.virtlink.collections

import io.kotest.core.spec.style.FunSpec

object MutableUnionFindMapFactory: MutableDisjointMapFactory {
    override fun <K, V> create(initial: Map<Set<K>, V>): MutableDisjointMap<K, V> {
        val roots = mutableMapOf<K, V>()
        val parents = mutableMapOf<K, K>()
        val ranks = mutableMapOf<K, Int>()

        populate(initial, roots, parents, ranks)

        return MutableUnionFindMap(roots, parents, ranks)
    }
}

class MutableUnionFindMapTests: FunSpec({
    include(testMutableDisjointMap(MutableUnionFindMapFactory))
})