package net.pelsmaeker.unifiers

// Some term datastructures for tests

interface Term {
    val subterms: List<Term>
}

data class TermVar(
    val name: String,
) : Term {
    override val subterms: List<Term> get() = emptyList()
    override fun toString(): String = "?$name"
}

data class StringTerm(
    val value: String,
) : Term {
    override val subterms: List<Term> get() = emptyList()
    override fun toString(): String = "\"$value\""
}

data class IntTerm(
    val value: Int,
) : Term {
    override val subterms: List<Term> get() = emptyList()
    override fun toString(): String = value.toString()
}

data class ApplTerm(
    val op: String,
    val args: List<Term>,
) : Term {
    override val subterms: List<Term> get() = args
    override fun toString(): String = "$op(${args.joinToString(", ")})"
}

data class ListTerm(
    val elements: List<Term>,
) : Term {
    override val subterms: List<Term> get() = elements
    override fun toString(): String = "[${elements.joinToString(", ")}]"
}
