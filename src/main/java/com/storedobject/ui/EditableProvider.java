package com.storedobject.ui;

import com.vaadin.flow.shared.Registration;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface EditableProvider<T> {
    boolean append(T item, boolean refresh);
    boolean add(T item, boolean refresh);
    boolean update(T item, boolean refresh);
    boolean delete(T item, boolean refresh);
    boolean undelete(T item, boolean refresh);
    int reload(T item, boolean refresh);
    Registration addValueChangeTracker(BiConsumer<AbstractListProvider<T>, Boolean> tracker);
    void savedAll();
    boolean isAdded(T item);
    boolean isEdited(T item);
    boolean isDeleted(T item);
    Stream<T> streamAll();
    Stream<T> streamAdded();
    Stream<T> streamEdited();
    Stream<T> streamDeleted();
    void clear();

    default boolean isSavePending() {
        return streamAll().anyMatch(o -> isAdded(o) || isEdited(o) || isDeleted(o));
    }
}
