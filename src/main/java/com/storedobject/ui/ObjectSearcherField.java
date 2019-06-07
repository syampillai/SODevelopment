package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;

public class ObjectSearcherField<T extends StoredObject> extends ObjectGetField<T> {

    public ObjectSearcherField(Class<T> objectClass, ObjectSetter objectSetter) {
        this(null, objectClass, objectSetter);
    }

    public ObjectSearcherField(String label, Class<T> objectClass, ObjectSetter objectSetter) {
        super(label, objectClass);
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(Class<O> objectClass, ObjectSetter objectSetter) {
        return null;
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(String label, Class<O> objectClass, ObjectSetter objectSetter) {
        return null;
    }
}