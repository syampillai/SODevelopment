package com.storedobject.core;

/**
 * Interface to denote that the {@link Detail} interface allows only a single instance.
 *
 * @author Syam
 */
public interface SingleInstanceDetail extends Detail {

    /**
     * By overriding this method, a fixed value is returned as the unique value so that only one instance can exist
     * for a specific master instance.
     * <p>Note: This is the mechanism used for ensuring single instance. If this method is overridden, single
     * instance is not guaranteed.</p>
     *
     * @return Returns an empty string as the fixed value to enforce uniqueness.
     */
    @Override
    default Object getUniqueValue() {
        return "";
    }
}
