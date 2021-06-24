package com.storedobject.ui.util;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.EditableObjectGrid;
import com.storedobject.ui.LinkGrid;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.editor.Editor;

public final class LinkGridButtons<T extends StoredObject> extends ButtonLayout {

    private final Button add, edit, delete, reload, reloadAll, view;
    private boolean allowAdd = true, allowEdit = true, allowDelete = true, allowReload = true, allowReloadAll = true, rowEditing = false;
    private View masterView;
    private final LinkGrid<T> grid;
    private ObjectEditor<T> editor;

    private final ObjectChangedListener<T> objectChanges = new ObjectChangedListener<>() {

        @Override
        public void inserted(T object) {
            if(grid.canChange(object, EditorAction.NEW)) {
                grid.added(object);
            }
        }

        @Override
        public void updated(T object) {
            if(grid.canChange(object, EditorAction.EDIT)) {
                grid.edited(object);
            }
        }

        @Override
        public void deleted(T object) {
            if(grid.canChange(object, EditorAction.DELETE)) {
                grid.deleted(object);
            }
        }

        @Override
        public void undeleted(T object) {
            if(grid.canChange(object, EditorAction.RELOAD)) {
                grid.reloaded(object);
            }
        }
    };

    public LinkGridButtons(LinkGrid<T> linkGrid) {
        this.grid = linkGrid;
        add = new Button("Add", e -> linkGrid.add()).asSmall();
        edit = new Button("Edit", e -> linkGrid.edit()).asSmall();
        delete = new Button("Delete", e -> linkGrid.delete()).asSmall();
        reload = new Button(linkGrid.isDetail() ? "Undo" : "Undelete", e -> grid.reload()).asSmall();
        reloadAll = new Button("Undo All", e -> grid.reloadAll()).asSmall();
        view = new Button("View", e -> linkGrid.view()).asSmall();
        add(add, edit, delete, view, reload, reloadAll);
        add.setVisible(false);
        edit.setVisible(false);
        delete.setVisible(false);
        view.setVisible(false);
        reload.setVisible(false);
        reloadAll.setVisible(false);
        if(grid instanceof EditableObjectGrid) {
            //noinspection unchecked
            Editor<T> e = ((EditableObjectGrid<T>) grid).getEditor();
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
        boolean anyRows = grid.size() > 0;
        view.setVisible(anyRows);
        boolean masterEdit = masterView instanceof ObjectEditor;
        if(masterEdit) {
            ObjectEditor<?> oe = (ObjectEditor<?>) masterView;
            masterEdit = oe.isDoNotSaveAllowed() && oe.getObject() != null && !oe.isReadOnly() &&
                    enabled() && !readOnly();
        }
        boolean v = masterEdit && isAllowAdd();
        add.setVisible(v);
        v = masterEdit && anyRows && grid.isDetail() && isAllowEdit();
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

    public final boolean isAllowAdd() {
        return allowAdd;
    }

    public void setAllowAdd(boolean allowAdd) {
        this.allowAdd = allowAdd;
        changed();
    }

    public final boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
        changed();
    }

    public final boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
        changed();
    }

    public final boolean isAllowReload() {
        return allowReload;
    }

    public void setAllowReload(boolean allowReload) {
        this.allowReload = allowReload;
        changed();
    }

    public final boolean isAllowReloadAll() {
        return allowReloadAll;
    }

    public void setAllowReloadAll(boolean allowReloadAll) {
        this.allowReloadAll = allowReloadAll;
        changed();
    }

    private boolean readOnly() {
        return grid.isReadOnly();
    }

    private boolean enabled() {
        return grid.isEnabled();
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
        ObjectEditor<T> ed = this.editor;
        if(ed != null) {
            if(ed.executing()) {
                ed.abort();
            }
            ed.removeObjectChangedListener(objectChanges);
        }
        this.editor = editor;
        editor.removeObjectChangedListener(objectChanges);
        editor.addObjectChangedListener(objectChanges);
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
