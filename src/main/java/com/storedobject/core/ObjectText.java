package com.storedobject.core;

import com.storedobject.common.Storable;

public final class ObjectText<T extends StoredObject> implements Storable {

    public ObjectText() {
    }

    public ObjectText(Id id) {
    }

    public ObjectText(T object) {
    }

    public ObjectText(String text) {
    }

    public ObjectText(ObjectText<T> objectText) {
    }

    public static <O extends StoredObject> ObjectText<O> create(Object value) {
        return null;
    }

    public void setId(Id id) {
    }

    public void setObject(T object) {
    }

    public void setText(String text) {
    }

    public Id getId() {
        return null;
    }

    public T getObject() {
        return null;
    }

    public String getText() {
        return null;
    }
}
