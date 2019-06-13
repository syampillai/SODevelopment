package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

public interface ObjectProvider<T extends StoredObject> {

    default Id getObjectId() {
        return getObject().getId();
    }

    T getObject();

    default Class<T> getObjectClass() {
        //noinspection unchecked
        return (Class<T>) getObject().getClass();
    }

    static <O extends StoredObject> ObjectProvider<O> create(O so) {
        return new ObjectProvider<O>() {

            private O so;

            @Override
            public O getObject() {
                return so;
            }
        };
    }
}