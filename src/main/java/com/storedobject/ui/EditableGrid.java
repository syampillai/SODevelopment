package com.storedobject.ui;

import com.storedobject.core.EditableList;

import java.util.stream.Stream;

/**
 * An editable grid. It internally maintains an {@link EditableList} that provides status information on all each row
 * of the grid. (See {@link #getEditableList()}).
 *
 * @param <T>
 * @author Syam
 */
public abstract class EditableGrid<T> extends DataGrid<T> implements EditableList<T> {

    /**
     * Constructor that will generate columns from the Bean's properties.
     *
     * @param objectClass Bean type
     */
    public EditableGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    /**
     * Constructor that will generate columns from the column names passed.
     *
     * @param objectClass Bean type
     * @param columns Column names
     */
    public EditableGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    /**
     * Get the "editable list" from this grid.
     *
     * @return The embedded "editable list".
     */
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

    /**
     * Cancel the editing if it is active. Sub-classes should implement this.
     */
    protected void cancelEdit() {
    }
}