package com.storedobject.core;

import com.storedobject.common.Storable;

public final class ObjectText<T extends StoredObject> implements Storable {

    private T object;

    public ObjectText() {
    }

    public ObjectText(Id id) {
    }

    public ObjectText(T object) {
    }

    public ObjectText(String text) {
    }

    public ObjectText(ObjectText<T> objectText) {
        this.object = objectText.object;
    }

    public static <O extends StoredObject> ObjectText<O> create(Object value) {
        return null;
    }

    public void setId(Id id) {
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void setText(String text) {
    }

    public Id getId() {
        return object.getId();
    }

    public T getObject() {
        return object;
    }

    public String getText() {
        return object.toString();
    }

    public boolean isEmpty() {
        return "".equals(getText());
    }
}
