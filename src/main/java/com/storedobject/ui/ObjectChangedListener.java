package com.storedobject.ui;

import com.storedobject.core.StoredObject;

/**
 * "Object Changed" listener to track changes to the object.
 *
 * @author Syam
 */
public interface ObjectChangedListener<T extends StoredObject> {

    /**
     * Fired when a new object is inserted. The default implementation invokes {@link #saved(StoredObject)}.
     *
     * @param object Object.
     */
    default void inserted(T object) {
        saved(object);
    }

    /**
     * Fired when a new object is updated. The default implementation invokes {@link #saved(StoredObject)}.
     *
     * @param object Object.
     */
    default void updated(T object) {
        saved(object);
    }

    /**
     * Fired when a new object is saved. The default implementation does nothing.
     *
     * @param object Object.
     */
    default void saved(T object) {
    }

    /**
     * Fired when a new object is deleted. The default implementation does nothing.
     *
     * @param object Object.
     */
    default void deleted(T object) {
    }

    /**
     * Fired when a new object is undeleted. The default implementation does nothing.
     *
     * @param object Object.
     */
    default void undeleted(T object) {
    }
}
