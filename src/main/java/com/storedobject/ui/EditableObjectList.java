package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;

import java.util.function.BiConsumer;

public class EditableObjectList<T extends StoredObject> extends ObjectList<T> implements EditableList<T> {

    public EditableObjectList(Class<T> objectClass) {
        this(objectClass, false);
    }

    public EditableObjectList(Class<T> objectClass, boolean allowAny) {
        super(objectClass, allowAny);
    }

    public boolean isChanged() {
        return false;
    }

    @Override
    public Id getId(T item) {
        return null;
    }

    public void setFromClient(boolean fromClient) {
    }

    public boolean isFromClient() {
        return false;
    }

    public void addValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
    }

    public void removeValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean append(T item) {
        return false;
    }

    @Override
    public boolean add(int index, T item) {
        return true;
    }

    @Override
    public boolean delete(T item) {
        return true;
    }

    @Override
    public boolean delete(int index) {
        return delete(getItem(index));
    }

    @Override
    public boolean undelete(T item) {
        return true;
    }

    public boolean undelete(int index) {
        return undelete(getItem(index));
    }

    @Override
    public boolean update(T item) {
        return true;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
    }

    @Override
    public void reload(T item) {
    }

    @Override
    public void reloadAll() {
    }

    @Override
    public boolean isAdded(T item) {
        return false;
    }

    @Override
    public boolean isDeleted(T item) {
        return false;
    }

    @Override
    public boolean isEdited(T item) {
        return false;
    }

    public EditableList<T> getValue() {
        return this;
    }

    public EditableList<T> getOldValue() {
        return null;
    }
}
