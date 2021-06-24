package com.storedobject.core;

import java.util.function.Consumer;

@FunctionalInterface
public interface ObjectConsumer<T extends StoredObject> extends Consumer<T> {

    default void setObject(T object) {
        accept(object);
    }

    default void setObject(Id objectId) {
    }

    default Class<T> getObjectClass() {
        return null;
    }

    default boolean isAllowAny() {
        return false;
    }
}