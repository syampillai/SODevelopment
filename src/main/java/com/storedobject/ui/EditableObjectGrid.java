package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.ui.util.ObjectGridData;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
/**
 * An editable grid of objects. It internally maintains an {@link EditableList} that provides status information on all each row
 * of the grid. (See {@link #getEditableList()}).
 *
 * @param <T>
 * @author Syam
 */
public class EditableObjectGrid<T extends StoredObject> extends AbstractEditableGrid<T> implements ObjectGridData<T>, EditableDataGrid {

    T editingItem;

    public EditableObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public EditableObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, new EditableObjectList<>(objectClass, any));
    }

    public EditableObjectGrid(Class<T> objectClass, EditableList<T> dataProvider) {
        this(objectClass, null, dataProvider);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns, EditableList<T> dataProvider) {
        super(objectClass, columns);
    }

    @Override
    public ObjectDataProvider<T, Void> getDataProvider() {
        //noinspection unchecked
        return (ObjectDataProvider<T, Void>) super.getDataProvider();
    }

    @Override
    public EditableObjectList<T> getEditableList() {
        return (EditableObjectList<T>) super.getEditableList();
    }

    public Registration addValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
        return getEditableList().addValueChangeTracker(tracker);
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return new ArrayList<>();
    }

    @Override
    public void setOrderBy(String orderBy) {
    }

    @Override
    public String getOrderBy() {
        return "";
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
    }

    @Override
    public List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        return new ArrayList<>();
    }

    public void reload(T object) {
    }

    public void reloadAll() {
    }

    public void setFromClient(boolean fromClient) {
    }

    @Override
    public boolean isColumnEditable(String columnName) {
        return !"*".equals(columnName);
    }

    protected HasValue<?, ?> getColumnField(String columnName) {
        return null;
    }

    @Override
    public final Stream<HasValue<?, ?>> streamEditableFields() {
        return Stream.of();
    }

    public final ObjectEditor<T> getRowEditor() {
        return ObjectEditor.create(getObjectClass());
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    public final ObjectEditor<T> getObjectEditor() {
        return getRowEditor();
    }

    protected void customizeObjectEditor() {
    }

    public void setReadOnly(boolean readOnly) {
    }

    public final boolean isReadOnly() {
        return getObjectEditor().isReadOnly();
    }

    public boolean editItem(T item) {
        return false;
    }

    public void cancelEdit() {
    }

    public void cancelRowEdit() {
    }

    public void saveEdited() {
    }

    public final T getEditingItem() {
        return editingItem;
    }
}