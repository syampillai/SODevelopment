package com.storedobject.core;

import java.sql.Date;

/**
 * Functional interface representing an entity capable of providing a {@link Date}.
 * This interface is designed for use in scenarios where objects need to supply
 * a specific date value or expose a shared behavior for date-based operations.
 *
 * @author Syam
 */
@FunctionalInterface
public interface HasDate {

    /**
     * Retrieves the date associated with the implementing entity.
     *
     * @return the {@link Date} object provided by the entity
     */
    Date getDate();
}
