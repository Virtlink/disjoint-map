package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [MutableDisjointMapTests] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface MutableDisjointMapTests : DisjointMapTests {

    override fun <K, V> create(initial: Collection<Component<K, V>>): MutableDisjointMap<K, V>


    /**
     * Tests the [MutableDisjointMap.set] method.
     */
    interface `set()`: MutableDisjointMapTests {

        @Test
        fun `sets a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Assume
            assertEquals(null, map["B"])

            // Act
            map.set("B", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }


        @Test
        fun `sets a new value to an existing component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Assume
            assertEquals(null, map["B"])

            // Act
            map.set("C", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }

        @Test
        fun `replaces the value of an existing component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Assume
            assertEquals("V", map["B"])

            // Act
            map.set("C", "XX")

            // Assert
            assertEquals("XX", map["B"])
        }
    }

    /**
     * Tests the [MutableDisjointMap.remove] method.
     */
    interface `remove1()`: MutableDisjointMapTests {

        @Test
        fun `removes an existing key from a component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val oldValue = map.remove("B")

            // Assert
            assertEquals(oldValue, "V")
            assertEquals(listOf(
                Component.of(setOf("A", "C") to "V")
            ), map.components)
        }

        @Test
        fun `does nothing when the key does not exist`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val oldValue = map.remove("X")

            // Assert
            assertNull(oldValue)
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "V")
            ), map.components)
        }

        @Test
        fun `removes a component when removing the last key from a component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V")
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
     * Tests the [MutableDisjointMap.clear] method.
     */
    interface `clear()`: MutableDisjointMapTests {

        @Test
        fun `removes all keys and values`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E") to "V2")
                )
            )

            // Act
            map.clear()

            // Assert
            assertEquals(emptyMap<String, String>(), map.components)
        }

    }

    /**
     * Tests the [MutableDisjointMap.union] method.
     */
    interface `union()`: MutableDisjointMapTests {

        @Test
        fun `merges components`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to null)
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
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E") to null)
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
                listOf(
                    Component.of(setOf("A", "B", "C", "D", "E", "F") to "V")
                )
            )

            // Assume
            assertTrue(map.same("B", "D"))
            assertEquals(map.find("B"), map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("D"))
            assertEquals(6, map.getSetSize("B"))
            assertEquals(6, map.getSetSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])

            // Act
            map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(map.same("B", "D"))
            assertEquals(map.find("B"), map.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), map.getComponent("D"))
            assertEquals(6, map.getSetSize("B"))
            assertEquals(6, map.getSetSize("D"))
            assertEquals("V", map["B"])
            assertEquals("V", map["D"])
        }

        @Test
        fun `stores result of unify function when both have value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E") to "V2")
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
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E") to "V2")
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
     * Tests the [MutableDisjointMap.disunion] method.
     */
    interface `disunion()`: MutableDisjointMapTests {

        @Test
        fun `disunifies key from set`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "W")
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
            map.disunion("B")

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
        fun `does nothing when key is not in the set`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "W")
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
            map.disunion("X")

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
    }

    /**
     * Tests the [MutableDisjointMap.compute] method.
     */
    interface `compute()`: MutableDisjointMapTests {

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
            assertEquals(listOf(
                Component.of(setOf("B") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing component with no value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Act
            val newValue = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes a new value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newValue = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }

    }




    /**
     * Tests the [MutableDisjointMap.computeIfPresent] method.
     */
    interface `computeIfPresent()`: MutableDisjointMapTests {

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
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to null)
            ), map.components)
            assertEquals(null, newValue)
        }

        @Test
        fun `computes a new value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }

    }



    /**
     * Tests the [MutableDisjointMap.computeIfAbsent] method.
     */
    interface `computeIfAbsent()`: MutableDisjointMapTests {

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
            assertEquals(listOf(
                Component.of(setOf("B") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing component with no value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                assertEquals("A", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), map.components)
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes no value to an existing component with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "V")
            ), map.components)
            assertEquals("V", newValue)
        }

    }

}