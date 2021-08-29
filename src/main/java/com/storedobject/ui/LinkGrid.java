package com.storedobject.ui;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.View;

import java.util.stream.Stream;

public interface LinkGrid<T extends StoredObject> extends StoredObjectLink<T> {

    Class<T> getObjectClass();

    T getSelected();

    void added(T object);

    void edited(T object);

    void deleted(T object);

    void reloaded(T object);

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

    EditableObjectList<T> getEditableList();

    LinkGridButtons<T> getButtonPanel();

    StoredObjectUtility.Link<T> getLink();

    default void loadMaster() {
        getEditableList().load(getType(), getMaster(), null, getLink().getOrderBy());
        getButtonPanel().changed();
    }

    default void setValue(StoredObjectLink<T> value) {
        if(value == this) {
            getButtonPanel().changed();
            return;
        }
        setMaster(value.getMaster(), false);
        clear();
        value.streamAll().forEach(o -> {
            if(value.isAdded(o)) {
                added(o);
            } else if(value.isEdited(o)) {
                edited(o);
            } else if(value.isDeleted(o)) {
                deleted(o);
            } else {
                append(o);
            }
        });
        getButtonPanel().changed();
    }

    default StoredObjectLink<T> getOldValue() {
        return StoredObjectLink.create(getLink(), getMaster());
    }

    @Override
    default int getType() {
        return getLink().getType();
    }

    @Override
    default String getName() {
        return getLink().getName();
    }

    default void loaded() {
    }

    default T selected() {
        if(size() == 0) {
            Application.warning("No entries exist");
            return null;
        }
        T object = getSelected();
        if(object == null) {
            if(size() == 1) {
                object = getItem(0);
                select(object);
                return object;
            }
            Application.warning("Nothing selected");
        }
        return object;
    }

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

    default void view() {
        T object = selected();
        if(object == null) {
            return;
        }
        getObjectEditor().viewObject(object, getButtonPanel().getMasterView());
    }

    @Override
    default boolean isAdded(T item) {
        return getEditableList().isAdded(item);
    }

    @Override
    default boolean isDeleted(T item) {
        return getEditableList().isDeleted(item);
    }

    @Override
    default boolean isEdited(T item) {
        return getEditableList().isEdited(item);
    }

    @Override
    default Stream<T> streamAll() {
        return getEditableList().streamAll();
    }

    @Override
    default int size() {
        return getEditableList().size();
    }

    @Override
    default boolean add(T item) {
        return getEditableList().add(item);
    }

    @Override
    default boolean delete(T item) {
        return getEditableList().delete(item);
    }

    @Override
    default boolean undelete(T item) {
        return getEditableList().undelete(item);
    }

    @Override
    default boolean update(T item) {
        return getEditableList().update(item);
    }

    default void clear() {
        getEditableList().clear();
    }

    ObjectLinkField<T> getField();

    /**
     * Invoked to check whether a change is allowed from the client side or not.
     *
     * @param item Item to change.
     * @param editorAction Editor action (One of the static values from {@link EditorAction}).
     * @return True if change is acceptable. If returned false, change will be ignored.
     */
    default boolean canChange(T item, int editorAction) {
        return ((ObjectEditor<?>)getButtonPanel().getMasterView()).acceptValueChange(getField(), item, editorAction);
    }
}