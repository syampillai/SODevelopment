package com.storedobject.core;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ObjectList<T extends StoredObject> extends List<T>, ObjectLoader<T>, Filtered<T> {

    default void close() {
        clear();
    }

    @Override
    default void clear() {
        ObjectLoader.super.clear();
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

    default Stream<Id> idStream(int startingIndex, int endingIndex) {
        return stream(startingIndex, endingIndex).map(StoredObject::getId);
    }

    default Stream<Id> idStreamAll(int startingIndex, int endingIndex) {
        return streamAll(startingIndex, endingIndex).map(StoredObject::getId);
    }

    void setProcessor(Consumer<T> processor);
}
