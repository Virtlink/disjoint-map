package com.virtlink.collections

fun <K, V> PersistentDisjointMap<K, V>.union(key1: K, key2: K): PersistentDisjointMap<K, V> {
    return this.union(key1, key2, { TODO() }) { _, _ -> throw IllegalStateException() }
}

fun <K, V> MutableDisjointMap<K, V>.union(key1: K, key2: K) {
    this.union(key1, key2, { TODO() }) { _, _ -> throw IllegalStateException() }
}

fun <K, V> Sequence<Map.Entry<K, V>>.toMap(): Map<K, V> {
    return this.map { e -> e.key to e.value }.toMap()
}

fun <K, V> Iterable<Map.Entry<K, V>>.toEntrySet(): Set<DisjointMapTests.Entry<K, V>> {
    return this.map { DisjointMapTests.Entry.of(it) }.toSet()
}
fun <K, V> Sequence<Map.Entry<K, V>>.toEntrySet(): Set<DisjointMapTests.Entry<K, V>> {
    return this.map { DisjointMapTests.Entry.of(it) }.toSet()
}