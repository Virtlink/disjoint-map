@file:Suppress("ReplaceCallWithBinaryOperator")

package net.pelsmaeker.unifiers

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.shouldBe

internal fun testUnifier(
    createUnifier: (Map<TermVar, Term>) -> Unifier<Term, TermVar>
) = funSpec {

    context("isEmpty()") {
        test("should return true, when the unifier is empty") {
            // Arrange
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isEmpty()

            // Assert
            result shouldBe true
        }

        test("should return false, when the unifier is not empty") {
            // Arrange
            val unifier = createUnifier(mapOf(
                TermVar("a") to TermVar("a1"),
            ))

            // Act
            val result = unifier.isEmpty()

            // Assert
            result shouldBe false
        }
    }

    context("isNotEmpty()") {
        test("should return false, when the unifier is empty") {
            // Arrange
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isNotEmpty()

            // Assert
            result shouldBe false
        }

        test("should return true, when the unifier is not empty") {
            // Arrange
            val unifier = createUnifier(mapOf(
                TermVar("a") to TermVar("a1"),
            ))

            // Act
            val result = unifier.isNotEmpty()

            // Assert
            result shouldBe true
        }
    }

    context("find()") {
        test("should return null, when the variable is not in the unifier") {
            // Arrange
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.find(TermVar("a"))

            // Assert
            result shouldBe null
        }

        test("should return the representative, when the variable is in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.find(TermVar("a"))

            // Assert
            result shouldBeOneOf listOf(
                TermVar("a"),
                TermVar("a1"),
                TermVar("a2"),
            )
        }
    }

    context("get()") {
        test("should return the term itself, when the term is not a variable") {
            // Arrange
            val unifier = createUnifier(emptyMap())
            val term = StringTerm("test")

            // Act
            val result = unifier[term]

            // Assert
            result shouldBe term
        }

        test("should return the term itself, when the term is a variable and is not in the unifier") {
            // Arrange
            val unifier = createUnifier(emptyMap())
            val term = TermVar("a")

            // Act
            val result = unifier[term]

            // Assert
            result shouldBe term
        }

        test("should return the representative variable, when the term is a variable and is in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier[TermVar("a")]

            // Assert
            result shouldBeOneOf listOf(
                TermVar("a"),
                TermVar("a1"),
                TermVar("a2"),
            )
        }

        test("should return the uninstantiated representative term, when the term is a variable and is in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to ApplTerm("A", listOf(TermVar("x"))),
                TermVar("x") to TermVar("y"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier[TermVar("a")]

            // Assert
            result shouldBe ApplTerm("A", listOf(TermVar("x")))
        }
    }

    context("contains()") {
        test("should return false, when the variable is not in the unifier") {
            // Arrange
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.contains(TermVar("a"))

            // Assert
            result shouldBe false
        }

        test("should return true, when the variable is in the unifier") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.contains(TermVar("a"))

            // Assert
            result shouldBe true
        }
    }

    context("same()") {
        test("should return true, when the terms are equal variables but not in the unifier") {
            // Arrange
            val term1 = TermVar("a")
            val term2 = TermVar("a")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe true
        }

        test("should return true, when the terms are equal terms but not in the unifier") {
            // Arrange
            val term1 = StringTerm("test")
            val term2 = StringTerm("test")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe true
        }

        test("should return true, when the terms are equal and their variables are equal in the unifier") {
            // Arrange
            val term1 = ApplTerm("A", listOf(TermVar("a1")))
            val term2 = ApplTerm("A", listOf(TermVar("a2")))
            val entries = mapOf(
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe true
        }

        test("should return true, when the terms are equal when their variables fully instantiated") {
            // Arrange
            val term1 = ApplTerm("A", listOf(TermVar("a1"), StringTerm("foo")))
            val term2 = ApplTerm("A", listOf(IntTerm(1), TermVar("b1")))
            val entries = mapOf(
                TermVar("a1") to IntTerm(1),
                TermVar("b1") to TermVar("b2"),
                TermVar("b2") to StringTerm("foo"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe true
        }

        test("should return false, when the terms are not equal when their variables are fully instantiated") {
            // Arrange
            val term1 = ApplTerm("A", listOf(TermVar("a1"), StringTerm("foo")))
            val term2 = ApplTerm("A", listOf(IntTerm(1), TermVar("b1")))
            val entries = mapOf(
                TermVar("a1") to IntTerm(2),
                TermVar("b1") to TermVar("b2"),
                TermVar("b2") to StringTerm("bar"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe false
        }

        test("should return false, when the terms are not equal") {
            // Arrange
            val term1 = ApplTerm("A", listOf(IntTerm(2), StringTerm("foo")))
            val term2 = ApplTerm("B", listOf(IntTerm(1), TermVar("b1")))
            val entries = mapOf(
                TermVar("b1") to TermVar("b2"),
                TermVar("b2") to StringTerm("foo"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.same(term1, term2)

            // Assert
            result shouldBe false
        }
    }

    context("instantiate()") {
        test("should return the term variable, when it is not in the unifier") {
            // Arrange
            val term = TermVar("a")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.instantiate(term)

            // Assert
            result shouldBe term
        }

        test("should return the term, when the terms contains no variables") {
            // Arrange
            val term = ApplTerm("A", listOf(IntTerm(1), StringTerm("test")))
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.instantiate(term)

            // Assert
            result shouldBe term
        }

        test("should replace the variables with the representative variables in the unifier") {
            // Arrange
            val term1 = ApplTerm("A", listOf(TermVar("a1")))
            val term2 = ApplTerm("A", listOf(TermVar("a2")))
            val entries = mapOf(
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result1 = unifier.instantiate(term1)
            val result2 = unifier.instantiate(term2)

            // Assert
            result1 shouldBe result2
        }

        test("should replace the variables with the terms in the unifier") {
            // Arrange
            val term1 = ApplTerm("A", listOf(TermVar("a1"), StringTerm("foo")))
            val term2 = ApplTerm("A", listOf(IntTerm(1), TermVar("b1")))
            val term3 = ApplTerm("A", listOf(IntTerm(1), TermVar("b2")))
            val entries = mapOf(
                TermVar("a1") to IntTerm(1),
                TermVar("b1") to TermVar("b2"),
                TermVar("b2") to StringTerm("foo"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result1 = unifier.instantiate(term1)
            val result2 = unifier.instantiate(term2)
            val result3 = unifier.instantiate(term3)

            // Assert
            val expected = ApplTerm("A", listOf(IntTerm(1), StringTerm("foo")))
            result1 shouldBe expected
            result2 shouldBe expected
            result3 shouldBe expected
        }
    }

    context("getFreeVars()") {
        test("should return an empty set, when the term contains no variables") {
            // Arrange
            val term = StringTerm("test")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.getFreeVars(term)

            // Assert
            result shouldBe emptySet()
        }

        test("should return a set with the free variables in the term, when they don't occur in the unifier") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.getFreeVars(term)

            // Assert
            result shouldBe setOf(TermVar("a"))
        }

        test("should return a set with the free variables in the term, when they occur in the unifier") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.getFreeVars(term)

            // Assert
            result shouldBe setOf(TermVar("a"))
        }

        test("should return a set with the free variables in the term, when they occur in the unifier and are instantiated") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a1") to IntTerm(1),
                TermVar("a") to TermVar("a1"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.getFreeVars(term)

            // Assert
            result shouldBe emptySet()
        }

        test("should return free variables in the instantiated term, when they occur in the unifier and are instantiated") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a1") to ApplTerm("F", listOf(TermVar("x"))),
                TermVar("a") to TermVar("a1"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.getFreeVars(unifier.instantiate(term))

            // Assert
            result shouldBe setOf(TermVar("x"))
        }
    }

    context("isGround()") {
        test("should return true, when the term contains no variables") {
            // Arrange
            val term = StringTerm("test")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isGround(term)

            // Assert
            result shouldBe true
        }

        test("should return false, when the term contains a variable that is not in the unifier") {
            // Arrange
            val term = TermVar("a")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isGround(term)

            // Assert
            result shouldBe false
        }

        test("should return true, when the term is fully instantiated") {
            // Arrange
            val term = ApplTerm("A", listOf(IntTerm(1), StringTerm("test")))
            val entries = mapOf(
                TermVar("a") to IntTerm(1),
                TermVar("b") to StringTerm("test"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.isGround(term)

            // Assert
            result shouldBe true
        }

        test("should return false, when the term is not fully instantiated") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a") to ApplTerm("F", listOf(TermVar("x"))),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.isGround(term)

            // Assert
            result shouldBe false
        }
    }

    context("isCyclic()") {
        test("should return false, when the term contains no variables") {
            // Arrange
            val term = StringTerm("test")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isCyclic(term)

            // Assert
            result shouldBe false
        }

        test("should return false, when the term contains a variable that is not in the unifier") {
            // Arrange
            val term = TermVar("a")
            val unifier = createUnifier(emptyMap())

            // Act
            val result = unifier.isCyclic(term)

            // Assert
            result shouldBe false
        }

        test("should return true, when the term is cyclic") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a") to term,
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.isCyclic(term)

            // Assert
            result shouldBe true
        }

        test("should return false, when the term is not cyclic") {
            // Arrange
            val term = ApplTerm("A", listOf(TermVar("a"), StringTerm("test")))
            val entries = mapOf(
                TermVar("a") to StringTerm("test"),
            )
            val unifier = createUnifier(entries)

            // Act
            val result = unifier.isCyclic(term)

            // Assert
            result shouldBe false
        }
    }

    context("equals()") {
        test("should return true, when comparing an empty unifier to an empty unifier") {
            // Arrange
            val unifier1 = createUnifier(emptyMap())
            val unifier2 = createUnifier(emptyMap())

            // Act
            val result12 = unifier1.equals(unifier2)
            val result21 = unifier2.equals(unifier1)
            val hash1 = unifier1.hashCode()
            val hash2 = unifier2.hashCode()

            // Assert
            result12 shouldBe true
            result21 shouldBe true
            hash1 shouldBe hash2
        }

        test("should return false, when comparing an empty unifier to a non-empty unifier") {
            // Arrange
            val entries2 = mapOf(
                TermVar("a") to TermVar("a1"),
            )
            val unifier1 = createUnifier(emptyMap())
            val unifier2 = createUnifier(entries2)

            // Act
            val result12 = unifier1.equals(unifier2)
            val result21 = unifier2.equals(unifier1)

            // Assert
            result12 shouldBe false
            result21 shouldBe false
        }

        test("should return true, when the unifiers were created from the same entries") {
            // Arrange
            val entries = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),
                TermVar("b") to TermVar("b1"),
                TermVar("b2") to TermVar("b1"),
                TermVar("c") to ApplTerm("C", listOf(TermVar("x"), TermVar("y"))),
                TermVar("d") to ApplTerm("D", listOf(TermVar("a"), TermVar("b"))),
                TermVar("e") to ApplTerm("E", listOf(TermVar("d"))),
                TermVar("f") to ApplTerm(
                    "F1",
                    listOf(ApplTerm("F2", listOf(TermVar("a"), TermVar("b"))), TermVar("c"))
                ),
            )
            val unifier1 = createUnifier(entries)
            val unifier2 = createUnifier(entries)

            // Act
            val result12 = unifier1.equals(unifier2)
            val result21 = unifier2.equals(unifier1)
            val hash1 = unifier1.hashCode()
            val hash2 = unifier2.hashCode()

            // Assert
            result12 shouldBe true
            result21 shouldBe true
            hash1 shouldBe hash2
        }

        test("should return true, when the unifiers boil down to the same equalities") {
            // Arrange
            val entries1 = mapOf(
                TermVar("a") to TermVar("a1"),
                TermVar("a1") to TermVar("a2"),

                TermVar("b") to TermVar("b1"),
                TermVar("b2") to TermVar("b1"),

                TermVar("c") to ApplTerm("C", listOf(TermVar("x"), TermVar("y"))),

                TermVar("d") to ApplTerm("D", listOf(TermVar("a"), TermVar("b"))),

                TermVar("e") to ApplTerm("E", listOf(TermVar("d"))),

                TermVar("f") to ApplTerm(
                    "F1",
                    listOf(ApplTerm("F2", listOf(TermVar("a"), TermVar("b"))), TermVar("c"))
                ),
            )
            val entries2 = mapOf(
                TermVar("a2") to TermVar("a1"),
                TermVar("a1") to TermVar("a"),

                TermVar("b1") to TermVar("b"),
                TermVar("b1") to TermVar("b2"),

                TermVar("c") to ApplTerm("C", listOf(TermVar("x"), TermVar("y"))),

                TermVar("d") to ApplTerm("D", listOf(TermVar("a2"), TermVar("b1"))),

                TermVar("e") to ApplTerm("E", listOf(TermVar("d"))),

                TermVar("f") to ApplTerm(
                    "F1",
                    listOf(ApplTerm("F2", listOf(TermVar("a1"), TermVar("b"))), TermVar("c"))
                ),
            )
            val unifier1 = createUnifier(entries1)
            val unifier2 = createUnifier(entries2)

            // Act
            val result12 = unifier1.equals(unifier2)
            val result21 = unifier2.equals(unifier1)
            val hash1 = unifier1.hashCode()
            val hash2 = unifier2.hashCode()

            // Assert
            result12 shouldBe true
            result21 shouldBe true
            hash1 shouldBe hash2
        }
    }
}


