package com.storedobject.ui;

import com.storedobject.core.StoredObject;

import java.util.function.Consumer;

public class ObjectSearcherField<T extends StoredObject> extends ObjectGetField<T> {

    public ObjectSearcherField(Class<T> objectClass, Consumer<T> objectConsumer) {
        this(null, objectClass, objectConsumer);
    }

    public ObjectSearcherField(String label, Class<T> objectClass, Consumer<T> objectConsumer) {
        super(label, objectClass);
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(Class<O> objectClass, Consumer<O> objectConsumer) {
        return null;
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(String label, Class<O> objectClass, Consumer<O> objectConsumer) {
        return null;
    }

    public void setObjectConsumer(Consumer<T> objectConsumer) {
    }
}