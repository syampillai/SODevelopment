package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

import java.util.Iterator;
import java.util.stream.Stream;

public final class ObjectLinkField<T extends StoredObject> implements HasValue<HasValue.ValueChangeEvent<StoredObjectLink<T>>, StoredObjectLink<T>>, ObjectsSetter {

    public ObjectLinkField(String label, StoredObjectUtility.Link<T> link) {
    }

    public StoredObjectUtility.Link<T> getLink() {
        return null;
    }

    public Class<? extends StoredObject> getMasterClass() {
        return null;
    }

    public void hideColumn(String columnName) {
    }

    public void unhideColumn(String columnName) {
    }

    public String getFieldName() {
        return null;
    }

    public StoredObjectLink<T> getOldValue() {
        return null;
    }

    public void edited(T object) {
    }

    public void added(T object) {
    }

    public void deleted(T object) {
    }

    public void reloaded(T object) {
    }

    public boolean isAdded(T object) {
        return false;
    }

    public boolean isDeleted(T object) {
        return false;
    }

    public final boolean isAllowAdd() {
        return false;
    }

    public void setAllowAdd(boolean allowAdd) {
    }

    public final boolean isAllowEdit() {
        return false;
    }

    public void setAllowEdit(boolean allowEdit) {
    }

    public final boolean isAllowDelete() {
        return false;
    }

    public void setAllowDelete(boolean allowDelete) {
    }

    public final boolean isAllowReload() {
        return false;
    }

    public void setAllowReload(boolean allowReload) {
    }

    public final boolean isAllowReloadAll() {
        return false;
    }

    public void setAllowReloadAll(boolean allowReloadAll) {
    }

    public void add() {
    }

    public void edit() {
    }

    public void delete() {
    }

    public void reload() {
    }

    public void reloadAll() {
    }

    public void view() {
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    public Stream<T> getItems() {
        return null;
    }

    public void add(T object) {
    }

    public void add(Stream<T> objects) {
    }

    public void add(Iterator<T> objects) {
    }

    public void add(ObjectIterator<T> objects) {
    }

    public void add(Iterable<T> objects) {
    }

    public void scrollTo(@SuppressWarnings("unused") T object) {
    }

    @Override
    public void setObject(StoredObject object) {
    }

    @Override
    public void setObjects(Iterable<? extends StoredObject> objects) {
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    public StoredObject getMaster() {
        return null;
    }

    public void setMaster(StoredObject master) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void setValue(StoredObjectLink<T> value) {
    }

    @Override
    public StoredObjectLink<T> getValue() {
        return null;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<StoredObjectLink<T>>> valueChangeListener) {
        return null;
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
    }

    public final ObjectEditor<T> getObjectEditor() {
        return null;
    }

    public void setMasterView(View masterView) {
    }

    public void setFromClient(boolean fromClient) {
    }
}
