package com.virtlink;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Nullness helper function.
 */
public final class N {

    /**
     * Helper function for Kotlin that converts an explicitly nullable value {code T?} into one that is only null
     * if {@code T} is a nullable type.
     *
     * For example, if you have a {@link Map}, its {@code get} function will return either the value associated
     * with the key, or {@code null} if not found. Therefore its return type in Kotlin is {@code V?}, nullable {@code V}.
     * However, there is also the option for {@code get} to return {@code null} when the key is found but the
     * actual stored value is {@code null}. Therefore, we do not want to insert an explicit non-null assertion
     * ((@code !!) in Kotlin) because that would throw an exception if the result is {@code null}.
     *
     * Instead, this function silences the nullness checker without throwing an exception.
     *
     * @param value the value of type {@code T?}
     * @param <T> the type of the value
     * @return the value of type {@code T}
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T of(@Nullable T value) { return value; }

}