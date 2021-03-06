package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [DisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface DisjointMapTests {

    fun <K, V> create(initial: Iterable<DisjointSet<K, V>> = emptyList()): DisjointMap<K, V>

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
                    DisjointSet(setOf("A"), "Va"),
                    DisjointSet(setOf("B"), "V"),
                    DisjointSet(setOf("C"), "V")
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
                    DisjointSet(setOf("A1", "A2", "A3"), "Va"),
                    DisjointSet(setOf("B1", "B2"), "V"),
                    DisjointSet(setOf("C1"), "V")
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
                    DisjointSet(setOf("A"), "Va"),
                    DisjointSet(setOf("B"), "V"),
                    DisjointSet(setOf("C"), "V")
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
                    DisjointSet(setOf("A1", "A2", "A3"), "Va"),
                    DisjointSet(setOf("B1", "B2"), "V"),
                    DisjointSet(setOf("C1"), "V")
                )
            )

            // Assert
            assertFalse(map.isEmpty())
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
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), null as String?)
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
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), null as String?)
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
                    DisjointSet(setOf("A"), "V")
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
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val rep = map.find("B")

            // Assert
            assertEquals("A", rep)
        }

    }

    /**
     * Tests the [DisjointMap.contains()] method.
     */
    interface `contains()`: DisjointMapTests {
        @Test
        fun `returns false for any key in empty map`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val result = map.contains("A")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns false for non-existing key`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A1", "A2", "A3"), "Va"),
                    DisjointSet(setOf("B1", "B2"), "V"),
                    DisjointSet(setOf("C1"), "V")
                )
            )

            // Act
            val result = map.contains("X")

            // Assert
            assertFalse(result)
        }

        @Test
        fun `returns true for representative`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A1", "A2", "A3"), "Va"),
                    DisjointSet(setOf("B1", "B2"), "V"),
                    DisjointSet(setOf("C1"), "V")
                )
            )

            // Assume
            assert(map.find("C1") == "C1") { "C1 must be the representative" }

            // Act
            val result = map.contains("C1")

            // Assert
            assertTrue(result)
        }

        @Test
        fun `returns true for non-representative`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A1", "A2", "A3"), "Va"),
                    DisjointSet(setOf("B1", "B2"), "V"),
                    DisjointSet(setOf("C1"), "V")
                )
            )

            // Assume
            assert(map.find("A3") != "A3") { "A3 must not be the representative" }

            // Act
            val result = map.contains("A3")

            // Assert
            assertTrue(result)
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
                    DisjointSet(setOf("A"), "V")
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
                    DisjointSet(setOf("A"), "V1"),
                    DisjointSet(setOf("X"), "V2")
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
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val same = map.same("B", "C")

            // Assert
            assertTrue(same)
        }


    }

}