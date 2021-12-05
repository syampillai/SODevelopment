package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.EditableObjectGrid;
import com.storedobject.ui.LinkGrid;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.editor.Editor;

public final class LinkGridButtons<T extends StoredObject> extends ButtonLayout {

    private final Button add, edit, delete, reload, reloadAll, view;
    private boolean allowAdd = true, allowEdit = true, allowDelete = true, allowReload = true, allowReloadAll = true, rowEditing = false;
    private View masterView;
    private final LinkGrid<T> link;
    private ObjectEditor<T> editor;

    public LinkGridButtons(LinkGrid<T> link) {
        this.link = link;
        add = new Button("Add", e -> link.add()).asSmall();
        edit = new Button("Edit", e -> link.edit()).asSmall();
        delete = new Button("Delete", e -> link.delete()).asSmall();
        reload = new Button(link.isDetail() ? "Undo" : "Undelete", e -> this.link.reload()).asSmall();
        reloadAll = new Button("Undo All", e -> this.link.reloadAll()).asSmall();
        view = new Button("View", e -> link.view()).asSmall();
        add(add, edit, delete, view, reload, reloadAll);
        add.setVisible(false);
        edit.setVisible(false);
        delete.setVisible(false);
        view.setVisible(false);
        reload.setVisible(false);
        reloadAll.setVisible(false);
        if(this.link instanceof EditableObjectGrid) {
            //noinspection unchecked
            Editor<T> e = ((EditableObjectGrid<T>) this.link).getEditor();
            e.addOpenListener(l -> setRowEditing(true));
            e.addCloseListener(l -> setRowEditing(false));
        }
    }

    private void setRowEditing(boolean rowEditing) {
        if(this.rowEditing == rowEditing) {
            return;
        }
        this.rowEditing = rowEditing;
        changed();
    }

    public void changed() {
        boolean anyRows = link.size() > 0;
        view.setVisible(anyRows);
        boolean masterEdit = masterView instanceof ObjectEditor;
        if(masterEdit) {
            ObjectEditor<?> oe = (ObjectEditor<?>) masterView;
            masterEdit = oe.isDoNotSaveAllowed() && oe.getObject() != null && !oe.isReadOnly() &&
                    enabled() && !readOnly();
        }
        boolean v = masterEdit && isAllowAdd();
        add.setVisible(v);
        v = masterEdit && anyRows && link.isDetail() && isAllowEdit();
        edit.setVisible(v);
        v = masterEdit && anyRows && isAllowDelete();
        delete.setVisible(v);
        if(rowEditing) {
            reload.setVisible(true);
            reloadAll.setVisible(true);
        } else {
            v = masterEdit && anyRows && (add.isVisible() || edit.isVisible() || delete.isVisible());
            reload.setVisible(v && isAllowReload());
            reloadAll.setVisible(v && isAllowReloadAll());
        }
    }

    public boolean isAllowAdd() {
        return allowAdd;
    }

    public void setAllowAdd(boolean allowAdd) {
        this.allowAdd = allowAdd;
        changed();
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
        changed();
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
        changed();
    }

    public boolean isAllowReload() {
        return allowReload;
    }

    public void setAllowReload(boolean allowReload) {
        this.allowReload = allowReload;
        changed();
    }

    public boolean isAllowReloadAll() {
        return allowReloadAll;
    }

    public void setAllowReloadAll(boolean allowReloadAll) {
        this.allowReloadAll = allowReloadAll;
        changed();
    }

    private boolean readOnly() {
        return link.isReadOnly();
    }

    private boolean enabled() {
        return link.isEnabled();
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
        ObjectEditor<T> ed = this.editor;
        if(ed != null) {
            if(ed.executing()) {
                ed.abort();
            }
        }
        this.editor = editor;
        editor.setDoNotSave(true);
        changed();
    }

    public ObjectEditor<T> getObjectEditor() {
        if(editor != null) {
            editor.setDoNotSave(true);
        }
        return editor;
    }

    public void setMasterView(View masterView) {
        this.masterView = masterView;
    }

    public View getMasterView() {
        return masterView;
    }
}
