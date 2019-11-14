package com.storedobject.ui;

import com.storedobject.core.ObjectSearcher;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;

import java.util.stream.Stream;

public class ReferenceLinkGrid<T extends StoredObject> extends ObjectGrid<T> implements LinkGrid<T>, StoredObjectLink<T> {

    public ReferenceLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public ReferenceLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(linkField.getObjectClass(), columns, new EditableObjectList<>(linkField.getObjectClass(), linkField.isAllowAny()));
    }

    @Override
    public LinkGridButtons<T> getButtonPanel() {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getObjectClass();
    }

    @Override
    public StoredObjectUtility.Link<T> getLink() {
        return null;
    }

    @Override
    public T getSelected() {
        return null;
    }

    @Override
    public final boolean isDetail() {
        return false;
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    @Override
    public EditableObjectList<T> getEditableList() {
        return null;
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
    public boolean contains(T object) {
        return false;
    }

    @Override
    public void edited(T object) {
    }

    @Override
    public boolean append(T object) {
        return false;
    }

    @Override
    public void added(T object) {
    }

    @Override
    public void deleted(T object) {
    }

    @Override
    public void reloaded(T object) {
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
    }

    @Override
    public void reloadAll() {
    }

    @Override
    public final boolean isEdited() {
        return false;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public void setDirect(boolean direct) {

    }

    public ObjectSearcher<T> getSearcher() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
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
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public Stream<T> streamAll() {
        return null;
    }
}
