package net.pelsmaeker.unifiers

import io.kotest.core.spec.style.FunSpec

class UnionFindUnifierTests: FunSpec({
    include(testUnifier { map ->
        TermUnionFindUnifier().apply { addAll(map) }
    })

    include(testMutableUnifier { map ->
        TermUnionFindUnifier().apply { addAll(map) }
    })
})

class TermUnionFindUnifier : UnionFindUnifier<Term, TermVar>() {
    override fun asVar(term: Term): TermVar? = term as? TermVar
    override fun isEqualWithoutSubterms(
        term1: Term,
        term2: Term,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun withSubterms(term: Term, subterms: List<Term>): Term = when(term) {
        is ApplTerm -> term.copy(args = subterms)
        is ListTerm -> term.copy(elements = subterms)
        else -> term
    }
    override fun getSubterms(term: Term): List<Term> = term.subterms
}