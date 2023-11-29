package com.storedobject.core;

/**
 * represents something that has a name.
 *
 * @author Syam
 */
@FunctionalInterface
public interface HasName {

    /**
     * Get the name.
     *
     * @return The name.
     */
    String getName();
}
