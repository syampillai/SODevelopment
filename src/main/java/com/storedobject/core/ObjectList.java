package com.storedobject.core;

import java.util.List;

public interface ObjectList<T extends StoredObject> extends List<T>, ObjectLoader<T>, Filtered<T> {

    default void close() {
        clear();
    }

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    int indexOf(Id id);

    boolean add(Id id);

    void refresh();

    T refresh(Id id);

    T refresh(T object);

    int getCacheLevel();
}
