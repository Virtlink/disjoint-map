package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [PersistentDisjointMap] interface.
 */
@Suppress("ClassName")
interface PersistentDisjointMapTests : ImmutableDisjointMapTests {

    override fun <K, V> create(initial: Map<Set<K>, V>): PersistentDisjointMap<K, V>

    /**
     * Tests the [PersistentDisjointMap.union] method.
     */
    interface `union()`: PersistentDisjointMapTests {

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
            val newMap = map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(newMap.same("B", "D"))
            assertEquals(newMap.find("B"), newMap.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), newMap.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), newMap.getComponent("D"))
            assertEquals(6, newMap.getComponentSize("B"))
            assertEquals(6, newMap.getComponentSize("D"))
            assertEquals("V", newMap["B"])
            assertEquals("V", newMap["D"])
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
            val newMap = map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertTrue(newMap.same("B", "D"))
            assertEquals("A", newMap.find("B"))
            assertEquals("A", newMap.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E"), newMap.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E"), newMap.getComponent("D"))
            assertEquals(5, newMap.getComponentSize("B"))
            assertEquals(5, newMap.getComponentSize("D"))
            assertEquals("V", newMap["B"])
            assertEquals("V", newMap["D"])
        }

        @Test
        fun `does nothing when elements are already merged`() {
            // Arrange
            val map = create(
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
            val newMap = map.union("B", "D", { TODO() }) { _, _ -> throw IllegalStateException() }

            // Assert
            assertTrue(newMap.same("B", "D"))
            assertEquals(newMap.find("B"), newMap.find("D"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), newMap.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "E", "F"), newMap.getComponent("D"))
            assertEquals(6, newMap.getComponentSize("B"))
            assertEquals(6, newMap.getComponentSize("D"))
            assertEquals("V", newMap["B"])
            assertEquals("V", newMap["D"])
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
            val newMap = map.union("B", "D", { TODO() }) { v1, v2 -> v1 + v2 }

            // Assert
            assertEquals("V1V2", newMap["B"])
            assertEquals("V1V2", newMap["D"])
        }

        @Test
        fun `stores null when unify function returns null`() {
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
            val newMap = map.union("B", "D", { TODO() }) { _, _ -> null }

            // Assert
            assertEquals(null, newMap["B"])
            assertEquals(null, newMap["D"])
        }

    }


    /**
     * Tests the [PersistentDisjointMap.setComponent] method.
     */
    interface `setComponent()`: PersistentDisjointMapTests {

        @Test
        fun `sets a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Assume
            assertEquals(null, map["B"])

            // Act
            val newMap = map.setComponent("B", "XX")

            // Assert
            assertEquals("XX", newMap["B"])
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
            val newMap = map.setComponent("C", "XX")

            // Assert
            assertEquals("XX", newMap["B"])
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
            val newMap = map.setComponent("C", "XX")

            // Assert
            assertEquals("XX", newMap["B"])
        }
    }


    /**
     * Tests the [PersistentDisjointMap.removeKey] method.
     */
    interface `removeKey()`: PersistentDisjointMapTests {

        @Test
        fun `removes a representative`() {
            // Arrange
            val map = create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D") to null,
                    setOf("E", "F") to null
                )
            ).union("E", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })
             .union("D", "C", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Act
            val newMap = map.remove("E")

            // Assert
            assertEquals(setOf("A", "B", "C", "D", "F"), newMap.getComponent("B"))
            assertEquals(setOf("A", "B", "C", "D", "F"), newMap.getComponent("D"))
            assertEquals(emptySet<String>(), newMap.getComponent("E"))
            assertEquals("V", newMap["B"])
            assertEquals("V", newMap["D"])
            assertNull(newMap["E"])
        }


    }

}