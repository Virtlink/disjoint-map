package com.virtlink.collections

import kotlinx.collections.immutable.ImmutableMap

/**
 * An immutable disjoint map.
 */
interface ImmutableDisjointMap<K, out V> : DisjointMap<K, V>, ImmutableMap<K, V> {


}