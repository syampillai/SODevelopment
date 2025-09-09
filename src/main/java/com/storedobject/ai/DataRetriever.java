package com.storedobject.ai;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;

/**
 * Represents an interface for retrieving and managing stored objects of various types.
 * This interface provides default methods to list stored objects, count their instances,
 * and retrieve specific objects from storage.
 *
 * @author Syam
 */
public interface DataRetriever {

    /**
     * Lists all stored objects of the specified type.
     *
     * @param <T> the type of stored objects to be listed; it must extend {@code StoredObject}.
     * @param c the {@code Class} object representing the type of stored objects to be listed.
     * @return an {@code ObjectIterator<T>} containing all stored objects of the specified type.
     */
    default  <T extends StoredObject> ObjectIterator<T> list(Class<T> c) {
        return StoredObject.list(c);
    }

    /**
     * Counts the number of instances of the specified class that exist in storage.
     *
     * @param <T> the type of the class extending {@code StoredObject}.
     * @param c the class whose instances need to be counted.
     * @return the number of instances of the specified class in storage.
     */
    default <T extends StoredObject> int count(Class<T> c) {
        return StoredObject.count(c);
    }

    /**
     * Retrieves a stored object of the specified type and purpose.
     *
     * @param <T>     The type of the stored object to retrieve, extending {@code StoredObject}.
     * @param c       The class object representing the type of the stored object to retrieve.
     * @param purpose The purpose or reason for retrieving the stored object, represented as a string.
     * @return The stored object of the specified type.
     */
    default <T extends StoredObject> T get(Class<T> c, String purpose) {
        return StoredObject.get(c);
    }
}
