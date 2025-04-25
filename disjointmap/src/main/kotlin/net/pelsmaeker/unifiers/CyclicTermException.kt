package net.pelsmaeker.unifiers

/**
 * An exception that is thrown when a cyclic term is detected during unification.
 *
 * @property term The term that caused the cyclic term exception.
 * @property variable The variable that caused the cyclic term exception.
 * @param message The message to include in the exception; or `null` to use the default message.
 * @param cause The cause of the exception; or `null` if there is no cause.
 */
class CyclicTermException(
    val term: Any,
    val variable: Any,
    message: String? = null,
    cause: Throwable? = null,
): Exception(message ?: "Occurs check failed for $term and $variable", cause)
