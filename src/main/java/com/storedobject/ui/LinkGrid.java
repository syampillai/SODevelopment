package com.storedobject.ui;

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
    }

    default void setValue(StoredObjectLink<T> value) {
    }

    default StoredObjectLink<T> getOldValue() {
        return null;
    }

    @Override
    default int getType() {
        return 0;
    }

    @Override
    default String getName() {
        return null;
    }

    default T selected() {
        return null;
    }

    default boolean isInvalid() {
        return false;
    }

    default void setObjectEditor(ObjectEditor<T> editor) {
    }

    default ObjectEditor<T> getObjectEditor() {
        return null;
    }

    default ObjectEditor<T> createObjectEditor() {
        return null;
    }

    default ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    default boolean isAllowAdd() {
        return false;
    }

    default void setAllowAdd(boolean allowAdd) {
    }

    default boolean isAllowEdit() {
        return false;
    }

    default void setAllowEdit(boolean allowEdit) {
    }

    default boolean isAllowDelete() {
        return false;
    }

    default void setAllowDelete(boolean allowDelete) {
    }

    default boolean isAllowReload() {
        return false;
    }

    default void setAllowReload(boolean allowReload) {
    }

    default boolean isAllowReloadAll() {
        return false;
    }

    default void setAllowReloadAll(boolean allowReloadAll) {
    }

    default void setMasterView(View masterView) {
    }

    default void view() {
    }

    @Override
    default boolean isAdded(T item) {
        return false;
    }

    @Override
    default boolean isDeleted(T item) {
        return false;
    }

    @Override
    default boolean isEdited(T item) {
        return false;
    }

    @Override
    default Stream<T> streamAll() {
        return null;
    }

    @Override
    default int size() {
        return 0;
    }

    @Override
    default boolean add(T item) {
        return false;
    }

    @Override
    default boolean delete(T item) {
        return false;
    }

    @Override
    default boolean undelete(T item) {
        return false;
    }

    @Override
    default boolean update(T item) {
        return false;
    }

    default void clear() {
    }

    default void saveEdited() {
    }

    default void cancelEditing() {
    }
}