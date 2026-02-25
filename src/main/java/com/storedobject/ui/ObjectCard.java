package com.storedobject.ui;

import com.storedobject.core.StoredObject;

public class ObjectCard<T extends StoredObject> extends Card {

    private T object;

    public ObjectCard() {
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
