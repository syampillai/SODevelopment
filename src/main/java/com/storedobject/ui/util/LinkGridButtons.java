package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.core.Transaction;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.View;

public final class LinkGridButtons<T extends StoredObject> extends ButtonLayout {

    private final Button add, edit, delete, reload, reloadAll, view, save, cancel;
    private boolean allowAdd = true, allowEdit = true, allowDelete = true, allowDirectAdd = true,
            allowDirectEdit = true, allowDirectDelete = true, allowReload = true, allowReloadAll = true;
    private View masterView;
    private final LinkGrid<T> grid;
    private ObjectEditor<T> editor;

    private ObjectChangedListener<T> objectChanges = new ObjectChangedListener<T>() {

        @Override
        public void inserted(ObjectMasterData<T> object) {
            grid.added(object.getObject());
        }

        @Override
        public void updated(ObjectMasterData<T> object) {
            grid.edited(object.getObject());
        }

        @Override
        public void deleted(ObjectMasterData<T> object) {
            grid.deleted(object.getObject());
        }
    };

    public LinkGridButtons(LinkGrid<T> linkGrid) {
        this.grid = linkGrid;
        save = new Button("Save", e-> saveEdited()).asSmall();
        cancel = new Button("Cancel", e -> linkGrid.reloadAll()).asSmall();
        add = new Button("Add", e -> linkGrid.add()).asSmall();
        edit = new Button("Edit", e -> linkGrid.edit()).asSmall();
        delete = new Button("Delete", e -> linkGrid.delete()).asSmall();
        reload = new Button(linkGrid.isDetail() ? "Undo" : "Undelete", e -> linkGrid.reload()).asSmall();
        reloadAll = new Button("Undo All", e -> linkGrid.reloadAll()).asSmall();
        view = new Button("View", e -> linkGrid.view()).asSmall();
        add(save, cancel, add, edit, delete, view, reload, reloadAll);
        save.setVisible(false);
        cancel.setVisible(false);
        add.setVisible(false);
        edit.setVisible(false);
        delete.setVisible(false);
        view.setVisible(false);
        reload.setVisible(false);
        reloadAll.setVisible(false);
    }

    private void saveEdited() {
        if(((Transactional)grid).transact(grid::save)) {
            grid.reloadAll();
        }
    }

    public void changed() {
        boolean anyRows = grid.size() > 0;
        view.setVisible(anyRows);
        boolean canMasterEdit = masterView instanceof ObjectEditor &&
                ((ObjectEditor<?>) masterView).getObject() != null && ((ObjectEditor<?>)masterView).canEdit();
        save.setVisible(grid.isEdited());
        cancel.setVisible(save.isVisible());
        add.setVisible((!readOnly() || (canMasterEdit && isAllowDirectAdd())) && enabled() && isAllowAdd());
        edit.setVisible(anyRows && (!readOnly() || (canMasterEdit && isAllowDirectEdit())) && enabled() && isAllowEdit() && grid.isDetail());
        delete.setVisible(anyRows && (!readOnly() || (canMasterEdit && isAllowDirectDelete())) && enabled() && isAllowDelete());
        if(save.isVisible()) {
            reload.setVisible(true);
            reloadAll.setVisible(true);
        } else {
            reload.setVisible(isAllowReload() && anyRows && !readOnly() && enabled() && (add.isVisible() || edit.isVisible() || delete.isVisible()));
            reloadAll.setVisible(isAllowReloadAll() && anyRows && !readOnly() && enabled() && (add.isVisible() || edit.isVisible() || delete.isVisible()));
        }
        if(delete.isVisible()) {
            if(readOnly()) {
                delete.getElement().getStyle().set("background", "var(--lumo-error-color-10pct)");
            } else {
                delete.getElement().getStyle().set("background", add.getElement().getStyle().get("background"));
            }
        }
    }

    public final boolean isAllowDirectAdd() {
        return allowDirectAdd && editor != null && !editor.isViewOnly();
    }

    public void setAllowDirectAdd(boolean allowDirectAdd) {
        this.allowDirectAdd = allowDirectAdd;
        changed();
    }

    public final boolean isAllowDirectEdit() {
        return allowDirectEdit && editor != null && !editor.isViewOnly();
    }

    public void setAllowDirectEdit(boolean allowDirectEdit) {
        this.allowDirectEdit = allowDirectEdit;
        changed();
    }

    public final boolean isAllowDirectDelete() {
        return allowDirectDelete && editor != null && !editor.isViewOnly();
    }

    public void setAllowDirectDelete(boolean allowDirectDelete) {
        this.allowDirectDelete = allowDirectDelete;
        changed();
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
        if(this.editor != null && this.editor.executing()) {
            this.editor.abort();
            this.editor.removeObjectChangedListener(objectChanges);
        }
        this.editor = editor;
        editor.removeObjectChangedListener(objectChanges);
        editor.addObjectChangedListener(objectChanges);
        editor.setLinkEditing(true);
        changed();
    }

    public ObjectEditor<T> getObjectEditor() {
        if(editor != null) {
            editor.setLinkEditing(true);
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
