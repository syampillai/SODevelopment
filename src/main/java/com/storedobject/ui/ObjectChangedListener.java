package com.storedobject.ui;

import com.storedobject.core.StoredObject;

public interface ObjectChangedListener<T extends StoredObject> {

    default void inserted(T object) {
        saved(object);
    }

    default void updated(T object) {
        saved(object);
    }

    default void saved(T object) {
    }

    default void deleted(T object) {
    }

    default void undeleted(T object) {
    }
}
