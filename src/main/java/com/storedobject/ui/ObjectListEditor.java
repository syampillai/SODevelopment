package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.Transaction;
import com.storedobject.vaadin.ButtonLayout;

import java.util.function.Function;

/**
 * This class can be used to edit a list of {@link StoredObject}s.
 *
 * @param <T> Type of objects to edit.
 * @author Syam
 */
public class ObjectListEditor<T extends StoredObject> extends EditableObjectGrid<T> implements Transactional {

    protected final ButtonLayout buttonPanel = new ButtonLayout();

    public ObjectListEditor(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ObjectListEditor(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns == null ? StoredObjectUtility.browseColumns(objectClass) : columns, false);
    }

    public void add() {
    }

    public void edit() {
    }

    public void delete() {
    }

    public void reload() {
    }

    public ObjectEditor<T> getObjectViewer() {
        return null;
    }

    public T selected() {
        return getSelected();
    }

    public boolean isInvalid() {
        return selected() == null;
    }

    public final boolean isAllowAdd() {
        return selected() == null;
    }

    public void setAllowAdd(boolean allowAdd) {
    }

    public final boolean isAllowEdit() {
        return selected() == null;
    }

    public void setAllowEdit(boolean allowEdit) {
    }

    public final boolean isAllowDelete() {
        return selected() == null;
    }

    public void setAllowDelete(boolean allowDelete) {
    }

    public final boolean isAllowReload() {
        return selected() == null;
    }

    public void setAllowReload(boolean allowReload) {
    }

    public final boolean isAllowReloadAll() {
        return selected() == null;
    }

    public void setAllowReloadAll(boolean allowReloadAll) {
    }

    public final boolean isAllowView() {
        return selected() == null;
    }

    public void setAllowView(boolean allowView) {
    }

    public boolean isAllowSaveAll() {
        return selected() == null;
    }

    public void setAllowSaveAll(boolean allowSaveAll) {
    }

    public void view() {
    }

    public void validateData() throws Exception {
    }

    public void save(Transaction transaction) throws Exception {
    }

    public void save() {
    }

    public void setSaver(Function<EditableList<T>, Boolean> saver) {
    }
}