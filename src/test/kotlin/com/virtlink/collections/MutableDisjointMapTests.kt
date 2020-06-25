package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [MutableDisjointMapTests] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface MutableDisjointMapTests : DisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): MutableDisjointMap<K, V>


    /**
     * Tests the [MutableDisjointMap.set] method.
     */
    interface `set()`: MutableDisjointMapTests {

        @Test
        fun `when the key is not in the map, creates a new set with the given key and value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B"), 1)
                )
            )

            // Act
            val oldValue = map.set("C", 99)

            // Assert
            assertEquals(
                mapOf(
                    DisjointSet(setOf("A", "B"), 1),
                    DisjointSet(setOf("C"), 99)
                ), map.toMap()
            )
            assertNull(oldValue)
        }

        @Test
        fun `when the key is in the map, set the value of the existing set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )
            // Act
            val oldValue = map.set("C", "XX")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), map.toMap())
            assertEquals("V", oldValue)
        }

        @Test
        fun `when two sets get the same value, the sets stay disjoint`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B"), "V"),
                    DisjointSet(setOf("D", "E"), "X")
                )
            )

            // Act
            val oldValue = map.set("B", "X")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B"), "X"),
                DisjointSet(setOf("D", "E"), "X")
            ), map.toMap())
            assertEquals("V", oldValue)
        }
    }

    /**
     * Tests the [MutableDisjointMap.remove] method.
     */
    interface `remove()`: MutableDisjointMapTests {

        @Test
        fun `when the key is non-representative, removes it but leaves the rest of the set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Assume
            assertNotEquals("B", map.find("B")) // Not representative

            // Act
            val oldValue = map.remove("B")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
            assertEquals("V", oldValue)
        }

        @Test
        fun `when the key is representative, removes it but leaves the rest of the set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Assume
            assertEquals("A", map.find("A")) // Representative

            // Act
            val oldValue = map.remove("A")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("B", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
            assertEquals("V", oldValue)
        }

        @Test
        fun `when the key is the only key in a set, removes the only key and the now empty set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act
            val oldValue = map.remove("A")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
            assertEquals(oldValue, "V")
        }

        @Test
        fun `when the key is not in the map, does not remove anything`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act
            val oldValue = map.remove("X")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
            assertNull(oldValue)
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
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act
            map.clear()

            // Assert
            assertEquals(emptyMap<String, String>(), map.toMap())
        }

    }

    /**
     * Tests the [MutableDisjointMap.union] method.
     */
    interface `union()`: MutableDisjointMapTests {

        @Test
        fun `when both keys are in sets, unifies sets taking non-null value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), null)
                )
            )

            // Act
            map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V")
            ), map.toMap())
        }

        @Test
        fun `when both keys are in sets, unifies sets and values`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V1"),
                    DisjointSet(setOf("D", "E", "F"), "V2")
                )
            )

            // Act
            map.union("B", "D", { TODO() }, { v1, v2 -> v1 + v2 })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V1V2")
            ), map.toMap())
        }

        @Test
        fun `when both keys are in sets and unifier returns null, unifies sets and stores null`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet<String, String?>(setOf("A", "B", "C"), "V1"),
                    DisjointSet(setOf("D", "E", "F"), "V2")
                )
            )

            // Act
            map.union("B", "D", { TODO() }, { _, _ -> null })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), null)
            ), map.toMap())
        }

        @Test
        fun `when both are in same set, nothing happens`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V")
                )
            )

            // Act
            map.union("B", "D", { TODO() }) { _, _ -> throw IllegalStateException() }

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V")
            ), map.toMap())
        }

    }

    /**
     * Tests the [MutableDisjointMap.disunion] method.
     */
    interface `disunion()`: MutableDisjointMapTests {

        @Test
        fun `when key is non-representative, disunifies it from its set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Assume
            assertNotEquals("B", map.find("B")) // Not representative

            // Act
            map.disunion("B")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A", "C"),
                "V" to setOf("B"),
                "X" to setOf("D", "E", "F")
            ), map.toMap())
        }

        @Test
        fun `when key is representative, disunifies it from its set`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Assume
            assertEquals("A", map.find("A")) // Representative

            // Act
            map.disunion("A")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A"),
                "V" to setOf("B", "C"),
                "X" to setOf("D", "E", "F")
            ), map.toMap())
        }

        @Test
        fun `when key is only one in its set, nothing happens`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act
            map.disunion("A")

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
        }

        @Test
        fun `when key is not in the map, throws`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act/Assert
            assertThrows(NoSuchElementException::class.java) {
                map.disunion("x")
            }
        }
    }

    /**
     * Tests the [MutableDisjointMap.compute] method.
     */
    interface `compute()`: MutableDisjointMapTests {

        @Test
        fun `computes a new value to a new set`() {
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
                DisjointSet(setOf("B"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing set with no value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), null as String?)
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
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes a new value to an existing set with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }

    }




    /**
     * Tests the [MutableDisjointMap.computeIfPresent] method.
     */
    interface `computeIfPresent()`: MutableDisjointMapTests {

        @Test
        fun `computes no value to a new set`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val newValue = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(emptyMap<Set<String>, String>(), map.toMap())
            assertEquals(null, newValue)
        }


        @Test
        fun `computes no value to an existing set with no value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), null as String?)
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), null)
            ), map.toMap())
            assertEquals(null, newValue)
        }

        @Test
        fun `computes a new value to an existing set with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
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
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }

    }



    /**
     * Tests the [MutableDisjointMap.computeIfAbsent] method.
     */
    interface `computeIfAbsent()`: MutableDisjointMapTests {

        @Test
        fun `computes a new value to a new set`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                assertEquals("B", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("B"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }


        @Test
        fun `computes a new value to an existing set with no value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), null as String?)
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                assertEquals("A", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), map.toMap())
            assertEquals("XX", newValue)
        }

        @Test
        fun `computes no value to an existing set with an existing value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "V")
            ), map.toMap())
            assertEquals("V", newValue)
        }

    }

}