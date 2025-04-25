package net.pelsmaeker.unifiers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe


fun testPersistentUnifier(
    createPersistentUnifier: (Map<TermVar, Term>) -> PersistentUnifier<Term, TermVar>
) = funSpec {
    context("composeWith()") {
        test("should return a new unifier, when composing an empty unifier with an empty unifier") {
            // Arrange
            val unifier1 = createPersistentUnifier(emptyMap())
            val unifier2 = createPersistentUnifier(emptyMap())

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe createPersistentUnifier(emptyMap())
            unifier1 shouldBe createPersistentUnifier(emptyMap()) // Ensure immutability
        }

        test("should return a new unifier, when composing an empty unifier with a non-empty unifier") {
            // Arrange
            val entries2 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier1 = createPersistentUnifier(emptyMap())
            val unifier2 = createPersistentUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe unifier2
            unifier1 shouldBe createPersistentUnifier(emptyMap()) // Ensure immutability
        }

        test("should return a new unifier, when composing a non-empty unifier with an empty unifier") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(emptyMap())

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe unifier1
            unifier1 shouldBe createPersistentUnifier(entries1) // Ensure immutability
        }

        test("should return a new unifier, when composing two non-empty unifiers that are disjoint") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val entries2 = mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("b1") to TermVar("b2"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe createPersistentUnifier(entries1 + entries2)
            unifier1 shouldBe createPersistentUnifier(entries1) // Ensure immutability
        }

        test("should return a new unifier, when composing two non-empty unifiers that are not disjoint but not conflicting") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val entries2 = mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b1") to TermVar("b2"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe createPersistentUnifier(entries1 + entries2)
            unifier1 shouldBe createPersistentUnifier(entries1) // Ensure immutability
        }

        test("should return null, when composing two conflicting unifiers") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val entries2 = mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b1") to StringTerm("bar"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe null
            unifier1 shouldBe createPersistentUnifier(entries1) // Ensure immutability
        }

        test("should return null, when composing two unifiers that are indirectly conflicting") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b1"))),
                TermVar("b1") to StringTerm("foo"),
            )
            val entries2 = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b2"))),
                TermVar("b2") to StringTerm("bar"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(entries2)

            // Act
            val result = unifier1.composeWith(unifier2)

            // Assert
            result shouldBe null
            unifier1 shouldBe createPersistentUnifier(entries1) // Ensure immutability
        }

        test("should throw CyclicTermException, when composing with a unifier that would cause a cyclic term") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val entries2 = mapOf(
                TermVar("a2") to TermVar("a"),
            )
            val unifier1 = createPersistentUnifier(entries1)
            val unifier2 = createPersistentUnifier(entries2)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier1.composeWith(unifier2)
            }
        }
    }

    context("add()") {
        test("should return a new unifier, when adding a new variable") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.add(TermVar("b"), TermVar("b1"))

            // Assert
            result shouldBe createPersistentUnifier(entries + (TermVar("b") to TermVar("b1")))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return null, when adding a conflicting variable") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), StringTerm("bar"))

            // Assert
            result shouldBe null
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when adding a variable that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), TermVar("b1"))

            // Assert
            result shouldBe createPersistentUnifier(entries + (TermVar("a") to TermVar("b1")))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when adding an entry that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), TermVar("a1"))

            // Assert
            result shouldBe createPersistentUnifier(entries)
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return null, when adding an indirectly conflicting entry") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("b"))),
                TermVar("b") to StringTerm("foo"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.add(TermVar("a"), ApplTerm("F", listOf(StringTerm("bar"))))

            // Assert
            result shouldBe null
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should throw CyclicTermException, when adding an entry that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createPersistentUnifier(entries)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier.add(TermVar("a2"), TermVar("a"))
            }
        }
    }

    context("addAll()") {
        test("should return a new unifier, when adding new entries") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("c") to TermVar("c1"),
            ))

            // Assert
            result shouldBe createPersistentUnifier(entries + mapOf(
                TermVar("b") to TermVar("b1"),
                TermVar("c") to TermVar("c1"),
            ))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return null, when adding a conflicting entry") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to StringTerm("bar"),
                TermVar("b") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe null
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when adding a variable that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe createPersistentUnifier(entries + mapOf(
                TermVar("a") to TermVar("b1"),
                TermVar("b") to StringTerm("baz"),
            ))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when adding an entry that is already in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.addAll(mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("b") to StringTerm("baz"),
            ))

            // Assert
            result shouldBe createPersistentUnifier(entries + mapOf(
                TermVar("b") to StringTerm("baz"),
            ))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should throw CyclicTermException, when adding an entry that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createPersistentUnifier(entries)

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
        test("should return a new unifier, when unifying two equal term variables") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), TermVar("a"))

            // Assert
            result shouldBe createPersistentUnifier(entries)
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when unifying two same term variables") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a2") to TermVar("a1"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), TermVar("a2"))

            // Assert
            result shouldBe createPersistentUnifier(entries)
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return a new unifier, when unifying two different terms") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), StringTerm("foo"))

            // Assert
            result shouldBe createPersistentUnifier(entries + (TermVar("a") to StringTerm("foo")))
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should return null, when unifying two conflicting terms") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to StringTerm("foo"),
            )
            val unifier = createPersistentUnifier(entries)

            // Act
            val result = unifier.unify(TermVar("a"), StringTerm("bar"))

            // Assert
            result shouldBe null
            unifier shouldBe createPersistentUnifier(entries) // Ensure immutability
        }

        test("should throw CyclicTermException, when unifying a term that would cause a cyclic term") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("a2"), StringTerm("test"))),
            )
            val unifier = createPersistentUnifier(entries)

            // Act & Assert
            shouldThrow<CyclicTermException> {
                unifier.unify(
                    ApplTerm("A", listOf(TermVar("a"))),
                    ApplTerm("A", listOf(TermVar("a2"))),
                )
            }
        }
    }


}