package com.storedobject.core;

public interface ObjectsSetter extends ObjectSetter {
    void setObjects(Iterable<? extends StoredObject> object);
}