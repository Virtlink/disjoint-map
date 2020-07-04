package com.virtlink.collections

import java.util.*

fun <K, V> populate(sets: Iterable<DisjointSet<K, V>>, roots: MutableMap<K, V>, parents: MutableMap<K, K>, ranks: MutableMap<K, Int>) {
    val queue = LinkedList<K>()
    for ((keys, value) in sets) {
        if (keys.isEmpty()) continue
        queue.clear()
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
