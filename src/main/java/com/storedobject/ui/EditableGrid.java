package com.storedobject.ui;

import com.storedobject.vaadin.HasColumns;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.data.provider.DataProvider;

import java.lang.reflect.Method;
import java.util.stream.Stream;

@HtmlImport("so-editable-grid-styles.html")
public class EditableGrid<T> extends GridPro<T> implements HasColumns<T>, EditableList<T> {

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
    }

    @Override
    public final void setDataProvider(DataProvider<T, ?> dataProvider) {
    }

    public EditableList<T> getEditableList() {
        return null;
    }

    /**
     * For internal use only.
     *
     * @return The embedded SO Grid.
     */
    @Override
    public final SOGrid<T> getSOGrid() {
        return null;
    }

    protected String getEditorGetMethodName(@SuppressWarnings("unused") String columnName) {
        return null;
    }

    protected Method getEditorGetMethod(String columnName) {
        return null;
    }

    protected String getEditorSetMethodName(@SuppressWarnings("unused") String columnName) {
        return null;
    }

    protected Method getEditorSetMethod(@SuppressWarnings("unused") String columnName) {
        return null;
    }

    protected AbstractField<?, ?> getColumnField(@SuppressWarnings("unused") String columName) {
        return null;
    }

    @SuppressWarnings("unused")
    public void setEditedValue(String columName, T item, Object columnValue) {
    }

    public boolean isColumnEditable(String columnName) {
        return true;
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
        return getEditableList().size();
    }

    @Override
    public boolean append(T item) {
        return getEditableList().append(item);
    }

    @Override
    public boolean add(T item) {
        return getEditableList().add(item);
    }

    @Override
    public boolean delete(T item) {
        deselect(item);
        return getEditableList().delete(item);
    }

    @Override
    public boolean undelete(T item) {
        return getEditableList().undelete(item);
    }

    @Override
    public boolean update(T item) {
        return getEditableList().update(item);
    }
}