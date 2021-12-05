package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.AcceptAbandonButtons;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

public class DetailLinkGrid<T extends StoredObject> extends EditableObjectGrid<T> implements LinkGrid<T> {

    private final ObjectLinkField<T> linkField;
    private final StoredObjectUtility.Link<T> link;
    private final LinkGridButtons<T> buttonPanel;
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
        addDataLoadedListener(buttonPanel::changed);
        setLinkType(link.getType(), false);
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

    protected final EditableList<T> createEditableList() {
        return new DList();
    }

    @Override
    public T getSelected() {
        return super.getSelected();
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
    public boolean canDelete(T item) {
        return canChange(item, EditorAction.DELETE);
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
            itemDeleted(object);
        }
    }

    @Override
    public void reload() {
        T item = selected();
        if(item != null && canReload(item)) {
            cancelEdit();
            itemReloaded(item);
        }
    }

    @Override
    public void reloadAll() {
        cancelEdit();
        setMaster(getMaster(), true);
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
    public boolean isDetail() {
        return true;
    }

    @Override
    public LinkGridButtons<T> getButtonPanel() {
        return buttonPanel;
    }

    @Override
    public StoredObjectUtility.Link<T> getLink() {
        return link;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        buttonPanel.changed();
    }

    @Override
    public int getType() {
        return link.getType();
    }

    @Override
    public String getName() {
        return link.getName();
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
        super.clear();
    }

    @Override
    public T getItem(int index) {
        return super.getItem(index);
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
        return super.selected();
    }

    @Override
    public ObjectLinkField<T> getField() {
        return linkField;
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

    @Override
    public LinkValue<T> getLinkGrid() {
        return (DList)getEditableList();
    }

    @Override
    public void setMaster(StoredObject master, boolean load) {
        clear();
        if(master != null) {
            super.setMaster(master, load);
        }
    }

    @Override
    public StoredObject getMaster() {
        return super.getMaster();
    }

    @Override
    protected final boolean canChange(T item, int editorAction) {
        return ((ObjectEditor<?>)getButtonPanel().getMasterView()).acceptValueChange(getField(), item, editorAction);
    }

    class DList extends EList implements LinkValue<T> {

        @Override
        public StoredObjectUtility.Link<T> getLink() {
            return link;
        }

        @Override
        public EditableProvider<T> getEditableList() {
            //noinspection unchecked
            return (EditableProvider<T>) getDataProvider();
        }

        @Override
        public void clear() {
            DetailLinkGrid.this.clear();
        }

        @Override
        public ObjectLinkField<T> getField() {
            return linkField;
        }

        @Override
        public StoredObject getMaster() {
            return getObjectLoader().getMaster();
        }
    }
}