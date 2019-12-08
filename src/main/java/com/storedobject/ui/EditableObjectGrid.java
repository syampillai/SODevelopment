package com.storedobject.ui;

import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.ui.util.ObjectGridData;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EditableObjectGrid<T extends StoredObject> extends EditableGrid<T> implements ObjectGridData<T>, EditableDataGrid {

    ObjectSetter objectSetter;

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

    public EditableObjectGrid(Class<T> objectClass, ObjectDataProvider<T> dataProvider) {
        this(objectClass, null, dataProvider);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns, ObjectDataProvider<T> dataProvider) {
        super(objectClass, columns);
    }

    @Override
    public ObjectDataProvider<T> getDataProvider() {
        return null;
    }

    @Override
    public EditableObjectList<T> getEditableList() {
        return null;
    }

    public Registration addValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
        return null;
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    @Override
    public void setOrderBy(String orderBy) {
    }

    @Override
    public String getOrderBy() {
        return null;
    }

    @Override
    public void setObjectSetter(ObjectSetter setter) {
    }

    @Override
    public List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        return null;
    }

    public void reload(T object) {
    }

    public void reloadAll() {
    }

    public void setFromClient(boolean fromClient) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    public final ObjectEditor<T> getRowEditor() {
        return null;
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    public final ObjectEditor<T> getObjectEditor() {
        return null;
    }

    public void setReadOnly(boolean readOnly) {
    }

    public final boolean isReadOnly() {
        return false;
    }

    public boolean editItem(T item) {
        return false;
    }

    public void cancelEdit() {
    }

    public void saveEdited() {
    }

    public final T getEditingItem() {
        return null;
    }

    @Override
    public Stream<HasValue<?, ?>> streamEditableFields() {
        return null;
    }
}