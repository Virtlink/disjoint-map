package com.virtlink.collections


data class MutableComponent<K, V>(override val keys: Set<K>, override val value: V) : DisjointMap.Component<K, V> {
    companion object {
        fun <K, V> of(pair: Pair<Set<K>, V>): MutableComponent<K, V> =
            MutableComponent(pair.first, pair.second)

        fun <K, V> of(component: DisjointMap.Component<K, V>): MutableComponent<K, V> =
            MutableComponent(component.keys, component.value)
    }
    override fun toString(): String = "$keys=$value"
}