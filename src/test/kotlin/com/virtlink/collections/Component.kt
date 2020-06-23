package com.virtlink.collections


data class Component<K, V>(val keys: Set<K>, val value: V) {
    companion object {
        fun <K, V> of(pair: Pair<Set<K>, V>): Component<K, V> =
            Component(pair.first, pair.second)
    }
    override fun toString(): String = "$keys=$value"
}