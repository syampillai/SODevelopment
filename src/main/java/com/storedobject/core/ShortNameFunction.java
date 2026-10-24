package com.storedobject.core;

import java.util.function.Function;

/**
 * Functional interface representing a short name resolution function to be applied to specific entities
 * or objects associated with a given formula. This interface provides a method that returns a
 * {@link Function} which can transform or resolve a short name into a specific object.
 * It is primarily used to map string-based short names into associated object instances based on
 * implementations.
 * <p>
 * The expected behavior is determined by the concrete implementation of the {@code getFunction} method,
 * allowing the resolution logic to vary depending on the context or specific entity handling requirements.
 * </p>
 * <p>
 * Note: The {@code getFunction} method may interact with context-sensitive data or lookup processes,
 * and its behavior is determined by the provided {@link Formula}.
 * </p>
 *
 * @author Syam
 */
@FunctionalInterface
public interface ShortNameFunction {

    /**
     * Returns a {@link Function} that resolves or transforms a string identifier
     * (short name) into an associated object instance based on the provided {@link Formula}.
     * This method enables dynamic or context-sensitive mapping of short names to specific
     * objects.
     *
     * @param forFormula the {@link Formula} context used to determine the resolution logic
     *                   for mapping short names to their corresponding objects
     * @return a {@link Function} that takes a {@link String} input (short name) and returns
     *         the resolved object associated with the input
     */
    Function<String, Object> getFunction(Formula forFormula);
}
