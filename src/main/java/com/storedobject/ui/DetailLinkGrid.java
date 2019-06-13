package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;

public class DetailLinkGrid<T extends StoredObject> extends EditableObjectGrid<T> implements LinkGrid<T> {

    public DetailLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public DetailLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(linkField.getObjectClass(), columns == null ? linkField.getLink().getBrowseColumns() : columns, linkField.isAllowAny());
    }

    @Override
    public LinkGridButtons<T> getButtonPanel() {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public StoredObjectUtility.Link<T> getLink() {
        return null;
    }

    @Override
    public T getSelected() {
        return super.getSelected();
    }

    @Override
    public final boolean isDetail() {
        return true;
    }

    @Override
    public View createView() {
        return null;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public View getView(boolean create) {
        return null;
    }

    @Override
    public void edited(T object) {
    }

    @Override
    public void added(T object) {
    }

    @Override
    public void deleted(T object) {
    }

    @Override
    public void reloaded(T object) {
        reload(object);
    }

    @Override
    public void add() {
    }

    @Override
    public void edit() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void reload() {
        reload(selected());
    }

    @Override
    public boolean isEdited() {
        return isReadOnly() && getEditableList().isChanged();
    }

    @Override
    public final void setObjectEditor(ObjectEditor<T> editor) {
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public final boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public StoredObject getMaster() {
        return null;
    }

    @Override
    public void setMaster(StoredObject master, boolean load) {
    }

    @Override
    public void clear() {
    }
}