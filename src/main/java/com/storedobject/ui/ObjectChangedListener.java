package com.storedobject.ui;

import com.storedobject.core.StoredObject;

public interface ObjectChangedListener<T extends StoredObject> {

    default void inserted(ObjectMasterData<T> object) {
        saved(object);
    }

    default void updated(ObjectMasterData<T> object) {
        saved(object);
    }

    default void saved(ObjectMasterData<T> object) {
    }

    default void deleted(ObjectMasterData<T> object) {
    }
}
