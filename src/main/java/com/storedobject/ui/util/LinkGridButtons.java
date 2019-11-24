package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.LinkGrid;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.View;

public final class LinkGridButtons<T extends StoredObject> extends ButtonLayout {

    public LinkGridButtons(LinkGrid<T> linkGrid) {
    }

    public void changed() {
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

    public void setObjectEditor(ObjectEditor<T> editor) {
    }

    public ObjectEditor<T> getObjectEditor() {
        return null;
    }

    public void setMasterView(View masterView) {
    }

    public View getMasterView() {
        return null;
    }
}
