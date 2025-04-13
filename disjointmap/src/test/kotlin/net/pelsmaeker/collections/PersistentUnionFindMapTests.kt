package net.pelsmaeker.collections

import io.kotest.core.spec.style.FunSpec
import kotlinx.collections.immutable.persistentMapOf

object PersistentUnionFindMapFactory: PersistentDisjointMapFactory {
    override fun <K, V> create(initial: Map<Set<K>, V>): PersistentDisjointMap<K, V> {
        val roots = persistentMapOf<K, V>().builder()
        val parents = persistentMapOf<K, K>().builder()
        val ranks = persistentMapOf<K, Int>().builder()

        populate(initial, roots, parents, ranks)

        return PersistentUnionFindMap(roots.build(), parents.build(), ranks.build())
    }
}

class PersistentUnionFindMapTests: FunSpec({
    include(testPersistentDisjointMap(PersistentUnionFindMapFactory))
})