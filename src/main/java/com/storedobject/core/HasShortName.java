package com.storedobject.core;

/**
 * Represents something that has a short-name.
 *
 * @author Syam
 */
@FunctionalInterface
public interface HasShortName {

    /**
     * Get the short name.
     *
     * @return The short name.
     */
    String getShortName();
}
