package net.pelsmaeker.unifiers

internal class UnifyOperation<T, V: T> {
    /** The queue of pairs of terms to unify. */
    private val worklist: ArrayDeque<Pair<T, T>> = ArrayDeque<Pair<T, T>>()

    constructor(left: T, right: T) {
        worklist.add(Pair(left, right))
    }

    constructor(pairs: Iterable<Pair<T, T>>) {
        worklist.addAll(pairs)
    }

    constructor(unifier: Unifier<T, V>) {
        TODO()
    }


}