package com.storedobject.ui;

import com.storedobject.core.EditableList;

import java.util.stream.Stream;

public abstract class AbstractEditableGrid<T> extends DataGrid<T> implements EditableList<T> {

    public AbstractEditableGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public AbstractEditableGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    public AbstractEditableGrid(Class<T> objectClass, Iterable<String> columns, EditableList<T> dataProvider) {
        super(objectClass, columns);
    }

    public EditableList<T> getEditableList() {
        //noinspection unchecked
        return (EditableList<T>) getDataProvider();
    }

    @Override
    public boolean contains(T item) {
        return getEditableList().contains(item);
    }

    @Override
    public boolean isAdded(T item) {
        return getEditableList().isAdded(item);
    }

    @Override
    public boolean isDeleted(T item) {
        return getEditableList().isDeleted(item);
    }

    @Override
    public boolean isEdited(T item) {
        return getEditableList().isEdited(item);
    }

    @Override
    public Stream<T> streamAll() {
        return getEditableList().streamAll();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean append(T item) {
        return false;
    }

    @Override
    public boolean add(T item) {
        return false;
    }

    @Override
    public boolean delete(T item) {
        return false;
    }

    @Override
    public boolean undelete(T item) {
        return false;
    }

    @Override
    public boolean update(T item) {
        return false;
    }

    protected abstract void cancelEdit();
}