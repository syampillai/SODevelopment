package com.storedobject.core;

public interface ObjectsSetter<T extends StoredObject> extends ObjectSetter<T> {
    void setObjects(Iterable<T> objects);
}