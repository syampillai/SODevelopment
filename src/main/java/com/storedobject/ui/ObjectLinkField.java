package com.storedobject.ui;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectsSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Iterator;
import java.util.stream.Stream;

public final class ObjectLinkField<T extends StoredObject> extends CustomField<ObjectLinkData<T>> implements ObjectsSetter {

    public ObjectLinkField(String label, StoredObjectUtility.Link<T> link) {
        super(null);
    }

    public StoredObjectUtility.Link<T> getLink() {
        return null;
    }

    public Class<? extends StoredObject> getMasterClass() {
        return null;
    }

    public String getFieldName() {
        return null;
    }

    @Override
    protected ObjectLinkData<T> generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(ObjectLinkData<T> value) {
    }

    public ObjectLinkData<T> getOldValue() {
        return null;
    }

    public boolean isEdited() {
        return false;
    }

    public void edited(T object) {
    }

    public void added(T object) {
    }

    public void deleted(T object) {
    }

    public void reloaded(T object) {
    }

    public boolean isEdited(T object) {
        return false;
    }

    public boolean isAdded(T object) {
        return false;
    }

    public boolean isDeleted(T object) {
        return false;
    }

    public final boolean isAllowDirectAdd() {
        return false;
    }

    public void setAllowDirectAdd(boolean allowDirectAdd) {
    }

    public final boolean isAllowDirectEdit() {
        return false;
    }

    public void setAllowDirectEdit(boolean allowDirectEdit) {
    }

    public final boolean isAllowDirectDelete() {
        return false;
    }

    public void setAllowDirectDelete(boolean allowDirectDelete) {
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
    public void setEnabled(boolean enabled) {
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
    public void setValue(ObjectLinkData<T> value) {
    }

    @Override
    public ObjectLinkData<T> getValue() {
        return null;
    }

    @Override
    public void setVisible(boolean visible) {
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

    public static class Tab extends Tabs {

        public Tab() {
        }

        public void addField(ObjectLinkField<?> field) {
        }
    }
}
