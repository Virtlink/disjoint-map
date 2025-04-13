package net.pelsmaeker.collections

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

interface DisjointMapFactory {
    fun <K, V> create(initial: Map<Set<K>, V> = emptyMap()): DisjointMap<K, V>
}

fun testDisjointMap(
    factory: DisjointMapFactory,
) = funSpec {
    context("size") {
        test("should return zero, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Assert
            map.size shouldBe 0
        }

        test("should return the number of representatives, when each has its own value") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Assert
            map.size shouldBe 3
        }

        test("should return the number of keys") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A1", "A2", "A3") to "Va",
                    setOf("B1", "B2") to "V",
                    setOf("C1") to "V",
                )
            )

            // Assert
            map.size shouldBe 6
        }
    }

    context("isEmpty()") {
        test("should return true, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Assert
            map.isEmpty() shouldBe true
        }

        test("should return false, when the map is not empty") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Assert
            map.isEmpty() shouldBe false
        }

        test("should return false, when there are multiple keys") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A1", "A2", "A3") to "Va",
                    setOf("B1", "B2") to "V",
                    setOf("C1") to "V",
                )
            )

            // Assert
            map.isEmpty() shouldBe false
        }
    }

    context("get()") {
        test("should return null, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map["A"]

            // Assert
            result shouldBe null
        }

        test("should return null, when the key does not exist") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Act
            val result = map["D"]

            // Assert
            result shouldBe null
        }

        test("should return the value, when the key exists as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("A") shouldBe "A"  // Representative

            // Act
            val result = map["A"]

            // Assert
            result shouldBe "V"
        }

        test("should return the value, when the key exists but not as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val result = map["B"]

            // Assert
            result shouldBe "V"
        }

        test("should return null, when the value is null") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to (null as String?),
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val result = map["A"]

            // Assert
            result shouldBe null
        }
    }

    context("getOrDefault()") {
        test("should return the default value, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map.getOrDefault("A", "default")

            // Assert
            result shouldBe "default"
        }

        test("should return the default value, when the key does not exist") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Act
            val result = map.getOrDefault("D", "default")

            // Assert
            result shouldBe "default"
        }

        test("should return the value, when the key exists as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val result = map.getOrDefault("A", "default")

            // Assert
            result shouldBe "V"
        }

        test("should return the value, when the key exists but not as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val result = map.getOrDefault("B", "default")

            // Assert
            result shouldBe "V"
        }

        test("should return null, when the value is null") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to (null as String?),
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val result = map.getOrDefault("A", "default")

            // Assert
            result shouldBe null
        }
    }

    context("find()") {
        test("should return null, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map.find("A")

            // Assert
            result shouldBe null
        }

        test("should return null, when it does not exist in the map") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Act
            val result = map.find("D")

            // Assert
            result shouldBe null
        }

        test("should return itself, when it is a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val result = map.find("A")

            // Assert
            result shouldBe "A"
        }

        test("should return the representative, when it is not a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val result = map.find("B")

            // Assert
            result shouldBe "A"
        }
    }

    context("contains()") {
        test("should return false, when the map is empty") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map.contains("A")

            // Assert
            result shouldBe false
        }

        test("should return false, when the key does not exist") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "Va",
                    setOf("B") to "V",
                    setOf("C") to "V",
                )
            )

            // Act
            val result = map.contains("D")

            // Assert
            result shouldBe false
        }

        test("should return true, when the key exists as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("A") shouldBe "A" // Representative

            // Act
            val result = map.contains("A")

            // Assert
            result shouldBe true
        }

        test("should return true, when the key exists but not as a representative") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Assume
            map.find("B") shouldNotBe "B" // Not representative

            // Act
            val result = map.contains("B")

            // Assert
            result shouldBe true
        }
    }

    context("same") {
        test("should return false, when comparing the same elements that are not present in the map") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map.same("A", "A")

            // Assert
            result shouldBe false
        }

        test("should return false, when comparing different elements that are not present in the map") {
            // Arrange
            val map = factory.create<String, String>()

            // Act
            val result = map.same("A", "B")

            // Assert
            result shouldBe false
        }

        test("should return true, when comparing the same elements that are representatives") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "V",
                )
            )

            // Act
            val result = map.same("A", "A")

            // Assert
            result shouldBe true
        }

        test("should return false, when comparing different elements that are representatives") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A") to "V1",
                    setOf("B") to "V2",
                )
            )

            // Act
            val result = map.same("A", "B")

            // Assert
            result shouldBe false
        }

        test("should return true, when comparing different elements in the same component") {
            // Arrange
            val map = factory.create(
                mapOf(
                    setOf("A", "B", "C") to "V",
                )
            )

            // Act
            val result = map.same("A", "B")

            // Assert
            result shouldBe true
        }
    }
}