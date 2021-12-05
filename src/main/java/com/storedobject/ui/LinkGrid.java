package com.storedobject.ui;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;

public interface LinkGrid<T extends StoredObject> {

    Class<T> getObjectClass();

    LinkValue<T> getLinkGrid();

    void itemAppended(T object);

    void itemInserted(T object);

    void itemUpdated(T object);

    void itemDeleted(T object);

    void itemUndeleted(T object);

    void itemReloaded(T object);

    default void setValue(StoredObjectLink<T> value) {
        if(value == getLinkGrid()) {
            getButtonPanel().changed();
            return;
        }
        setMaster(value.getMaster(), false);
        clear();
        value.streamAll().forEach(o -> {
            if(value.isAdded(o)) {
                itemInserted(o);
            } else if(value.isEdited(o)) {
                itemUpdated(o);
            } else if(value.isDeleted(o)) {
                itemDeleted(o);
            } else {
                itemAppended(o);
            }
        });
        getButtonPanel().changed();
    }

    T getSelected();

    void add();

    void edit();

    void delete();

    void reload();

    void reloadAll();

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);

    boolean isEnabled();

    boolean isDetail();

    void setMaster(StoredObject master, boolean load);

    LinkGridButtons<T> getButtonPanel();

    StoredObjectUtility.Link<T> getLink();

    void clear();

    default StoredObjectLink<T> getOldValue() {
        return StoredObjectLink.create(getLink(), getMaster());
    }

    StoredObject getMaster();

    int getType();

    String getName();

    T getItem(int index);

    void select(T item);

    default boolean isInvalid() {
        return false;
    }

    default void setObjectEditor(ObjectEditor<T> editor) {
        getButtonPanel().setObjectEditor(editor);
    }

    default ObjectEditor<T> getObjectEditor() {
        return createObjectEditor();
    }

    default ObjectEditor<T> createObjectEditor() {
        ObjectEditor<T> editor = getButtonPanel().getObjectEditor();
        if(editor == null) {
            editor = constructObjectEditor();
            if(editor == null) {
                editor = ObjectEditor.create(getObjectClass());
            }
            View v = getButtonPanel().getMasterView();
            if(v instanceof TransactionCreator) {
                editor.setTransactionCreator((TransactionCreator) v);
            }
            getButtonPanel().setObjectEditor(editor);
        }
        return editor;
    }

    default ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    default boolean isAllowAdd() {
        return getButtonPanel().isAllowAdd();
    }

    default void setAllowAdd(boolean allowAdd) {
        getButtonPanel().setAllowAdd(allowAdd);
    }

    default boolean isAllowEdit() {
        return getButtonPanel().isAllowEdit();
    }

    default void setAllowEdit(boolean allowEdit) {
        getButtonPanel().setAllowAdd(allowEdit);
    }

    default boolean isAllowDelete() {
        return getButtonPanel().isAllowDelete();
    }

    default void setAllowDelete(boolean allowDelete) {
        getButtonPanel().setAllowDelete(allowDelete);
    }

    default boolean isAllowReload() {
        return getButtonPanel().isAllowReload();
    }

    default void setAllowReload(boolean allowReload) {
        getButtonPanel().setAllowReload(allowReload);
    }

    default boolean isAllowReloadAll() {
        return getButtonPanel().isAllowReloadAll();
    }

    default void setAllowReloadAll(boolean allowReloadAll) {
        getButtonPanel().setAllowReloadAll(allowReloadAll);
    }

    default void setMasterView(View masterView) {
        getButtonPanel().setMasterView(masterView);
    }

    T selected();

    default void view() {
        T object = selected();
        if(object == null) {
            return;
        }
        getObjectEditor().viewObject(object, getButtonPanel().getMasterView());
    }

    ObjectLinkField<T> getField();

    int size();
}