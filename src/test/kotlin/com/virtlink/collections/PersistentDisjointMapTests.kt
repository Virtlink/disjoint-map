package com.virtlink.collections

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests the [PersistentDisjointMap] interface.
 */
@Suppress("ClassName", "unused", "RemoveRedundantBackticks")
interface PersistentDisjointMapTests : ImmutableDisjointMapTests {

    override fun <K, V> create(initial: Collection<DisjointMap.Component<K, V>>): PersistentDisjointMap<K, V>

    /**
     * Tests the [PersistentDisjointMap.put] method.
     */
    interface `put()`: PersistentDisjointMapTests {

        @Test
        fun `creates a new component with the given key and value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V")
                )
            )

            // Act
            val newMap = map.put("C", "XX")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C") to "XX")
            ), newMap.components)
        }

        @Test
        fun `separates an existing component and assigns the given value to the given key`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newMap = map.put("C", "XX")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C") to "XX")
            ), newMap.components)
        }

        @Test
        fun `does not union with an existing component with equal value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V"),
                    Component.of(setOf("D", "E") to "X")
                )
            )

            // Act
            val newMap = map.put("C", "X")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C") to "X"),
                Component.of(setOf("D", "E") to "X")
            ), newMap.components)
        }
    }


    /**
     * Tests the [PersistentDisjointMap.putComponent] method.
     */
    interface `putComponent()`: PersistentDisjointMapTests {

        @Test
        fun `creates a new component with the given keys and value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V")
                )
            )

            // Act
            val newMap = map.putComponent(setOf("C", "D"), "XX")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C", "D") to "XX")
            ), newMap.components)
        }

        @Test
        fun `separates keys from existing components and assigns the given value to the given keys`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newMap = map.putComponent(setOf("C", "D"), "XX")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C", "D") to "XX")
            ), newMap.components)
        }

        @Test
        fun `does not union with an existing component with equal value`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V"),
                    Component.of(setOf("E", "F") to "X")
                )
            )

            // Act
            val newMap = map.putComponent(setOf("C", "D"), "X")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B") to "V"),
                Component.of(setOf("C", "D") to "X"),
                Component.of(setOf("E", "F") to "X")
            ), newMap.components)
        }

    }

    /**
     * Tests the [PersistentDisjointMap.putAll] method.
     */
    interface `putAll()`: PersistentDisjointMapTests {

        @Test
        fun `for a normal map acts as if put has been called on each entry`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V"),
                    Component.of(setOf("D", "E") to "X")
                )
            )
            val input = mapOf(
                "A" to "W",
                "C" to "XX",
                "E" to "X"
            )

            // Act
            val newMap = map.putAll(input)

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A") to "W"),
                Component.of(setOf("B") to "V"),
                Component.of(setOf("C") to "XX"),
                Component.of(setOf("D") to "X"),
                Component.of(setOf("E") to "X")
            ), newMap.components)
        }

        @Test
        fun `for a disjoint map acts as if putComponent has been called on each entry`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B") to "V"),
                    Component.of(setOf("D", "E") to "X")
                )
            )
            val input = create(
                listOf(
                    Component.of(setOf("A", "C") to "XX"),
                    Component.of(setOf("D") to "X")
                )
            )

            // Act
            val newMap = map.putAll(input)

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "C") to "XX"),
                Component.of(setOf("B") to "V" ),
                Component.of(setOf("D") to "X" ),
                Component.of(setOf("E") to "X")
            ), newMap.components)
        }

    }

    /**
     * Tests the [PersistentDisjointMap.remove] method.
     */
    interface `remove1()`: PersistentDisjointMapTests {

        @Test
        fun `removes a non-representative key but leaves the rest of the non-empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("B")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A", "C"),
                "X" to setOf("D", "E", "F")
            ), newMap.components)
        }

        @Test
        fun `removes a representative key but leaves the rest of the non-empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("A")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("B", "C") to "V"),
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
        }

        @Test
        fun `removes the only key and the now empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("A")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
        }

        @Test
        fun `does not remove anything when the key does not exist`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("X")

            // Assert
            assertEquals(map.components, newMap.components)
        }

    }


    /**
     * Tests the [PersistentDisjointMap.remove] method.
     */
    interface `remove2()`: PersistentDisjointMapTests {

        @Test
        fun `removes a non-representative key but leaves the rest of the non-empty component when the value matches`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("B", "V")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "C") to "V"),
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
        }

        @Test
        fun `removes a representative key but leaves the rest of the non-empty component when the value matches`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("A", "V")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("B", "C") to "V"),
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
        }

        @Test
        fun `removes the only key and the now empty component when the value matches`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("A", "V")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
        }

        @Test
        fun `does not remove anything when the key does not exist`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("X")

            // Assert
            assertEquals(map.components, newMap.components)
        }

        @Test
        fun `does not remove anything when the value does not match`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.remove("A", "xx")

            // Assert
            assertEquals(map.components, newMap.components)
        }

    }



    /**
     * Tests the [PersistentDisjointMap.removeKey] method.
     */
    interface `removeKey()`: PersistentDisjointMapTests {

        @Test
        fun `removes a non-representative key but leaves the rest of the non-empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val (newMap, pair) = map.removeKey("B")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A", "C"),
                "X" to setOf("D", "E", "F")
            ), newMap.components)
            assertEquals("A" to "V", pair)
        }

        @Test
        fun `removes a representative key but leaves the rest of the non-empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val (newMap, pair) = map.removeKey("A")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("B", "C") to "V"),
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
            assertEquals("B" to "V", pair)
        }

        @Test
        fun `removes the only key and the now empty component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val (newMap, pair) = map.removeKey("A")

            // Assert
            assertEquals(listOf(
                Component.of(setOf("D", "E", "F") to "X")
            ), newMap.components)
            assertEquals(null to "V", pair)
        }

        @Test
        fun `does not remove anything when the key does not exist`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val (newMap, pair) = map.removeKey("X")

            // Assert
            assertEquals(map.components, newMap.components)
            assertNull(pair)
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
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.clear()

            // Assert
            assertEquals(emptyMap<String, Set<String>>(), newMap.components)
        }

    }

    /**
     * Tests the [PersistentDisjointMap.union] method.
     */
    interface `union()`: PersistentDisjointMapTests {

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
                listOf(
                    Component.of(setOf("A", "B", "C", "D", "E", "F") to "V")
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
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E") to "V2")
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
                listOf(
                    Component.of(setOf("A", "B", "C") to "V1"),
                    Component.of(setOf("D", "E") to "V2")
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
     * Tests the [PersistentDisjointMap.disunion] method.
     */
    interface `disunion()`: PersistentDisjointMapTests {

        @Test
        fun `disunifies a non-representative key from a component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.disunion("B")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A", "C"),
                "V" to setOf("B"),
                "X" to setOf("D", "E", "F")
            ), newMap.components)
        }

        @Test
        fun `disunifies a representative key from a component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.disunion("A")

            // Assert
            assertEquals(listOf(
                "V" to setOf("A"),
                "V" to setOf("B", "C"),
                "X" to setOf("D", "E", "F")
            ), newMap.components)
        }

        @Test
        fun `does nothing when disunifying the only key from its component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A") to "V"),
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act
            val newMap = map.disunion("A")

            // Assert
            assertEquals(map.components, newMap.components)
        }

        @Test
        fun `throws when disunifying a key that does not exist`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("D", "E", "F") to "X")
                )
            )

            // Act/Assert
            assertThrows(NoSuchElementException::class.java) {
                map.disunion("x")
            }
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

            // Act
            val newMap = map.setComponent("B", "XX")

            // Assert
            assertEquals(listOf(
                "X" to setOf("XX")
            ), newMap.components)
        }


        @Test
        fun `sets a new value to an existing component`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to null as String?)
                )
            )

            // Act
            val newMap = map.setComponent("C", "XX")

            // Assert
            assertEquals(listOf(
                "XX" to setOf("A", "B", "C")
            ), newMap.components)
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
            val newMap = map.setComponent("C", "XX")

            // Assert
            assertEquals(listOf(
                "XX" to setOf("A", "B", "C")
            ), newMap.components)
        }
    }

    /**
     * Tests the [PersistentDisjointMap.compute] method.
     */
    interface `compute()`: PersistentDisjointMapTests {

        @Test
        fun `computes a new value to a new component`() {
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
                Component.of(setOf("B") to "XX")
            ), newMap.components)
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
            val (newMap, newValue) = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertNull(v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), newMap.components)
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
            val (newMap, newValue) = map.compute("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), newMap.components)
            assertEquals("XX", newValue)
        }

    }




    /**
     * Tests the [PersistentDisjointMap.computeIfPresent] method.
     */
    interface `computeIfPresent()`: PersistentDisjointMapTests {

        @Test
        fun `computes no value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(emptyMap<Set<String>, String>(), newMap.components)
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
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to null)
            ), newMap.components)
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
            val (newMap, newValue) = map.computeIfPresent("B") { r, v ->
                assertEquals("A", r)
                assertEquals("V", v)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), newMap.components)
            assertEquals("XX", newValue)
        }

    }



    /**
     * Tests the [PersistentDisjointMap.computeIfAbsent] method.
     */
    interface `computeIfAbsent()`: PersistentDisjointMapTests {

        @Test
        fun `computes a new value to a new component`() {
            // Arrange
            val map = create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                assertEquals("B", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("B") to "XX")
            ), newMap.components)
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
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                assertEquals("A", r)
                "XX"
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "XX")
            ), newMap.components)
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
            val (newMap, newValue) = map.computeIfAbsent("B") { _ ->
                throw IllegalStateException()
            }

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "V")
            ), newMap.components)
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
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val newMap = map.builder().build()

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C") to "V")
            ), newMap.components)
        }

        @Test
        fun `build will apply any changes`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
                )
            )

            // Act
            val builder = map.builder()
            builder.union("D", "E", { "XX" }) { _, _ -> throw IllegalStateException() }
            builder.union("C", "F", { TODO() }) { _, _ -> throw IllegalStateException() }
            val newMap = builder.build()

            // Assert
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C", "F") to "V"),
                Component.of(setOf("D", "E") to "XX")
            ), newMap.components)
        }

        @Test
        fun `multiple calls to build do not change intermediate results`() {
            // Arrange
            val map = create(
                listOf(
                    Component.of(setOf("A", "B", "C") to "V")
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
                Component.of(setOf("A", "B", "C") to "V"),
                Component.of(setOf("D", "E") to "XX")
            ), newMap1.components)
            assertEquals(listOf(
                Component.of(setOf("A", "B", "C", "F") to "V"),
                Component.of(setOf("D", "E") to "XX")
            ), newMap2.components)
        }

    }
}