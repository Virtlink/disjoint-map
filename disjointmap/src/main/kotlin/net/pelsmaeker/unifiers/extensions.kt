package net.pelsmaeker.unifiers


/**
 * Returns the result of applying the provided modifications on this unifier.
 *
 * The mutable unifier passed to the [mutator] closure has the same contents as this persistent unifier.
 *
 * @receiver The persistent unifier to mutate.
 * @param mutator A function that mutates the unifier.
 * @return A new persistent unifier with the provided modifications applied if the modifications were successful;
 * otherwise this instance if no modifications were made in the result of this operation;
 * otherwise, `null`.
 */
inline fun <T, V: T> PersistentUnifier<T, V>.mutate(mutator: (MutableUnifier<T, V>) -> Unit): PersistentUnifier<T, V>?
        = builder().apply(mutator).build()
