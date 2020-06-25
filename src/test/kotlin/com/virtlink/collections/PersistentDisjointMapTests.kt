package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [PersistentDisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface PersistentDisjointMapTests : ImmutableDisjointMapTests {

    override fun <K, V> create(initial: Iterable<DisjointSet<K, V>>): PersistentDisjointMap<K, V>

    /**
     * Tests the [PersistentDisjointMap.set] method.
     */
    interface `set()`: PersistentDisjointMapTests {

        @Test
        fun `when the key is not in the map, creates a new set with the given key and value`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B"), 1)
                )
            )

            // Act
            val newMap = map.set("C", 99)

            // Assert
            assertEquals(
                mapOf(
                    DisjointSet(setOf("A", "B"), 1),
                    DisjointSet(setOf("C"), 99)
                ), newMap.toMap()
            )
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
            val newMap = map.set("C", "XX")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), newMap.toMap())
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
            val newMap = map.set("B", "X")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B"), "X"),
                DisjointSet(setOf("D", "E"), "X")
            ), newMap.toMap())
        }
    }

    /**
     * Tests the [PersistentDisjointMap.remove] method.
     */
    interface `remove()`: PersistentDisjointMapTests {

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
            val newMap = map.remove("B")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), newMap.toMap())
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
            val newMap = map.remove("A")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("B", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), newMap.toMap())
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
            val newMap = map.remove("A")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("D", "E", "F"), "X")
            ), newMap.toMap())
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
            val newMap = map.remove("X")

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), map.toMap())
            assertSame(map, newMap)
        }

    }



    /**
     * Tests the [PersistentDisjointMap.clear] method.
     */
    interface `clear()`: PersistentDisjointMapTests {

        @Test
        fun `returns an empty map`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V"),
                    DisjointSet(setOf("D", "E", "F"), "X")
                )
            )

            // Act
            val newMap = map.clear()

            // Assert
            assertEquals(emptyMap<String, Set<String>>(), newMap.toMap())
        }

    }

    /**
     * Tests the [PersistentDisjointMap.union] method.
     */
    interface `union()`: PersistentDisjointMapTests {

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
            val newMap = map.union("B", "D", { TODO() }, lift { _, _ -> throw IllegalStateException() })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V")
            ), newMap.toMap())
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
            val newMap = map.union("B", "D", { TODO() }, { v1, v2 -> v1 + v2 })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V1V2")
            ), newMap.toMap())
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
            val newMap = map.union("B", "D", { TODO() }, { _, _ -> null })

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), null)
            ), newMap.toMap())
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
            val newMap = map.union("B", "D", { TODO() }) { _, _ -> throw IllegalStateException() }

            // Assert
            assertEquals(mapOf(
                DisjointSet(setOf("A", "B", "C", "D", "E", "F"), "V")
            ), newMap.toMap())
            assertSame(map, newMap)
        }

    }

    /**
     * Tests the [PersistentDisjointMap.disunion] method.
     */
    interface `disunion()`: PersistentDisjointMapTests {

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
            val newMap = map.disunion("B")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A", "C"),
                "V" to setOf("B"),
                "X" to setOf("D", "E", "F")
            ), newMap.toMap())
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
            val newMap = map.disunion("A")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A"),
                "V" to setOf("B", "C"),
                "X" to setOf("D", "E", "F")
            ), newMap.toMap())
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
            val newMap = map.disunion("A")

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A"), "V"),
                DisjointSet(setOf("D", "E", "F"), "X")
            ), newMap.toMap())
            assertSame(map, newMap)
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
     * Tests the [PersistentDisjointMap.compute] method.
     */
    interface `compute()`: PersistentDisjointMapTests {

        @Test
        fun `computes a new value to a new set`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val (newMap, newValue) = map.compute("B") { r, v ->
                assertEquals("B", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("B"), "XX")
            ), newMap.toMap())
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
            val (newMap, newValue) = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), newMap.toMap())
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
            val (newMap, newValue) = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), newMap.toMap())
            assertEquals("XX", newValue)
        }

    }




    /**
     * Tests the [PersistentDisjointMap.computeIfPresent] method.
     */
    interface `computeIfPresent()`: PersistentDisjointMapTests {

        @Test
        fun `computes no value to a new set`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(emptyMap<Set<String>, String>(), newMap.toMap())
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
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), null)
            ), newMap.toMap())
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
            val (newMap, newValue) = map.computeIfPresent("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), newMap.toMap())
            assertEquals("XX", newValue)
        }

    }



    /**
     * Tests the [PersistentDisjointMap.computeIfAbsent] method.
     */
    interface `computeIfAbsent()`: PersistentDisjointMapTests {

        @Test
        fun `computes a new value to a new set`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                assertEquals("B", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("B"), "XX")
            ), newMap.toMap())
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
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                assertEquals("A", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "XX")
            ), newMap.toMap())
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
            val (newMap, newValue) = map.computeIfAbsent("B") { _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "V")
            ), newMap.toMap())
            assertEquals("V", newValue)
        }

    }

    /**
     * Tests the [PersistentDisjointMap.builder] method.
     */
    interface `builder()`: PersistentDisjointMapTests {

        @Test
        fun `build will return the same map as the original when there are no changes`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val newMap = map.builder().build()

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "V")
            ), newMap.toMap())
        }

        @Test
        fun `build will apply any changes`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val builder = map.builder()
            builder.union("D", "E", { "XX" }) { _, _ -> throw IllegalStateException() }
            builder.union("C", "F", { TODO() }) { _, _ -> throw IllegalStateException() }
            val newMap = builder.build()

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C", "F"), "V"),
                DisjointSet(setOf("D", "E"), "XX")
            ), newMap.toMap())
        }

        @Test
        fun `multiple calls to build do not change intermediate results`() {
            // Arrange
            val map = create(
                listOf(
                    DisjointSet(setOf("A", "B", "C"), "V")
                )
            )

            // Act
            val builder = map.builder()
            builder.union("D", "E", { "XX" }) { _, _ -> throw IllegalStateException() }
            val newMap1 = builder.build()
            builder.union("C", "F", { TODO() }) { _, _ -> throw IllegalStateException() }
            val newMap2 = builder.build()

            // Assert
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C"), "V"),
                DisjointSet(setOf("D", "E"), "XX")
            ), newMap1.toMap())
            assertEquals(listOf(
                DisjointSet(setOf("A", "B", "C", "F"), "V"),
                DisjointSet(setOf("D", "E"), "XX")
            ), newMap2.toMap())
        }

    }
}