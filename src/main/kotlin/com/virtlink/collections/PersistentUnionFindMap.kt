package com.virtlink.collections

import kotlinx.collections.immutable.*

/**
 * A union-find map.
 *
 * This implementation is thread-safe.
 *
 * @param _parents maps each key to its parent key.
 *   This map contains only entries for those keys that are not root keys.
 * @param _values maps each key to its value, if any.
 *   This map contains only entries for those keys that are root keys.
 * @param _ranks maps each key to its rank, which is the number of keys it has under it
 */
class PersistentUnionFindMap<K, V> private constructor(
    override var _parents: PersistentMap<K, K>,      // We replace this map when doing path compression.
    override val _values: PersistentMap<K, V>,
    override val _ranks: PersistentMap<K, Int>
) : UnionFindMapBase<K, V>(), PersistentDisjointMap<K, V> {

    companion object {
        private val EMPTY = PersistentUnionFindMap<Nothing, Nothing>(
            persistentMapOf(),
            persistentMapOf(),
            persistentMapOf()
        )
        @Suppress("UNCHECKED_CAST")
        fun <K, V> emptyOf(): PersistentUnionFindMap<K, V> = EMPTY as PersistentUnionFindMap<K, V>

        fun <K, V> of(map: Map<Set<K>, V>): PersistentUnionFindMap<K, V> {
            val parents = persistentMapOf<K, K>().builder()
            val values = persistentMapOf<K, V>().builder()
            val ranks = persistentMapOf<K, Int>().builder()
            for ((elems, v) in map) {
                if (elems.isEmpty()) continue
                val rep = elems.first()
                require(rep !in values && rep !in ranks) { "Element $rep was already in the map." }
                values[rep] = v
                ranks[rep] = elems.size
                for (e in elems.drop(1)) {
                    require(e !in values && e !in ranks) { "Element $e was already in the map." }
                    parents[e] = rep
                }
            }
            return PersistentUnionFindMap(
                parents.build(),
                values.build(),
                ranks.build()
            )
        }
    }

    override fun find(key: K): K {
        val parents = this._parents.builder()

        val rep = findMutable(key, parents)
        // This is the only place where we modify this persistent data structure.
        // In the case that multiple threads happen to run at the same time
        // trying to replace the this._parents map, one of them will win.
        // It does not matter which one wins, since each possible version of
        // the map encodes the same transitive relationship
        // between each non-representative key and the representative key.
        // Since replacing a reference to an object is atomic in the JVM,
        // this operation is thread-safe.
        //this._parents = parents.build()
        return rep
    }

    override fun union(a: K, b: K, default: () -> V, unify: (V, V) -> V): PersistentUnionFindMap<K, V> {
        val parents = this._parents.builder()
        val values = this._values.builder()
        val ranks = this._ranks.builder()

        unionMutable(a, b, default, unify, parents, values, ranks)

        return PersistentUnionFindMap(parents.build(), values.build(), ranks.build())
    }

//    override fun set(key: K, value: V): PersistentDisjointMap<K, V> {
//        val values = this._values.builder()
//
//        setComponentMutable(key, value, values)
//        return PersistentUnionFindMap(this._parents, values.build(), this._ranks)
//    }

    override fun remove(key: K): PersistentDisjointMap<K, V> {
        val parents = this._parents.builder()
        val values = this._values.builder()
        val ranks = this._ranks.builder()

        // TODO: Return new rep and value
        removeMutable(key, parents, values, ranks)

        return PersistentUnionFindMap(parents.build(), values.build(), ranks.build())
    }

    override fun builder(): PersistentDisjointMap.Builder<K, V> {
        val parents = this._parents.builder()
        val values = this._values.builder()
        val ranks = this._ranks.builder()

//        return Builder(parents, values, ranks)
        TODO()
    }


//    private class Builder<K, V>(
//        override val _parents: PersistentMap.Builder<K, K>,
//        override val _values: PersistentMap.Builder<K, V>,
//        override val _ranks: PersistentMap.Builder<K, Int>
//    ): UnionFindMapBase<K, V>(),
////        TransientDisjointMap<K, V>,
//        PersistentDisjointMap.Builder<K, V> {
//
//        override fun union(a: K, b: K, default: () -> V, unify: (V, V) -> V) {
//            unionMutable(a, b, default, unify, this._parents, this._values, this._ranks)
//        }
//
//        override fun find(key: K): K {
//            return findMutable(key, this._parents)
//        }
//
////        override fun set(key: K, value: V) {
////            setComponentMutable(key, value, this._values)
////        }
//
//        override fun remove(key: K): V? {
//            val oldEntry = removeMutable(key, this._parents, this._values, this._ranks)
//            return oldEntry?.second
//        }
//
//        override fun compute(key: K, mapping: (K, V?) -> V): V {
//            val rep = find(key)
//            val oldValue = get(rep)
//            val newValue = mapping(rep, oldValue)
//            setComponentMutableRep(rep, newValue, this._values)
//            return newValue
//        }
//
//        override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
//            val rep = find(key)
//            val oldValue = get(rep) ?: return null
//            val newValue = mapping(rep, oldValue)
//            setComponentMutableRep(rep, newValue, this._values)
//            return newValue
//        }
//
//        override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
//            val rep = find(key)
//            val oldValue = get(rep)
//            if (oldValue != null) return oldValue
//            val newValue = mapping(rep)
//            setComponentMutableRep(rep, newValue, this._values)
//            return newValue
//        }
//
//        override fun build(): PersistentDisjointMap<K, V> {
//            return PersistentUnionFindMap(
//                this._parents.build(),
//                this._values.build(),
//                this._ranks.build()
//            )
//        }
//
////        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
////            get() = TODO("Not yet implemented")
////        override val keys: MutableSet<K>
////            get() = TODO("Not yet implemented")
////        override val values: MutableCollection<V>
////            get() = TODO("Not yet implemented")
//        override val components: ImmutableMap<Set<K>, V>
//            get() = TODO("Not yet implemented")
//
//        override fun getComponentSize(key: K): Int {
//            TODO("Not yet implemented")
//        }
//
//        override fun disunion(key: K, component: K) {
//            TODO("Not yet implemented")
//        }
//
//        override fun setComponent(key: K, value: V) {
//            TODO("Not yet implemented")
//        }
//
//        override fun removeKey(key: K): Pair<K?, V> {
//            TODO("Not yet implemented")
//        }
//
//        override fun clear() {
//            TODO("Not yet implemented")
//        }
//
//        override fun put(key: K, value: V): V? {
//            TODO("Not yet implemented")
//        }
//
//        override fun putAll(from: Map<out K, V>) {
//            TODO("Not yet implemented")
//        }
//    }

    override fun remove(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override val components: ImmutableMap<Set<K>, V>
        get() = TODO("Not yet implemented")

    override fun getComponentSize(key: K): Int {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun putComponent(keys: Set<K>, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun putAll(m: Map<out K, V>): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun clear(): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun disunion(key: K): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun setComponent(key: K, value: V): PersistentDisjointMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun removeKey(key: K): PersistentDisjointMap.Result<K, V, Pair<K?, V>?> {
        TODO("Not yet implemented")
    }

    override fun compute(key: K, mapping: (K, V?) -> V): PersistentDisjointMap.Result<K, V, V> {
        TODO("Not yet implemented")
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): PersistentDisjointMap.Result<K, V, V?> {
        TODO("Not yet implemented")
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): PersistentDisjointMap.Result<K, V, V> {
        TODO("Not yet implemented")
    }

}