package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;
import com.storedobject.core.StoredObjectUtility;

import java.util.stream.Stream;

public interface LinkValue<T extends StoredObject> extends StoredObjectLink<T> {

    StoredObjectUtility.Link<T> getLink();

    @Override
    default int getType() {
        return getLink().getType();
    }

    @Override
    default String getName() {
        return getLink().getName();
    }

    EditableProvider<T> getEditableList();

    @Override
    default boolean isAdded(T item) {
        return getEditableList().isAdded(item);
    }

    @Override
    default boolean isDeleted(T item) {
        return getEditableList().isDeleted(item);
    }

    @Override
    default boolean isEdited(T item) {
        return getEditableList().isEdited(item);
    }

    @Override
    default Stream<T> streamAll() {
        return getEditableList().streamAll();
    }

    @Override
    default boolean add(T item) {
        return getEditableList().add(item, false);
    }

    @Override
    default boolean delete(T item) {
        return getEditableList().delete(item, false);
    }

    @Override
    default boolean undelete(T item) {
        return getEditableList().undelete(item, false);
    }

    @Override
    default boolean update(T item) {
        return getEditableList().update(item, false);
    }

    void clear();

    ObjectLinkField<T> getField();
}