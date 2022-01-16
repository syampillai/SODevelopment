package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.AcceptAbandonButtons;
import com.storedobject.ui.util.LinkGridButtons;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

public class DetailLinkGrid<T extends StoredObject> extends AbstractLinkGrid<T> {

    private final LinkGridButtons<T> buttonPanel;
    private final AcceptAbandonButtons acceptAbandonButtons;

    public DetailLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public DetailLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(linkField, columns == null ? linkField.getLink().getBrowseColumns() : columns, linkField.isAllowAny());
        if(!link.isDetail()) {
            throw new SORuntimeException(link.getName() + " is not a Detail Link");
        }
        setFilter((String) null, false);
        buttonPanel = new LinkGridButtons<>(this);
        acceptAbandonButtons = new AcceptAbandonButtons(this::saveEdited, this::cancelEdit);
        addValueChangeTracker((e, fromClient) -> buttonPanel.changed());
        addDataLoadedListener(buttonPanel::changed);
        setLinkType(link.getType(), false);
        itemAppended(null);
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
    public boolean canDelete(T item) {
        return canChange(item, EditorAction.DELETE);
    }

    @Override
    public boolean add(T item) {
        if(item != null) {
            if(Id.isNull(item.getId())) {
                itemInserted(item);
                return true;
            }
            return super.add(item);
        }
        return true;
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
            return DetailLinkGrid.this.getMaster();
        }
    }
}