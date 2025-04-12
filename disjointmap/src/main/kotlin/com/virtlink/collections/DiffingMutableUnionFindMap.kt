package com.virtlink.collections

/**
 * A mutable union-find map that tracks its changes.
 *
 * Given an underlying union-find map U, and this map M:
 * A key can be added to M, which is not found in U.
 */
class DiffingMutableUnionFindMap<K, V>: MutableDisjointMap<K, V> {
    override val size: Int
        get() = TODO("Not yet implemented")

    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun getOrDefault(key: K, defaultValue: V): V {
        TODO("Not yet implemented")
    }

    override fun find(key: K): K? {
        TODO("Not yet implemented")
    }

    override fun set(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun union(key1: K, key2: K, default: () -> V, compare: Comparator<K>, unify: (V, V) -> V) {
        TODO("Not yet implemented")
    }

    override fun disunion(key: K) {
        TODO("Not yet implemented")
    }

    override fun compute(key: K, mapping: (K, V?) -> V): V {
        TODO("Not yet implemented")
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
        TODO("Not yet implemented")
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
        TODO("Not yet implemented")
    }

    override fun toMap(): MutableMap<Set<K>, V> {
        TODO("Not yet implemented")
    }
}