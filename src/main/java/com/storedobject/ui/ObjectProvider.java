package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

/**
 * Provider of sme sort of object.
 *
 * @param <T> Class type of the object to be provided.
 * @author Syam
 */
@FunctionalInterface
public interface ObjectProvider<T extends StoredObject> {

    /**
     * Get the Id of the object provided.
     *
     * @return Id of the object.
     */
    default Id getObjectId() {
        return getObject().getId();
    }

    /**
     * Get the object provided.
     *
     * @return Object provided.
     */
    T getObject();

    /**
     * Get the class of the object provided.
     *
     * @return Class of the object.
     */
    default Class<T> getObjectClass() {
        //noinspection unchecked
        return (Class<T>) getObject().getClass();
    }
}