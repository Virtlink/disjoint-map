package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [DisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface DisjointMapTests {

    fun <K, V> create(initial: Collection<DisjointMap.Component<K, V>> = emptyList()): DisjointMap<K, V>


    /**
     * Tests the [DisjointMap.size] property.
     */
    interface `size`: DisjointMapTests {
        @Test
        fun `returns zero for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Assert
            assertEquals(0, map.size)
        }

        @Test
        fun `returns number of representative if each has its own value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "Va"),
                    Component.of(setOf("B") to "V"),
                    Component.of(setOf("C") to "V")
                )
            )

            // Assert
            assertEquals(3, map.size)
        }

        @Test
        fun `returns number of keys`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Assert
            assertEquals(6, map.size)
        }
    }

    /**
     * Tests the [DisjointMap.isEmpty()] method.
     */
    interface `isEmpty()`: DisjointMapTests {
        @Test
        fun `returns true for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Assert
            assertTrue(map.isEmpty())
        }

        @Test
        fun `returns false for map where each representative has its own value `() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "Va"),
                    Component.of(setOf("B") to "V"),
                    Component.of(setOf("C") to "V")
                )
            )

            // Assert
            assertFalse(map.isEmpty())
        }

        @Test
        fun `returns false when there are multiple keys`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Assert
            assertFalse(map.isEmpty())
        }
    }

    /**
     * Tests the [DisjointMap.containsKey()] method.
     */
    interface `containsKey()`: DisjointMapTests {
        @Test
        fun `returns false for any key in empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val result = map.containsKey("A")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns false for non-existing key`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val result = map.containsKey("X")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns true for representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Assume
            assert(map.find("C1") == "C1") { "C1 must be the representative" }

            // Act
            val result = map.containsKey("C1")

            // Assert
            assertTrue(result)
        }

        @Test
        fun `returns true for non-representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Assume
            assert(map.find("A3") != "A3") { "A3 must not be the representative" }

            // Act
            val result = map.containsKey("A3")

            // Assert
            assertTrue(result)
        }
    }

    /**
     * Tests the [DisjointMap.containsValue()] method.
     */
    interface `containsValue()`: DisjointMapTests {
        @Test
        fun `returns false for any value in empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val result = map.containsValue("V")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns false for non-existing value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val result = map.containsValue("X")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns true for value that occurs once`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val result = map.containsValue("Va")

            // Assert
            assertTrue(result)
        }

        @Test
        fun `returns true for value that occurs multiple times`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val result = map.containsValue("V")

            // Assert
            assertTrue(result)
        }
    }

    /**
     * Tests the [DisjointMap.get] method.
     */
    interface `get()`: DisjointMapTests {
        @Test
        fun `returns null when key not found`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val value = map["X"]

            // Assert
            assertNull(value)
        }

        @Test
        fun `returns value when key found as representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Assume
            assert(map.find("A") == "A") { "A must be the representative" }

            // Act
            val value = map["A"]

            // Assert
            assertEquals("V", value)
        }

        @Test
        fun `returns value when key found not as representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Assume
            assert(map.find("B") != "B") { "B must not be the representative" }

            // Act
            val value = map["B"]

            // Assert
            assertEquals("V", value)
        }

        @Test
        fun `returns null when value is null`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Assume
            assert(map.find("A") == "A") { "A must be the representative" }

            // Act
            val value = map["A"]

            // Assert
            assertNull(value)
        }

    }

    /**
     * Tests the [DisjointMap.getOrDefault] method.
     */
    interface `getOrDefault()`: DisjointMapTests {
        @Test
        fun `returns default when key not found`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val value = map.getOrDefault("X", "default")

            // Assert
            assertEquals("default", value)
        }

        @Test
        fun `returns value when key found`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Assume
            assert(map.find("A") == "A") { "A must be the representative" }

            // Act
            val value = map.getOrDefault("A", "default")

            // Assert
            assertEquals("V", value)
        }

        @Test
        fun `returns null when value is null`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Assume
            assert(map.find("A") == "A") { "A must be the representative" }

            // Act
            val value = map.getOrDefault("A", "default")

            // Assert
            assertNull(value)
        }

    }

    /**
     * Tests the [DisjointMap.keys] property.
     */
    interface `keys`: DisjointMapTests {
        @Test
        fun `returns empty set for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val keys = map.keys

            // Assert
            assertEquals(emptySet<String>(), keys)
            assertEquals(emptySet<String>(), keys.iterator().asSequence().toSet())
            assertEquals(0, keys.size)
            assertFalse(keys.contains("X"))
            assertFalse(keys.containsAll(listOf("X", "Y")))
        }

        @Test
        fun `returns representative keys`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "Va"),
                    Component.of(setOf("B") to "V"),
                    Component.of(setOf("C") to "V")
                )
            )

            // Act
            val keys = map.keys

            // Assert
            assertEquals(setOf("A", "B", "C"), keys)
            assertEquals(setOf("A", "B", "C"), keys.iterator().asSequence().toSet())
            assertEquals(3, keys.size)
            assertFalse(keys.contains("X"))
            assertTrue(keys.contains("B"))
            assertFalse(keys.containsAll(listOf("X", "Y")))
            assertFalse(keys.containsAll(listOf("X", "B")))
            assertTrue(keys.containsAll(listOf("A", "B")))
        }

        @Test
        fun `returns all keys`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val keys = map.keys

            // Assert
            assertEquals(setOf("A1", "A2", "A3", "B1", "B2", "C1"), keys)
            assertEquals(setOf("A1", "A2", "A3", "B1", "B2", "C1"), keys.iterator().asSequence().toSet())
            assertEquals(6, keys.size)
            assertFalse(keys.contains("X"))
            assertTrue(keys.contains("C1"))
            assertFalse(keys.containsAll(listOf("X", "Y")))
            assertFalse(keys.containsAll(listOf("X", "C1")))
            assertTrue(keys.containsAll(listOf("A1", "C1")))
        }

        @Test
        fun `returns keys that have null value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to null),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val keys = map.keys

            // Assert
            assertEquals(setOf("A1", "A2", "A3", "B1", "B2", "C1"), keys)
            assertEquals(setOf("A1", "A2", "A3", "B1", "B2", "C1"), keys.iterator().asSequence().toSet())
            assertEquals(6, keys.size)
            assertFalse(keys.contains("X"))
            assertTrue(keys.contains("C1"))
            assertFalse(keys.containsAll(listOf("X", "Y")))
            assertFalse(keys.containsAll(listOf("X", "C1")))
            assertTrue(keys.containsAll(listOf("A1", "C1")))
        }
    }


    /**
     * Tests the [DisjointMap.values] property.
     */
    interface `values`: DisjointMapTests {
        @Test
        fun `returns empty collection for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val values = map.values

            // Assert
            assertEquals(emptyList<String>(), values.toList())
            assertEquals(emptyList<String>(), values.iterator().asSequence().toList())
            assertEquals(0, values.size)
            assertFalse(values.contains("X"))
            assertFalse(values.containsAll(listOf("X", "Y")))
        }

        @Test
        fun `returns all values`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val values = map.values

            // Assert
            assertEquals(listOf("Va", "V", "V"), values.toList())
            assertEquals(listOf("Va", "V", "V"), values.iterator().asSequence().toList())
            assertEquals(3, values.size)
            assertFalse(values.contains("X"))
            assertTrue(values.contains("Va"))
            assertTrue(values.contains("V"))
            assertFalse(values.containsAll(listOf("X", "Y")))
            assertFalse(values.containsAll(listOf("X", "Va")))
            assertTrue(values.containsAll(listOf("V", "Va")))
        }

        @Test
        fun `returns null values`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to null),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val values = map.values

            // Assert
            assertEquals(listOf("Va", null, "V"), values.toList())
            assertEquals(listOf("Va", null, "V"), values.iterator().asSequence().toList())
            assertEquals(3, values.size)
            assertFalse(values.contains("X"))
            assertTrue(values.contains("Va"))
            assertTrue(values.contains(null))
            assertFalse(values.containsAll(listOf("X", "Y")))
            assertFalse(values.containsAll(listOf("X", null)))
            assertTrue(values.containsAll(listOf("V", null)))
        }
    }


    /**
     * Tests the [DisjointMap.entries] property.
     */
    interface `entries`: DisjointMapTests {
        @Test
        fun `returns empty set for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val entries = map.entries

            // Assert
            assertEquals(emptySet<Entry<String, String>>(), entries.toEntrySet())
            assertEquals(emptySet<Entry<String, String>>(), entries.iterator().asSequence().toEntrySet())
            assertEquals(0, entries.size)
            assertFalse(entries.contains(Entry("X", "V")))
        }

        @Test
        fun `returns representative keys and associated values`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "Va"),
                    Component.of(setOf("B") to "V"),
                    Component.of(setOf("C") to "V")
                )
            )

            // Act
            val entries = map.entries

            // Assert
            val expectedSet = setOf(
                Entry("A", "Va"),
                Entry("B", "V"),
                Entry("C", "V")
            )
            assertEquals(expectedSet, entries.toEntrySet())
            assertEquals(expectedSet, entries.iterator().asSequence().toEntrySet())
            assertEquals(3, entries.size)
            assertFalse(entries.contains(Entry("A", "V")))
            assertTrue(entries.contains(Entry("B", "V")))
        }

        @Test
        fun `returns all keys and associated values`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to "V"),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val entries = map.entries

            // Assert
            val expectedSet = setOf(
                Entry("A1", "Va"),
                Entry("A2", "Va"),
                Entry("A3", "Va"),
                Entry("B1", "V"),
                Entry("B2", "V"),
                Entry("C1", "V")
            )
            assertEquals(expectedSet, entries.toEntrySet())
            assertEquals(expectedSet, entries.iterator().asSequence().toEntrySet())
            assertEquals(6, entries.size)
            assertFalse(entries.contains(Entry("A1", "V")))
            assertTrue(entries.contains(Entry("B1", "V")))
        }

        @Test
        fun `returns entries that have null value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A1", "A2", "A3") to "Va"),
                    Component.of(setOf("B1", "B2") to null),
                    Component.of(setOf("C1") to "V")
                )
            )

            // Act
            val entries = map.entries

            // Assert
            val expectedSet = setOf(
                Entry("A1", "Va"),
                Entry("A2", "Va"),
                Entry("A3", "Va"),
                Entry("B1", null),
                Entry("B2", null),
                Entry("C1", "V")
            )
            assertEquals(expectedSet, entries.toEntrySet())
            assertEquals(expectedSet, entries.iterator().asSequence().toEntrySet())
            assertEquals(6, entries.size)
            assertFalse(entries.contains(Entry("A1", "V")))
            assertTrue(entries.contains(Entry("B1", null)))
        }
    }


    /**
     * Tests the [DisjointMap.components] property.
     */
    interface `components`: DisjointMapTests {

        @Test
        fun `returns empty map for empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val components = map.components

            // Assert
            assertEquals(emptySet<Component<String, String>>(), components)
            assertEquals(emptySet<Component<String, String>>(), components.iterateToSet())
            assertEquals(0, components.size)
        }

        @Test
        fun `returns representative keys and associated values`() {
            // Arrange
            val expectedComponents = listOf(
                Component.of(setOf("A") to "Va"),
                Component.of(setOf("B") to "V"),
                Component.of(setOf("C") to "V")
            )
            val map = create(expectedComponents)

            // Act
            val components = map.components

            // Assert
            assertEquals(expectedComponents, components)
            assertEquals(expectedComponents, components.iterateToSet())
            assertEquals(3, components.size)
        }

        @Test
        fun `returns all keys and associated values`() {
            // Arrange
            val expectedComponents = listOf(
                Component.of(setOf("A1", "A2", "A3") to "Va"),
                Component.of(setOf("B1", "B2") to "V"),
                Component.of(setOf("C1") to "V")
            )
            val map = create(expectedComponents)

            // Act
            val components = map.components

            // Assert
            assertEquals(expectedComponents, components)
            assertEquals(expectedComponents, components.iterateToSet())
            assertEquals(6, components.size)
        }

        @Test
        fun `returns components that have null value`() {
            // Arrange
            val expectedComponents = listOf(
                Component.of(setOf("A1", "A2", "A3") to "Va"),
                Component.of(setOf("B1", "B2") to null),
                Component.of(setOf("C1") to "V")
            )
            val map = create(expectedComponents)

            // Act
            val components = map.components

            // Assert
            assertEquals(expectedComponents, components)
            assertEquals(expectedComponents, components.iterateToSet())
            assertEquals(6, components.size)
        }

    }


    /**
     * Tests the [DisjointMap.find] method.
     */
    interface `find()`: DisjointMapTests {

        @Test
        fun `returns same element when representative not found`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val rep = map.find("A")

            // Assert
            assertEquals("A", rep)
        }

        @Test
        fun `returns same element when found as representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V")
                )
            )

            // Act
            val rep = map.find("A")

            // Assert
            assertEquals("A", rep)
        }

        @Test
        fun `returns representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val rep = map.find("B")

            // Assert
            assertEquals("A", rep)
        }

    }


    /**
     * Tests the [DisjointMap.same] method.
     */
    interface `same()`: DisjointMapTests {

        @Test
        fun `returns true when comparing same elements with no representatives`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val same = map.same("A", "A")

            // Assert
            assertTrue(same)
        }


        @Test
        fun `returns false when comparing different elements with no representatives`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val same = map.same("A", "X")

            // Assert
            assertFalse(same)
        }


        @Test
        fun `returns true when comparing same elements that are representatives`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V")
                )
            )

            // Act
            val same = map.same("A", "A")

            // Assert
            assertTrue(same)
        }


        @Test
        fun `returns false when comparing different elements that are representatives`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V1"),
                    Component.of(setOf("X") to "V2")
                )
            )

            // Act
            val same = map.same("A", "X")

            // Assert
            assertFalse(same)
        }


        @Test
        fun `returns true when comparing elements in the same component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val same = map.same("B", "C")

            // Assert
            assertTrue(same)
        }


    }


    /**
     * Tests the [DisjointMap.getComponent] method.
     */
    interface `getComponent()`: DisjointMapTests {

        @Test
        fun `returns single element when not in map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val component = map.getComponent("A")

            // Assert
            assertEquals(setOf("A"), component)
        }


        @Test
        fun `returns single element when only one in map`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V")
                )
            )

            // Act
            val component = map.getComponent("A")

            // Assert
            assertEquals(setOf("A"), component)
        }


        @Test
        fun `returns all elements in component when asking for representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E", "F") to "V2")
                )
            )

            // Act
            val component = map.getComponent("A")

            // Assert
            assertEquals(setOf("A", "B", "C"), component)
        }

        @Test
        fun `returns all elements in component when not asking for representative`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E", "F") to "V2")
                )
            )

            // Act
            val component = map.getComponent("B")

            // Assert
            assertEquals(setOf("A", "B", "C"), component)
        }

    }



    /**
     * Tests the [DisjointMap.getComponentSize] method.
     */
    interface `getComponentSize()`: DisjointMapTests {

        @Test
        fun `returns 1 when not in map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val rank = map.getComponentSize("A")

            // Assert
            assertEquals(1, rank)
        }


        @Test
        fun `returns 1 when only one in map`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V")
                )
            )

            // Act
            val rank = map.getComponentSize("A")

            // Assert
            assertEquals(1, rank)
        }


        @Test
        fun `returns size of component when in component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C", "D") to "V1"),
                    Component.of(setOf("E", "F") to "V2")
                )
            )

            // Act
            val rank = map.getComponentSize("B")

            // Assert
            assertEquals(4, rank)
        }

    }

    data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        companion object {
            fun <K, V> of(entry: Map.Entry<K, V>): Entry<K, V> =
                Entry(entry.key, entry.value)
        }
        override fun toString(): String = "$key=$value"
    }

    data class Component<K, V>(override val keys: Set<K>, override val value: V) : DisjointMap.Component<K, V> {
        companion object {
            fun <K, V> of(pair: Pair<Set<K>, V>): Component<K, V> =
                Component(pair.first, pair.second)

            fun <K, V> of(component: DisjointMap.Component<K, V>): Component<K, V> =
                Component(component.keys, component.value)
        }
        override fun toString(): String = "$keys=$value"
    }

}