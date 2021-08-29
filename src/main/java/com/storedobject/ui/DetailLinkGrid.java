package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.AcceptAbandonButtons;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

public class DetailLinkGrid<T extends StoredObject> extends EditableObjectGrid<T> implements LinkGrid<T> {

    private final ObjectLinkField<T> linkField;
    private final StoredObjectUtility.Link<T> link;
    private final LinkGridButtons<T> buttonPanel;
    private StoredObject master;
    private final AcceptAbandonButtons acceptAbandonButtons;

    public DetailLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public DetailLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(linkField.getObjectClass(), columns == null ? linkField.getLink().getBrowseColumns() : columns, linkField.isAllowAny());
        this.linkField = linkField;
        link = linkField.getLink();
        if(!link.isDetail()) {
            throw new SORuntimeException(link.getName() + " is not a Detail Link");
        }
        buttonPanel = new LinkGridButtons<>(this);
        acceptAbandonButtons = new AcceptAbandonButtons(this::saveEdited, this::cancelEdit);
        addValueChangeTracker((e, fromClient) -> buttonPanel.changed());
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(buttonPanel);
    }

    @Override
    int getMarkerColumnWidth() {
        return isReadOnly() ? super.getMarkerColumnWidth() : 98;
    }

    @Override
    boolean isColumnEditableInternal(String columnName) {
        return "*".equals(columnName) || isColumnEditable(columnName);
    }

    @Override
    protected HasValue<?, ?> getColumnField(String columnName) {
        if("*".equals(columnName)) {
            return acceptAbandonButtons;
        }
        return super.getColumnField(columnName);
    }

    @Override
    public LinkGridButtons<T> getButtonPanel() {
        return buttonPanel;
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getObjectClass();
    }

    @Override
    public StoredObjectUtility.Link<T> getLink() {
        return link;
    }

    @Override
    public boolean isAllowAny() {
        return super.isAllowAny();
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
        cancelEdit();
        update(object);
    }

    @Override
    public void added(T object) {
        cancelEdit();
        add(object);
    }

    @Override
    public void deleted(T object) {
        cancelEdit();
        delete(object);
    }

    @Override
    public void reloaded(T object) {
        reload(object);
    }

    @Override
    public void add() {
        if(!buttonPanel.isAllowAdd()) {
            return;
        }
        getObjectEditor().addObject(buttonPanel.getMasterView());
    }

    @Override
    public void edit() {
        if(!buttonPanel.isAllowEdit()) {
            return;
        }
        T object = selected();
        if(object == null) {
            return;
        }
        if(canEdit(object)) {
            getObjectEditor().editObject(object, buttonPanel.getMasterView());
        }
    }

    @Override
    public void delete() {
        if(!isAllowDelete()) {
            return;
        }
        cancelEdit();
        T object = selected();
        if(object == null) {
            return;
        }
        if(canDelete(object)) {
            delete(object);
        }
    }

    @Override
    public boolean canDelete(T item) {
        return canChange(item, EditorAction.DELETE);
    }

    @Override
    public void reload() {
        T item = selected();
        if(item != null && canReload(item)) {
            reload(item);
        }
    }

    @Override
    public void loaded() {
        super.loaded();
    }

    private boolean canReload(T item) {
        @SuppressWarnings("unchecked") T o = (T) StoredObject.get(item.getClass(), item.getId());
        return canChange(o, EditorAction.RELOAD);
    }

    @Override
    public final void setObjectEditor(ObjectEditor<T> editor) {
        buttonPanel.setObjectEditor(editor);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        buttonPanel.changed();
    }

    @Override
    public StoredObject getMaster() {
        return master;
    }

    @Override
    public void setMaster(StoredObject master, boolean load) {
        this.master = master;
        if(load) {
            loadMaster();
        }
    }

    @Override
    public boolean isColumnEditable(String columnName) {
        if(!linkField.isColumnEditable(columnName)) {
            return false;
        }
        return super.isColumnEditable(columnName);
    }

    @Override
    public void clear() {
        cancelEdit();
        LinkGrid.super.clear();
        resetProvider();
    }

    @Override
    public T getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public void view() {
        cancelEdit();
        LinkGrid.super.view();
    }

    @Override
    public ObjectLinkField<T> getField() {
        return linkField;
    }

    @Override
    public final ObjectEditor<T> createObjectEditor() {
        return LinkGrid.super.createObjectEditor();
    }

    @Override
    public ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    @Override
    public void cancelEdit() {
        Editor<?> e = getEditor();
        if(e.isOpen()) {
            e.cancel();
        }
        editingItem = null;
    }

    @Override
    public T selected() {
        T selected = getSelected();
        if(selected == null && getEditor().isOpen()) {
            selected = getEditingItem();
            getEditor().cancel();
            select(selected);
            return selected;
        }
        return LinkGrid.super.selected();
    }

    @Override
    public boolean isInvalid() {
        Editor<?> e = getEditor();
        if(e.isOpen()) {
            saveEdited();
            return e.isOpen();
        }
        return false;
    }
}