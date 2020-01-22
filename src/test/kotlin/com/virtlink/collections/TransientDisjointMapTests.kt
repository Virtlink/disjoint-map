package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [TransientDisjointMapTests] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface TransientDisjointMapTests : DisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): TransientDisjointMap<K, V>

    // put

    // remove1
    // remove2

    // putAll
    // clear
    // keys
    // values
    // entries

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

}