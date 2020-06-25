package com.virtlink.collections

import java.util.*

//fun <K, V> createMap(components: Iterable<Pair<Set<K>, V>>): ImmutableDisjointMap<K, V> = object: ImmutableDisjointMap<K, V> {
//
//}

fun <K, V, T : PersistentDisjointMap<K, V>> T.populate(sets: Iterable<DisjointSet<K, V>>): T {
    @Suppress("UNCHECKED_CAST")
    return this.builder().apply {
        for ((keys, value) in sets) {
            val rep = keys.firstOrNull() ?: continue
            for (key in keys) {
                union(rep, key, { value }, { _, _ -> value })
            }
        }
    }.build() as T
}

fun <K, V, T : MutableDisjointMap<K, V>> T.populate(sets: Iterable<DisjointSet<K, V>>): T {
    for ((keys, value) in sets) {
        val rep = keys.firstOrNull() ?: continue
        for (key in keys) {
            union(rep, key, { value }, { _, _ -> value })
        }
    }
    return this
}

fun <K, V> populate(sets: Iterable<DisjointSet<K, V>>, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>) {
    val queue = LinkedList<K>()
    for ((keys, value) in sets) {
        if (keys.isEmpty()) continue
        val rep = keys.first()
        roots[rep] = value
        queue.add(rep)
        queue.add(rep)

        for (key in keys.drop(1)) {
            val parent = queue.remove()
            parents[key] = parent
            // FIXME: Ranks are incorrect
            ranks[parent] = (ranks[parent] ?: 1) + 1
            queue.add(key)
            queue.add(key)
        }
    }
}

fun <K, V> PersistentDisjointMap<K, V>.union(key1: K, key2: K): PersistentDisjointMap<K, V> {
    return this.union(key1, key2, { TODO() }) { _, _ -> throw IllegalStateException() }
}

fun <K, V> MutableDisjointMap<K, V>.union(key1: K, key2: K) {
    this.union(key1, key2, { TODO() }) { _, _ -> throw IllegalStateException() }
}

//fun <K, V> Sequence<Map.Entryry<K, V>>.toMap(): Map<K, V> {
//    return this.map { e -> e.key to e.value }.toMap()
//}
//
//fun <K, V> Iterable<Map.Entry<K, V>>.toEntrySet(): Set<DisjointMapTests.Entry<K, V>> {
//    return this.map { DisjointMapTests.Entry.of(it) }.toSet()
//}
//fun <K, V> Sequence<Map.Entry<K, V>>.toEntrySet(): Set<DisjointMapTests.Entry<K, V>> {
//    return this.map { DisjointMapTests.Entry.of(it) }.toSet()
//}
//
//fun <T> Iterable<T>.iterateToSet(): Set<T> {
//    return this.iterator().asSequence().toSet()
//}
//fun <T> Iterable<T>.iterateToList(): List<T> {
//    return this.iterator().asSequence().toList()
//}