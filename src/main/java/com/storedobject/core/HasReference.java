package com.storedobject.core;

import java.sql.Date;

public interface HasReference {

    SystemEntity getSystemEntity();

    int getNo();

    Date getDate();

    String getReference();

    /**
     * Get the prefix of the tag to be used for generating the reference.
     * <p>Warning: This should be a fixed string value.</p>
     *
     * @return A fixed tag value to be used as the prefix.
     */
    String getTagPrefix();

    /**
     * Get the suffix of the tag to be used for generating the reference.
     * <p>Note: This could be a dynamically generated string value based on the object instance.</p>
     *
     * @return A tag value (possibly a dynamic value) to be used as the suffix.
     */
    default String getTagSuffix() {
        return null;
    }

    default <O extends StoredObject> Amend<O> getAmend() {
        //noinspection unchecked
        return new Amend<>((O)this, 0);
    }

    record Amend<T extends StoredObject>(T object, int amendment) {
    }
}
