package com.virtlink.collections.impl

import com.virtlink.collections.MutableDisjointMap
import java.lang.IllegalArgumentException

/*abstract*/ class AbstractMutableDisjointMap<K, V> : MutableDisjointMap<K, V> {


    // Map<K, V>
    override val size: Int get() = keys.size

    override operator fun get(key: K): V? {
        val rep = find(key) ?: return null
        return this._roots[rep];
    }

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V): V {
        val rep = find(key) ?: return defaultValue
        return this._roots[rep] ?: defaultValue
    }


    // DisjointMap<K, V>
    override fun find(key: K): K? {
        return findMutable(key, this._roots, this._parents, this._ranks)
    }


    // MutableMap<K, V>
    private var _keys: MutableSet<K>? = null
    override val keys: MutableSet<K> get() {
        if (_keys == null) _keys = Keys()
        return _keys!!
    }

    private var _values: MutableCollection<V>? = null
    override val values: MutableCollection<V> get() {
        if (_values == null) _values = Values()
        return _values!!
    }

    private var _entries: MutableSet<MutableMap.MutableEntry<K, V>>? = null
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() {
        if (_entries == null) _entries = Entries()
        return _entries!!
    }

    override fun put(key: K, value: V): V? {
        disunion(key)
        // Either the key was in the map and now is the root of its own component,
        // or the key was not in the map and nothing changed.
        val oldValue = get(key)
        _roots[key] = value
        return oldValue
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { put(it.key, it.value) }
    }

    override fun remove(key: K): V? {
        val (_, oldValue) = removeMutable(key, null, false, this._roots, this._parents, this._ranks) ?: return null
        return oldValue
    }

    override fun remove(key: K, value: V): Boolean {
        removeMutable(key, value, true, this._roots, this._parents, this._ranks) ?: return false
        return true
    }

    override fun clear() {
        this._roots.clear()
        this._parents.clear()
        this._ranks.clear()
    }


    // MutableDisjointMap<K, V>
    private var _components: MutableSet<MutableDisjointMap.MutableComponent<K, V>>? = null
    override val components: MutableSet<MutableDisjointMap.MutableComponent<K, V>> get() {
        if (_components == null) _components = Components()
        return _components!!
    }

    override fun getComponent(key: K): MutableDisjointMap.MutableComponent<K, V>? {
        val rep = find(key) ?: return null
        val keys = this.keys.filter { find(it) == rep }.union(listOf(rep))
        val value = get(key)
        TODO()
        //return MutableDisjointMap.MutableComponent(keys, value)
    }

    override fun putComponent(component: MutableDisjointMap.MutableComponent<K, V>) {
        TODO()
        val keys = component.keys
        if (keys.isEmpty()) return
        val rep = keys.first()
        require(rep !in _roots && rep !in _ranks) { "Element $rep was already in the map." }
        _roots[rep] = component.value
        _ranks[rep] = keys.size
        for (e in keys.drop(1)) {
            require(e !in _roots && e !in _ranks) { "Element $e was already in the map." }
            _parents[e] = rep
        }
    }

    override fun putAllComponents(components: Iterable<MutableDisjointMap.MutableComponent<K, V>>) {
        components.forEach { putComponent(it) }
    }

    override fun union(key1: K, key2: K, default: () -> V, unify: (V, V) -> V) {
        unionMutable(key1, key1, default, unify, this._roots, this._parents, this._ranks)
    }

    override fun disunion(key: K) {
        disunionMutable(key, this._roots, this._parents, this._ranks)
    }

    override fun compute(key: K, mapping: (K, V?) -> V): V {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        val newValue = mapping(rep, oldValue)
        setComponentMutableRep(rep, newValue, this._roots)
        return newValue
    }

    override fun computeIfPresent(key: K, mapping: (K, V) -> V): V? {
        val rep = find(key) ?: key
        val oldValue = get(rep) ?: return null
        val newValue = mapping(rep, oldValue)
        setComponentMutableRep(rep, newValue, this._roots)
        return newValue
    }

    override fun computeIfAbsent(key: K, mapping: (K) -> V): V {
        val rep = find(key) ?: key
        val oldValue = get(rep)
        if (oldValue != null) return oldValue
        val newValue = mapping(rep)
        setComponentMutableRep(rep, newValue, this._roots)
        return newValue
    }


    /**
     * Inner class used to give a view of the keys in the map.
     */
    private inner class Keys: AbstractMutableSet<K>() {

        override val size: Int
            get() = this@MutableUnionFindMap._roots.size + this@MutableUnionFindMap._parents.size

        override fun add(element: K): Boolean {
            throw UnsupportedOperationException("Add is not supported on keys")
        }

        override fun iterator(): MutableIterator<K> = object: MutableIterator<K> {
            var lastKey: K? = null
            var iterator1: Iterator<K>? = this@MutableUnionFindMap._roots.keys.iterator()
            var iterator2: Iterator<K>? = this@MutableUnionFindMap._parents.keys.iterator()

            override fun hasNext(): Boolean {
                val iterator1 = this.iterator1
                if (iterator1 != null) return iterator1.hasNext()
                val iterator2 = this.iterator2
                if (iterator2 != null) return iterator2.hasNext()
                return false
            }

            override fun next(): K {
                val iterator1 = this.iterator1
                if (iterator1 != null) {
                    if (iterator1.hasNext()) {
                        val element = iterator1.next()
                        lastKey = element
                        return element
                    }
                    this.iterator1 = null
                }
                val iterator2 = this.iterator2
                if (iterator2 != null) {
                    if (iterator2.hasNext()) {
                        val element = iterator2.next()
                        lastKey = element
                        return element
                    }
                    this.iterator2 = null
                }
                throw NoSuchElementException()
            }

            override fun remove() {
                val element = lastKey ?: throw IllegalArgumentException()
                // FIXME: This is not correct, it will cause a concurrent modification exception
                this@MutableUnionFindMap.remove(element)
                TODO("Not yet implemented")
            }
        }

    }

    /**
     * Inner class used to give a view of the values in the map.
     */
    private inner class Values: AbstractMutableCollection<V>() {

        override val size: Int
            get() = this@MutableUnionFindMap._roots.size

        override fun add(element: V): Boolean {
            throw UnsupportedOperationException("Add is not supported on values")
        }

        override fun iterator(): MutableIterator<V> {
            TODO("Not yet implemented")
        }

    }

    /**
     * Inner class used to give a view of the entries in the map.
     */
    private inner class Entries: AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {

        override val size: Int
            get() = this@MutableUnionFindMap._roots.size + this@MutableUnionFindMap._parents.size

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            throw UnsupportedOperationException("Add is not supported on entries")
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
            TODO("Not yet implemented")
        }

    }


    /**
     * Inner class used to give a view of the components in the map.
     */
    private inner class Components: AbstractMutableSet<MutableDisjointMap.MutableComponent<K, V>>() {

        override val size: Int
            get() = this@MutableUnionFindMap._roots.size

        override fun add(element: MutableDisjointMap.MutableComponent<K, V>): Boolean {
            TODO("Not yet implemented")
        }

        override fun iterator(): MutableIterator<MutableDisjointMap.MutableComponent<K, V>> {
            TODO("Not yet implemented")
        }

    }


    /**
     * A mutable key-value entry.
     */
    private data class MutableEntry<K, V>(override val key: K, override val value: V) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            TODO("Not yet implemented")
        }

        override fun toString(): String = "$key=$value"
    }

    /**
     * A mutable component.
     */
    private data class MutableComponent<K, V>(override val keys: MutableSet<K>, override val value: V) : MutableDisjointMap.MutableComponent<K, V> {
        override fun setValue(newValue: V): V {
            TODO("Not yet implemented")
        }

        override fun toString(): String = "$keys=$value"
    }

}