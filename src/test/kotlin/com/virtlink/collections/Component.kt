package com.virtlink.collections


data class Component<K, V>(override val keys: Set<K>, override val value: V) : DisjointMap.Component<K, V> {
    companion object {
        fun <K, V> of(pair: Pair<Set<K>, V>): Component<K, V> =
            Component(pair.first, pair.second)

        fun <K, V> of(component: DisjointMap.Component<K, V>): Component<K, V> =
            Component(component.keys, component.value)
    }
    override fun toString(): String = "$keys=$value"
}