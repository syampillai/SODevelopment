package com.storedobject.core;

/**
 * The interface to denote that a {@link StoredObject} class is keeping details of a "master"
 * {@link StoredObject} class.
 *
 * @author Syam
 */
public interface Detail {

    /**
     * Get the {@link Id} of the detail class instance.
     *
     * @return {@link Id} of the detail class instance.
     */
    Id getId();

    /**
     * Get the {@link Id} of the class instance that makes this instance unique. (By default, generally,
     * the {@link Id} of this instance is returned).
     *
     * @return {@link Id} of the class instance that makes this instance unique.
     */
    default Id getUniqueId() {
        return getId();
    }

    /**
     * Get the unique value that determines the uniqueness of the detail entries. By
     * default, <code>null</code> is returned so that no uniqueness can be checked using this. (In most cases,
     * {@link #getUniqueId()} may be enough unless some other type of values need to be checked).
     *
     * @return Return the value to determine the uniqueness of the detail entries.
     */
    default Object getUniqueValue() {
        return null;
    }

    /**
     * Copy the attribute values from another instance. (This method is no more in use and may be removed
     * in the future).
     *
     * @param detail Another instance from which values to be copied.
     */
    default void copyValuesFrom(Detail detail) {
    }

    /**
     * Check if the given class is a "master" fpr this class. (Must return a consistent value).
     *
     * @param masterClass Class to check.
     * @return True if it is a "master" to this detail class. Otherwise, false.
     */
    boolean isDetailOf(Class<? extends StoredObject> masterClass);
}
