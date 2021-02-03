package com.storedobject.ui;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;

import java.util.stream.Stream;

public interface ObjectLoader<T extends StoredObject> {

    default String getCondition() {
        return null;
    }

    default String getOrderBy() {
        return null;
    }

    boolean isAllowAny();

    default void load() {
        load(getCondition(), getOrderBy());
    }

    default void load(String condition) {
        load(condition, getOrderBy());
    }

    void load(String condition, String orderBy);

    default void load(StoredObject master) {
        load(master, getCondition(), getOrderBy());
    }

    default void load(StoredObject master, String condition) {
        load(master, condition, getOrderBy());
    }

    default void load(StoredObject master, String condition, String orderBy) {
        load(0, master, condition, orderBy);
    }

    default void load(int linkType, StoredObject master) {
        load(linkType, master, getCondition(), getOrderBy());
    }

    default void load(int linkType, StoredObject master, String condition) {
        load(linkType, master, condition, getOrderBy());
    }

    void load(int linkType, StoredObject master, String condition, String orderBy);

    default void load(ObjectIterator<T> objects) {
        load(objects.stream());
    }

    void load(Stream<T> objects);

    @SuppressWarnings("unchecked")
    default void load(T... objects) {
        load(Stream.of(objects));
    }

    void clear();

    default void reload() {
        load();
    }

    void added(T item);

    void edited(T item);

    void deleted(T item);
}
