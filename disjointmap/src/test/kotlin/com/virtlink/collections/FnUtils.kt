package com.virtlink.collections

/**
 * Lifts a function that does not accept null to one that does,
 * invoking it only when both arguments are non-null;
 * otherwise returning the argument that is non-null.
 */
fun <T> lift(f: (T, T) -> T): (T?, T?) -> T? = { a, b ->
    when {
        a != null && b != null -> f(a, b)
        a != null -> a
        b != null -> b
        else -> null
    }
}