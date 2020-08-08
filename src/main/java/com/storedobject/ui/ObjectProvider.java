package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

@FunctionalInterface
public interface ObjectProvider<T extends StoredObject> {

    default Id getObjectId() {
        return getObject().getId();
    }

    T getObject();

    default Class<T> getObjectClass() {
        //noinspection unchecked
        return (Class<T>) getObject().getClass();
    }
}