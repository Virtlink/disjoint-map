package net.pelsmaeker.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

interface MutableDisjointMapFactory {
    fun <K, V> create(initial: Map<Set<K>, V> = emptyMap()): MutableDisjointMap<K, V>
}

fun testMutableDisjointMap(
    factory: MutableDisjointMapFactory,
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
            val oldValue = map.set("C", 99)

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B") to 1,
                setOf("C") to 99,
            )
            oldValue shouldBe null
        }


        test("should set the value of the existing set, when the key is in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )
            // Act
            val oldValue = map.set("C", "XX")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "XX",
            )
            oldValue shouldBe "V"
        }

        test("should keep the sets disjoint, when the two sets get the same value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B") to "V",
                    setOf("D", "E") to "X",
                )
            )

            // Act
            val oldValue = map.set("B", "X")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B") to "X",
                setOf("D", "E") to "X",
            )
            oldValue shouldBe "V"
        }
    }

    context("remove()") {
        test("should remove the element but leave the rest of the set, when the key is non-representative") {
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
            val oldValue = map.remove("B")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
            oldValue shouldBe "V"
        }

        test("should remove the element but leave the rest of the set, when the key is representative") {
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
            val oldValue = map.remove("A")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("B", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
            oldValue shouldBe "V"
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
            val oldValue = map.remove("A")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("D", "E", "F") to "X",
            )
            oldValue shouldBe "V"
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
            val oldValue = map.remove("X")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "V",
                setOf("D", "E", "F") to "X",
            )
            oldValue shouldBe null
        }
    }

    context("clear()") {
        test("should remove all keys and values") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                    setOf("D", "E", "F") to "X",
                )
            )

            // Act
            map.clear()

            // Assert
            map.toMap() shouldBe emptyMap<String, String>()
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
            map.union("B", "D", { error("Unreachable") },
                unify = { a, b -> if (a == null) b else if (b == null) a else error("Unreachable") }
            )

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V",
            )
        }

        test("should unify the sets and values, when both keys are in sets") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V1",
                    setOf("D", "E", "F") to "V2",
                )
            )

            // Act
            map.union("B", "D", { error("Unreachable") }) { v1, v2 -> v1 + v2 }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V1V2",
            )
        }

        test("should unify sets and store null, when both keys are in sets and unifier returns null") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to ("V1" as String?),
                    setOf("D", "E", "F") to "V2",
                )
            )

            // Act
            map.union("B", "D", { error("Unreachable") }) { _, _ -> null }

            // Assert
            map.toMap() shouldBe mapOf(
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
            map.union("B", "D", { error("Unreachable") }) { _, _ -> error("Unreachable") }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C", "D", "E", "F") to "V",
            )
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
            map.find("B") shouldNotBe "B"  // Not representative

            // Act
            map.disunion("B")

            // Assert
            map.toMap() shouldBe mapOf(
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
            map.disunion("A")

            // Assert
            map.toMap() shouldBe mapOf(
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
            map.disunion("A")

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A") to "V",
                setOf("D", "E", "F") to "X",
            )
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
            val newValue = map.compute("B") { r, v ->
                r shouldBe "B"
                v shouldBe null
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("B") to "XX",
            )
            newValue shouldBe "XX"
        }


        test("should compute a new value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to (null as String?),
                )
            )

            // Act
            val newValue = map.compute("B") { r, v ->
                r shouldBe "A"
                v shouldBe null
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
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
            val newValue = map.compute("B") { r, v ->
                r shouldBe "A"
                v shouldBe "V"
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
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
            val newValue = map.computeIfPresent("B") { _, _ -> error("Unreachable") }

            // Assert
            map.toMap() shouldBe emptyMap<Set<String>, String>()
            newValue shouldBe null
        }


        test("should compute no value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to (null as String?),
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { _, _ -> error("Unreachable") }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to null,
            )
            newValue shouldBe null
        }

        test("should compute a new value to an existing set with an existing value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val newValue = map.computeIfPresent("B") { r, v ->
                r shouldBe "A"
                v shouldBe "V"
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
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
            val newValue = map.computeIfAbsent("B") { r ->
                r shouldBe "B"
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("B") to "XX",
            )
            newValue shouldBe "XX"
        }


        test("should compute a new value to an existing set with no value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to (null as String?),
                )
            )

            // Act
            val newValue = map.computeIfAbsent("B") { r ->
                r shouldBe "A"
                "XX"
            }

            // Assert
            map.toMap() shouldBe mapOf(
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
            val newValue = map.computeIfAbsent("B") { _ -> error("Unreachable") }

            // Assert
            map.toMap() shouldBe mapOf(
                setOf("A", "B", "C") to "V",
            )
            newValue shouldBe "V"
        }
    }
}