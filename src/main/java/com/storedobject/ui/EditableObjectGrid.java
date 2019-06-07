package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.ui.util.ObjectGridData;
import com.storedobject.vaadin.ItemSelectedListener;

import java.util.List;
import java.util.function.BiConsumer;

public class EditableObjectGrid<T extends StoredObject> extends EditableGrid<T> implements ObjectGridData<T> {

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

    public void addValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
    }

    public void removeValueChangeTracker(BiConsumer<EditableObjectList<T>, Boolean> tracker) {
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    @Override
    public void setKeepCache(boolean keepCache) {
    }

    @Override
    public void setOrderBy(String orderBy) {
    }

    @Override
    public String getOrderBy() {
        return null;
    }

    @Override
    public void setLoadCondition(String loadCondition) {
    }

    @Override
    public String getLoadCondition() {
        return null;
    }

    @Override
    public void setObjectSetter(ObjectSetter setter) {
    }

    @Override
    public List<ItemSelectedListener<T>> getItemSelectedListeners(boolean create) {
        return null;
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
}