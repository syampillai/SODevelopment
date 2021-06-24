package com.storedobject.core;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public interface EditableList<T> {

    boolean contains(T item);

    boolean isAdded(T item);

    boolean isDeleted(T item);

    boolean isEdited(T item);

    Stream<T> streamAll();

    default Stream<T> stream() {
        return streamAll().filter(item -> !isDeleted(item));
    }

    default Stream<T> streamAdded() {
        return streamAll().filter(this::isAdded);
    }

    default Stream<T> streamEdited() {
        return streamAll().filter(this::isEdited);
    }

    default Stream<T> streamDeleted() {
        return streamAll().filter(this::isDeleted);
    }

    int size();

    boolean append(T item);

    default void append(Iterable<T> items) {
        items.forEach(this::append);
    }

    default boolean add(int index, T item) {
        return add(item);
    }

    boolean add(T item);

    boolean delete(T item);

    boolean undelete(T item);

    boolean update(T item);

    default boolean isSavePending() {
        return streamAll().anyMatch(o -> isAdded(o) || isEdited(o) || isDeleted(o));
    }

    default <R> T getDuplicate(Function<T, R> value) {
        if(value == null) {
            return null;
        }
        Set<R> valuesSet = new HashSet<>();
        return stream().filter(item -> {
            R v = value.apply(item);
            return v != null && !valuesSet.add(v);
        }).findAny().orElse(null);
    }
}