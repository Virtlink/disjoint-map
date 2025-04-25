package net.pelsmaeker.unifiers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlin.collections.addAll

fun testMutableUnifier(
    createMutableUnifier: (Map<TermVar, Term>) -> MutableUnifier<Term, TermVar>
) = funSpec {
    context("composeWith()") {
        test("should return true and modify the unifier, when composing an empty unifier with an empty unifier") {
            // Arrange
            val unifier1 = createMutableUnifier(emptyMap())
            val unifier2 = createMutableUnifier(emptyMap())

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe true
            unifier1.isEmpty() shouldBe true
        }

        test("should return true and modify the unifier, when composing an empty unifier with a non-empty unifier") {
            // Arrange
            val entries2 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier1 = createMutableUnifier(emptyMap())
            val unifier2 = createMutableUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe true
            unifier1 shouldBe unifier2
        }

        test("should return true and modify the unifier, when composing a non-empty unifier with an empty unifier") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(emptyMap())

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe true
            unifier1 shouldBe createMutableUnifier(entries1)
        }

        test("should return true and modify the unifier, when composing two non-empty unifiers that are disjoint") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val entries2 = mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("b1") to TermVar("b2"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe true
            unifier1 shouldBe createMutableUnifier(entries1 + entries2)
        }

        test("should return true and modify the unifier, when composing two non-empty unifiers that are not disjoint but not conflicting") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val entries2 = mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b1") to TermVar("b2"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe true
            unifier1 shouldBe createMutableUnifier(entries1 + entries2)
        }

        test("should return false and not modify the unifier, when composing two conflicting unifiers") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val entries2 = mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b1") to StringTerm("bar"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe false
            unifier1 shouldBe createMutableUnifier(entries1)
        }

        test("should return false and not modify the unifier, when composing two unifiers that are indirectly conflicting") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b1"))),
                TermVar("b1") to StringTerm("foo"),
            )
            val entries2 = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b2"))),
                TermVar("b2") to StringTerm("bar"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe false
            unifier1 shouldBe createMutableUnifier(entries1)
        }

        test("should throw CyclicTermException, when composing with a unifier that would cause a cyclic term") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val entries2 = mapOf(
                TermVar("a2") to TermVar("a"),
            )
            val unifier1 = createMutableUnifier(entries1)
            val unifier2 = createMutableUnifier(entries2)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier1.composeWith(unifier2)
            }
        }
    }

    context("add()") {
        test("should return true and modify the unifier, when adding a new variable") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.add(TermVar("b"), TermVar("b1"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + (TermVar("b") to TermVar("b1")))
        }

        test("should return false and not modify the unifier, when adding a conflicting variable") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), StringTerm("bar"))

            // Assert
            result shouldBe false
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should return true and modify the unifier, when adding a variable that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), TermVar("b1"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + (TermVar("a") to TermVar("b1")))
        }

        test("should return true but not modify the unifier, when adding an entry that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), TermVar("a1"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should return false and not modify the unifier, when adding an indirectly conflicting entry") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b"))),
                TermVar("b") to StringTerm("foo"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), ApplTerm("F", listOf(StringTerm("bar"))))

            // Assert
            result shouldBe false
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should throw CyclicTermException, when adding an entry that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createMutableUnifier(entries)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier.add(TermVar("a2"), TermVar("a"))
            }
        }
    }

    context("addAll()") {
        test("should return true and modify the unifier, when adding new entries") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("c") to TermVar("c1"),
            ))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("c") to TermVar("c1"),
            ))
        }

        test("should return false and not modify the unifier, when adding a conflicting entry") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to StringTerm("bar"),
                TermVar("b") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe false
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should return true and modify the unifier, when adding a variable that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("c") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("c") to StringTerm("baz"),
            ))
        }

        test("should return true, when adding an entry that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("b") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + mapOf(
                TermVar("b") to StringTerm("baz"),
            ))
        }

        test("should throw CyclicTermException, when adding an entry that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createMutableUnifier(entries)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier.addAll(mapOf(
                    TermVar("a2") to TermVar("a1"),
                    TermVar("a1") to TermVar("a"),
                ))
            }
        }
    }

    context("unify()") {
        test("should return true and modify the unifier, when unifying two equal term variables") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), TermVar("a"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should return true and modify the unifier, when unifying two same term variables") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a2") to TermVar("a1"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), TermVar("a2"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should return true and modify the unifier, when unifying two different terms") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), StringTerm("foo"))

            // Assert
            result shouldBe true
            unifier shouldBe createMutableUnifier(entries + (TermVar("a") to StringTerm("foo")))
        }

        test("should return false and not modify the unifier, when unifying two conflicting terms") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createMutableUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), StringTerm("bar"))

            // Assert
            result shouldBe false
            unifier shouldBe createMutableUnifier(entries)
        }

        test("should throw CyclicTermException, when unifying a term that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createMutableUnifier(entries)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier.unify(
                    ApplTerm("A", listOf(TermVar("a"))),
                    ApplTerm("A", listOf(TermVar("a2"))),
                )
            }
        }

        // TODO: Add more tests for unification of complex terms
    }
}

