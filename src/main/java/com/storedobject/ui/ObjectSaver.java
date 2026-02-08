package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.Transaction;

/**
 * Functional interface to define a contract for saving objects of type {@code T}
 * that extend {@code StoredObject}. Implementations of this interface should
 * define how to persist the given object within the context of a provided
 * transaction.
 *
 * @param <T> The type of object to be saved, must extend {@code StoredObject}.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ObjectSaver<T extends StoredObject> {

    /**
     * Saves the provided object of type {@code T} within the context of the specified transaction.
     * Implementations should define the persistence logic. If any error occurs during the save operation,
     * an exception is thrown.
     *
     * @param transaction The transaction context in which the object is to be saved.
     * @param object The object of type {@code T} to be saved. It must extend {@code StoredObject}.
     * @throws Exception If an error occurs during the save operation.
     */
    void save(Transaction transaction, T object) throws Exception;
}
