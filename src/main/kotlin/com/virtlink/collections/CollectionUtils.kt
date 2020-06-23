package com.virtlink.collections


/**
 * Zips two collections of equal length.
 *
 * @return A list of pairs, one from each collection.
 */
internal infix fun <T, R> Collection<T>.zipEqualLength(other: Collection<R>): List<Pair<T, R>> {
    require(this.size == other.size)
    return this zip other
}

/**
 * Returns a string representation of the specified iterable.
 *
 * @param iterable the iterable
 * @return the string representation
 */
internal fun listToString(iterable: Iterable<*>): String {
    val iterator = iterable.iterator()
    return StringBuilder().apply {
        append("[")
        var hasNext = iterator.hasNext()
        while (hasNext) {
            val obj = iterator.next()
            if (obj === iterable)
                append("<this>")
            else
                append(obj)
            hasNext = iterator.hasNext()
            if (hasNext)
                append(", ")
        }
        append("]")
    }.toString()
}

/**
 * Returns whether two ordered iterables are equal.
 *
 * @param ax the first iterable
 * @param bx the second iterable
 * @return `true` when they are equal; otherwise, `false`
 */
internal fun listEquals(ax: Iterable<*>, bx: Iterable<*>): Boolean {
    val axIterator = ax.iterator()
    val bxIterator = bx.iterator()
    while (axIterator.hasNext() && bxIterator.hasNext()) {
        val a = axIterator.next()
        val b = bxIterator.next()
        if (a != b) return false
    }
    if (axIterator.hasNext() != bxIterator.hasNext()) return false
    return true
}

/**
 * Returns the hash code for the given ordered iterable.
 *
 * @param iterable the iterable
 * @return the hash code
 */
internal fun listHashCode(iterable: Iterable<*>): Int {
    val iterator = iterable.iterator()
    var result = 1
    while (iterator.hasNext()) {
        val element = iterator.next()
        result = 31 * result + (element?.hashCode() ?: 0)
    }
    return result
}

/**
 * Returns whether two sets are equal.
 *
 * @param ax the first set
 * @param bx the second set
 * @return `true` when they are equal; otherwise, `false`
 */
internal fun setEquals(ax: Collection<*>, bx: Collection<*>): Boolean {
    // if every element in this set is in the other set
    // and every element in the other set is in this set
    // then the sets are equal.
    return ax.all { it in bx } && bx.all { it in ax }
}

/**
 * Returns the hash code for the given unordered iterable.
 *
 * @param iterable the iterable
 * @return the hash code
 */
internal fun setHashCode(iterable: Iterable<*>): Int {
    val iterator = iterable.iterator()
    var result = 1
    while (iterator.hasNext()) {
        val element = iterator.next()
        // commutative operation
        result += 31 * (element?.hashCode() ?: 0)
    }
    return result
}