package com.storedobject.core;

public interface StoredObjectLink<T extends StoredObject> extends EditableList<T> {

    StoredObject getMaster();

    int getType();

    String getName();

    default boolean isDetail(T object) {
        return false;
    }

    default void attach() {
    }

    default void detach() {
    }

    default void save(Transaction transaction) throws Exception {
    }

    default StoredObjectLink<T> copy() {
        return null;
    }

    static <O extends StoredObject> StoredObjectLink<O> create(StoredObjectUtility.Link<O> link, StoredObject master) {
        return null;
    }
}