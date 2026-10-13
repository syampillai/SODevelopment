package com.storedobject.core;

/**
 * Functional interface that represents an entity capable of owning and managing contact information.
 * The primary responsibility of implementations of this interface is to provide access to an object
 * that implements the {@code HasContacts} interface, which defines various methods for retrieving
 * and managing contact details such as phone numbers, email addresses, addresses, and other contact types.
 *
 * <p>This interface is particularly useful for maintaining a clear contract between an entity
 * that owns contact information and the associated operations to manage such data.</p>
 *
 * <p>Implementors must override the {@link #getContactOwner()} method to return an instance of
 * {@code HasContacts}, which provides the necessary functionality for managing contact information.</p>
 *
 * <p>As a functional interface, {@code ContactOwner} can be used in functional-style programming,
 * such as with lambda expressions or method references.</p>
 *
 * @author Syam
 */
@FunctionalInterface
public interface ContactOwner {

    /**
     * Retrieves the entity that manages contact information.
     *
     * @return an instance of {@code HasContacts} that provides access to methods for retrieving
     * and managing contact details, such as phone numbers, email addresses, and addresses.
     */
    HasContacts getContactOwner();
}
