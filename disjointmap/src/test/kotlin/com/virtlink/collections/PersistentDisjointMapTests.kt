package com.virtlink.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

interface PersistentDisjointMapFactory {
    fun <K, V> create(initial: Map<Set<K>, V> = emptyMap()): PersistentDisjointMap<K, V>
}

fun testPersistentDisjointMap(
    factory: PersistentDisjointMapFactory,
) = funSpec {
    include(testDisjointMap(object: DisjointMapFactory {
        override fun <K, V> create(initial: Map<Set<K>, V>): DisjointMap<K, V> {
            return factory.create(initial)
        }
    }))
    
    context("set()") {

        test("should create a new set with the given key and value, when the key is not in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B") to 1,
                )
            )

            // Act
            val newMap = map.set("C", 99)

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B") to 1,
                setOf("C") to 99,
            )
        }

        test("should set the value of the existing set, when the key is in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val newMap = map.set("C", "XX")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
        }

        test("should leave the sets disjoint, when two sets get the same value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B") to "V",
                    setOf("D", "E") to "X",
                )
            )

            // Act
            val newMap = map.set("B", "X")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B") to "X",
                setOf("D", "E") to "X",
            )
        }
    }
    
    context("remove()") {

        test("should remove the set but leave the rest of the set, when the key is non-representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val newMap = map.remove("B")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
        }

        test("should remove the set but leave the rest of the set, when the key is representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val newMap = map.remove("A")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("B", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
        }

        test("should remove the only key and the now empty set, when the key is the only key in a set") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act
            val newMap = map.remove("A")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("D", "E", "F") to "X",
            )
        }

        test("should not remove anything, when the key is not in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act
            val newMap = map.remove("X")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
            newMap shouldBeSameInstanceAs map
        }
    }
    
    context("clear()") {

        test("should return an empty map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act
            val newMap = map.clear()

            // Assert
            newMap.toMap() shouldBe emptyMap<String, Set<String>>()
        }
    }
    
    context("union()") {

        test("should unify sets taking the non-null value, when both keys are in sets") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to null,
                )
            )

            // Act
            val newMap = map.union("B", "D", { error("Unreachable") },
                unify = { a, b -> if (a == null) b else if (b == null) a else error("Unreachable") }
            )

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V",
            )
        }

        test("should unify sets and values, when both keys are in sets") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E", "F") to "V2",
                )
            )

            // Act
            val newMap = map.union("B", "D", { error("Unreachable") }) { v1, v2 -> v1 + v2 }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V1V2",
            )
        }

        test("should unify sets and stores null, when both keys are in sets and unifier returns null") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to ("V1" as String?),
                    setOf("D", "E", "F") to "V2",
                )
            )

            // Act
            val newMap = map.union("B", "D", { error("Unreachable") }) { _, _ -> null }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to null,
            )
        }

        test("should do nothing, when both are in same set") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C", "D", "E", "F") to "V",
                )
            )

            // Act
            val newMap = map.union("B", "D", { error("Unreachable") }) { _, _ ->
                error("Unreachable")
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V",
            )
            newMap shouldBeSameInstanceAs map
        }
    }
    
    context("disunion()") {

        test("should disunify it from its set, when key is non-representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val newMap = map.disunion("B")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "C") to "V",
                setOf("B") to "V",
                setOf("D", "E", "F") to "X",
            )
        }

        test("should disunify it from its set, when key is representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val newMap = map.disunion("A")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A") to "V",
                setOf("B", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
        }

        test("should do nothing, when key is only one in its set") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act
            val newMap = map.disunion("A")

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A") to "V",
                setOf("D", "E", "F") to "X",
            )
            newMap shouldBeSameInstanceAs map
        }

        test("should throw, when key is not in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act/Assert
            shouldThrow<NoSuchElementException> {
                map.disunion("x")
            }
        }
    }
    
    context("compute()") {

        test("should compute a new value to a new set") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val (newMap, newValue) = map.compute("B") { r, v ->
                r shouldBe "B"
                v shouldBe null
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("B") to "XX",
            )
            newValue shouldBe "XX"
        }


        test("should compute a new value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to null as String?,
                )
            )

            // Act
            val (newMap, newValue) = map.compute("B") { r, v ->
                r shouldBe "A"
                v shouldBe null
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
            newValue shouldBe "XX"
        }

        test("should compute a new value to an existing set with an existing value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val (newMap, newValue) = map.compute("B") { r, v ->
                r shouldBe "A"
                v shouldBe "V"
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
            newValue shouldBe "XX"
        }
    }
    
    context("computeIfPresent()") {

        test("should compute no value to a new set") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                error("Unreachable")
            }

            // Assert
            newMap.toMap() shouldBe emptyMap<Set<String>, String>()
            newValue shouldBe null
        }


        test("should compute no value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to null as String?,
                )
            )

            // Act
            val (newMap, newValue) = map.computeIfPresent("B") { _, _ ->
                error("Unreachable")
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to null,
            )
            newValue shouldBe newValue
        }

        test("should compute a new value to an existing set with an existing value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val (newMap, newValue) = map.computeIfPresent("B") { r, v ->
                r shouldBe "A"
                v shouldBe "V"
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
            newValue shouldBe "XX"
        }
    }
    
    context("computeIfAbsent()") {

        test("should compute a new value to a new set") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                r shouldBe "B"
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("B") to "XX",
            )
            newValue shouldBe "XX"
        }


        test("should compute a new value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to null as String?,
                )
            )

            // Act
            val (newMap, newValue) = map.computeIfAbsent("B") { r ->
                r shouldBe "A"
                "XX"
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
            newValue shouldBe "XX"
        }

        test("should compute no value to an existing set with an existing value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val (newMap, newValue) = map.computeIfAbsent("B") { _ ->
                error("Unreachable")
            }

            // Assert
            newMap.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "V",
            )
            newValue shouldBe "V"
        }
    }
    
    context("builder()") {

        context("build()") {
            test("should return the same map as the original, when there are no changes") {
                // Arrange
                val map = factory.create(
                    mapOf(
                        setOf("A", "B", "C") to "V",
                    )
                )

                // Act
                val newMap = map.builder().build()

                // Assert
                newMap.toMap() shouldBe mapOf(
                    setOf("A", "B", "C") to "V",
                )
            }

            test("should apply any changes") {
                // Arrange
                val map = factory.create(
                    mapOf(
                        setOf("A", "B", "C") to "V",
                    )
                )

                // Act
                val builder = map.builder()
                builder.union("D", "E", { "XX" }) { _, _ -> error("Unreachable") }
                builder.union("C", "F", { error("Unreachable") }) { _, _ -> error("Unreachable") }
                val newMap = builder.build()

                // Assert
                newMap.toMap() shouldBe mapOf(
                    setOf("A", "B", "C", "F") to "V",
                    setOf("D", "E") to "XX",
                )
            }

            test("should not change intermediate results, when multiple calls are performed") {
                // Arrange
                val map = factory.create(
                    mapOf(
                        setOf("A", "B", "C") to "V",
                    )
                )

                // Act
                val builder = map.builder()
                builder.union("D", "E", { "XX" }) { _, _ -> error("Unreachable") }
                val newMap1 = builder.build()
                builder.union("C", "F", { error("Unreachable") }) { _, _ -> error("Unreachable") }
                val newMap2 = builder.build()

                // Assert
                newMap1.toMap() shouldBe mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E") to "XX",
                )
                newMap2.toMap() shouldBe mapOf(
                    setOf("A", "B", "C", "F") to "V",
                    setOf("D", "E") to "XX",
                )
            }
        }
    }
}