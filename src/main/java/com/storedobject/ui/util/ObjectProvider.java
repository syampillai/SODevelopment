package com.storedobject.ui.util;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

public interface ObjectProvider<T extends StoredObject> {

    Id getObjectId();

    T getObject();

    Class<T> getObjectClass();

    static <O extends StoredObject> ObjectProvider create(O object) {
        return new ObjectProvider() {

            private O so = object;

            @Override
            public Id getObjectId() {
                return so.getId();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Class<O> getObjectClass() {
                return (Class<O>) so.getClass();
            }

            @Override
            public O getObject() {
                return so;
            }
        };
    }
}
