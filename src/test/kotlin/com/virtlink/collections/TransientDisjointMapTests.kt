package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [TransientDisjointMapTests] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface TransientDisjointMapTests : DisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): TransientDisjointMap<K, V>

    /**
     * Tests the [TransientDisjointMap.union] method.
     */
    interface `union()`: TransientDisjointMapTests {

        @Test
        fun `merges components`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to null
                )
            )

            // Assume
            assertFalse(map.same("B", "D"))
            assertEquals("A", map.find("B"))
            assertEquals("D", map.find("D"))
            assertEquals(setOf("A", "B", "C"), map.getComponent("B"))
            assertEquals(setOf("D", "E", "F"), map.getComponent("D"))
            assertEquals(3, map.getComponentSize("B"))
            assertEquals(3, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals(null, map["D"])

            // Act
            map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(map.same("B", "D"))
            assertEquals(map.find("B"), map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("D"))
            assertEquals(6, map.getComponentSize("B"))
            assertEquals(6, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])
        }

        @Test
        fun `adds smaller component to bigger component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E") to null
                )
            )

            // Assume
            assertFalse(map.same("B", "D"))
            assertEquals("A", map.find("B"))
            assertEquals("D", map.find("D"))
            assertEquals(setOf("A", "B", "C"), map.getComponent("B"))
            assertEquals(setOf("D", "E"), map.getComponent("D"))
            assertEquals(3, map.getComponentSize("B"))
            assertEquals(2, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals(null, map["D"])

            // Act
            map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(map.same("B", "D"))
            assertEquals("A", map.find("B"))
            assertEquals("A", map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E"), map.getComponent("D"))
            assertEquals(5, map.getComponentSize("B"))
            assertEquals(5, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])
        }

        @Test
        fun `does nothing when elements are already merged`() {
            // Arrange
            val map = create<String, String?>(
                mapOf(
                    setOf("A", "B", "C", "D", "E", "F") to "V"
                )
            )

            // Assume
            assertTrue(map.same("B", "D"))
            assertEquals(map.find("B"), map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("D"))
            assertEquals(6, map.getComponentSize("B"))
            assertEquals(6, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])

            // Act
            map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(map.same("B", "D"))
            assertEquals(map.find("B"), map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("D"))
            assertEquals(6, map.getComponentSize("B"))
            assertEquals(6, map.getComponentSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])
        }

        @Test
        fun `stores result of unify function when both have value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2"
                )
            )

            // Assume
            assertEquals("V1", map["B"])
            assertEquals("V2", map["D"])

            // Act
            map.union("B", "D", { TODO() }) { v1, v2 -> v1 + v2 }

            // Assert
            assertEquals("V1V2", map["B"])
            assertEquals("V1V2", map["D"])
        }

        @Test
        fun `unify function can return null`() {
            // Arrange
            val map = create<String, String?>(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2"
                )
            )

            // Assume
            assertEquals("V1", map["B"])
            assertEquals("V2", map["D"])

            // Act
            map.union("B", "D", { TODO() }) { _, _ -> null }

            // Assert
            assertEquals(null, map["B"])
            assertEquals(null, map["D"])
        }

    }


    /**
     * Tests the [TransientDisjointMap.setComponent] method.
     */
    interface `setComponent()`: TransientDisjointMapTests {

        @Test
        fun `sets a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Assume
            assertEquals(null, map["B"])

            // Act
            map.setComponent("B", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }


        @Test
        fun `sets a new value to an existing component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to null as String?
                )
            )

            // Assume
            assertEquals(null, map["B"])

            // Act
            map.setComponent("C", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }

        @Test
        fun `replaces the value of an existing component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Assume
            assertEquals("V", map["B"])

            // Act
            map.setComponent("C", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }
    }


    /**
     * Tests the [TransientDisjointMap.compute] method.
     */
    interface `compute()`: TransientDisjointMapTests {

        @Test
        fun `computes a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val newValue = map.compute("B") { r, v ->
                assertEquals("B", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("B") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing component with no value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to null as String?
                )
            )

            // Act
            val newValue = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes a new value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val newValue = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }

    }




    /**
     * Tests the [TransientDisjointMap.computeIfPresent] method.
     */
    interface `computeIfPresent()`: TransientDisjointMapTests {

        @Test
        fun `computes no value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val newValue = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(emptyMap<Set<String>, String>(), map.components)
            assertEquals(null, newValue)
        }


        @Test
        fun `computes no value to an existing component with no value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to null as String?
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to null
            ), map.components)
            assertEquals(null, newValue)
        }

        @Test
        fun `computes a new value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }

    }



    /**
     * Tests the [TransientDisjointMap.computeIfAbsent] method.
     */
    interface `computeIfAbsent()`: TransientDisjointMapTests {

        @Test
        fun `computes a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                assertEquals("B", r)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("B") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing component with no value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to null as String?
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                assertEquals("A", r)
                "XX"
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to "XX"
            ), map.components)
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes no value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V"
            ), map.components)
            assertEquals("V", newValue)
        }

    }

    /**
     * Tests the [TransientDisjointMap.put] method.
     */
    interface `put()`: TransientDisjointMapTests {

        @Test
        fun `changes the value of an existing component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val oldValue = map.put("B", "X")

            // Assert
            assertEquals(oldValue, "V")
            assertEquals(mapOf(
                setOf("A", "B", "C") to "X"
            ), map.components)
        }

        @Test
        fun `adds a new key and value`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val oldValue = map.put("X", "V2")

            // Assert
            assertNull(oldValue)
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V",
                setOf("X") to "V2"
            ), map.components)
        }

    }


    /**
     * Tests the [TransientDisjointMap.putAll] method.
     */
    interface `putAll()`: TransientDisjointMapTests {

        @Test
        fun `adds all keys and values, changes existing values`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2"
                )
            )

            // Act
            val oldValue = map.putAll(mapOf(
                "B" to "V1new",
                "D" to "V2new",
                "F" to "V3"
            ))

            // Assert
            assertNull(oldValue)
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V1",
                setOf("D", "E") to "V2",
                setOf("F") to "V2"
            ), map.components)
        }

    }


        /**
     * Tests the [TransientDisjointMap.remove] method.
     */
    interface `remove1()`: TransientDisjointMapTests {

        @Test
        fun `removes an existing key from a component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val oldValue = map.remove("B")

            // Assert
            assertEquals(oldValue, "V")
            assertEquals(mapOf(
                setOf("A", "C") to "V"
            ), map.components)
        }

        @Test
        fun `does nothing when the key does not exist`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val oldValue = map.remove("X")

            // Assert
            assertNull(oldValue)
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V"
            ), map.components)
        }

        @Test
        fun `removes a component when removing the last key from a component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A") to "V"
                )
            )

            // Act
            val oldValue = map.remove("A")

            // Assert
            assertEquals(oldValue, "V")
            assertEquals(emptyMap<String, String>(), map.components)
        }

    }

    /**
     * Tests the [TransientDisjointMap.remove] method.
     */
    interface `remove2()`: TransientDisjointMapTests {

        @Test
        fun `removes an existing key from a component when the value matches`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val success = map.remove("B", "V")

            // Assert
            assertTrue(success)
            assertEquals(mapOf(
                setOf("A", "C") to "V"
            ), map.components)
        }

        @Test
        fun `does nothing when the key does not exist`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val success = map.remove("X", "V")

            // Assert
            assertFalse(success)
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V"
            ), map.components)
        }

        @Test
        fun `does nothing when the value does not match`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V"
                )
            )

            // Act
            val success = map.remove("B", "X")

            // Assert
            assertFalse(success)
            assertEquals(mapOf(
                setOf("A", "B", "C") to "V"
            ), map.components)
        }

        @Test
        fun `removes a component when removing the last key and value from a component`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A") to "V"
                )
            )

            // Act
            val success = map.remove("A", "V")

            // Assert
            assertTrue(success)
            assertEquals(emptyMap<String, String>(), map.components)
        }

    }


    /**
     * Tests the [TransientDisjointMap.clear] method.
     */
    interface `clear()`: TransientDisjointMapTests {

        @Test
        fun `removes all keys and values`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2"
                )
            )

            // Act
            map.clear()

            // Assert
            assertEquals(emptyMap<String, String>(), map.components)
        }

    }

    /**
     * Tests the [TransientDisjointMap.keys] property.
     */
    interface `keys`: TransientDisjointMapTests {

        @Test
        fun `returns all keys`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2",
                    setOf("F") to "V3"
                )
            )

            // Act
            val keys = map.keys

            // Assert
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), keys)
        }

        // TODO: Test that mutating the keys works as expected

    }

    /**
     * Tests the [TransientDisjointMap.values] property.
     */
    interface `values`: TransientDisjointMapTests {

        @Test
        fun `returns all keys`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2",
                    setOf("F") to "V3"
                )
            )

            // Act
            val values = map.values

            // Assert
            assertEquals(listOf("V1", "V2", "V3"), values)
        }

        // TODO: Test that mutating the values works as expected

    }

    /**
     * Tests the [TransientDisjointMap.entries] property.
     */
    interface `entries`: TransientDisjointMapTests {

        @Test
        fun `returns all key-value pairs`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E") to "V2",
                    setOf("F") to "V3"
                )
            )

            // Act
            val entries = map.entries

            // Assert
            assertEquals(setOf(
                DisjointMapTests.Entry("A", "V1"),
                DisjointMapTests.Entry("B", "V1"),
                DisjointMapTests.Entry("C", "V1"),
                DisjointMapTests.Entry("D", "V2"),
                DisjointMapTests.Entry("E", "V2"),
                DisjointMapTests.Entry("F", "V3")
            ), entries)
        }

        // TODO: Test that mutating the entries works as expected

    }

}