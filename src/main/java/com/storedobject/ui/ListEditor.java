package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.NewObject;
import com.storedobject.ui.util.AcceptAbandonButtons;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class can be used to edit a list of any kind of Object instances. This may be used directly as a
 * {@link com.storedobject.vaadin.View} (since it is a {@link com.storedobject.vaadin.DataGrid}) or
 * it can be embedded in other layouts.
 *
 * @param <T> Type of objects to edit.
 * @author Syam
 */
public class ListEditor<T> extends EditableGrid<T> {

    /**
     * Button panel.
     */
    protected final ButtonLayout buttonPanel;
    /**
     * "Add" button.
     */
    protected final Button add;
    /**
     * "Edit" button.
     */
    protected final Button edit;
    /**
     * "Delete" button.
     */
    protected final Button delete;
    /**
     * "Save All" button.
     */
    protected final Button saveAll;
    private NewObject<T> newObject;
    private boolean allowAdd = true;
    private boolean allowEdit = true;
    private boolean allowDelete = true;
    private boolean allowSaveAll = true;
    private final AcceptAbandonButtons acceptAbandonButtons;
    private Function<EditableList<T>, Boolean> saver;

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     */
    public ListEditor(Class<T> objectClass) {
        this(objectClass, null, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     * @param columns Columns to edit.
     */
    public ListEditor(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     * @param loader Loader that can load an item again (mostly for refreshing or undoing).
     */
    public ListEditor(Class<T> objectClass, BiFunction<T, Boolean, T> loader) {
        this(objectClass, null, loader);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     * @param columns Columns to edit.
     * @param loader Loader that can load an item again (mostly for refreshing or undoing).
     */
    public ListEditor(Class<T> objectClass, Iterable<String> columns, BiFunction<T, Boolean, T> loader) {
        super(objectClass, columns, loader);
        saveAll = new Button("Save Changes", "save", e -> save()).asSmall().asPrimary();
        add = new Button("Add", e -> addIf()).asSmall();
        edit = new Button("Edit", e -> editIf()).asSmall();
        delete = new Button("Delete", e -> deleteIf()).asSmall();
        buttonPanel = new Buttons();
        acceptAbandonButtons = new AcceptAbandonButtons(this::saveEdited, this::cancelEdit);
        setWidth("100%");
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

    /**
     * Set a "new object generator" so that the {@link #add()} method can generate a customized instance when adding
     * a new instance.
     *
     * @param newObjectGenerator New object generator to set.
     */
    public void setNewObjectGenerator(NewObject<T> newObjectGenerator) {
        this.newObject = newObjectGenerator;
    }

    /**
     * Add a new instance. If no "new object generator" {@link #setNewObjectGenerator(NewObject)} is set,
     * it will try to create an instance from a default constructor if available.
     */
    public void add() {
        cancelEdit();
        try {
            T item = newObject == null ? getDataClass().getDeclaredConstructor().newInstance() : newObject.newObject();
            if(item != null) {
                add(item);
                editItem(item);
            }
        } catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            warning("Unable to create a new entry");
        } catch(Exception e) {
            warning(e);
        }
    }

    private void addIf() {
        if(isAllowAdd()) {
            add();
        }
    }

    /**
     * Edit a selected instance.
     */
    public void edit() {
        cancelEdit();
        if(!isAllowEdit()) {
            return;
        }
        T object = selected();
        if(object == null) {
            return;
        }
        if(canEdit(object)) {
            editItem(object);
        }
    }

    private void editIf() {
        if(isAllowEdit()) {
            edit();
        }
    }

    /**
     * Delete a selected instance.
     */
    public void delete() {
        cancelEdit();
        if(!isAllowDelete()) {
            return;
        }
        T object = selected();
        if(object == null) {
            return;
        }
        if(canDelete(object)) {
            itemDeleted(object);
        }
    }

    private void deleteIf() {
        if(isAllowDelete()) {
            delete();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        changed();
    }

    @Override
    public void clear() {
        cancelEdit();
        super.clear();
        changed();
    }

    /**
     * Check if this editor is in an invalid state or not. (It will be in invalid state if the current row
     * editor can not be closed because the data is invalid).
     *
     * @return True or false.
     */
    public boolean isInvalid() {
        Editor<?> e = getEditor();
        if(e.isOpen()) {
            saveEdited();
            return e.isOpen();
        }
        return false;
    }

    /**
     * Can add new instances? (This will not affect the programmatic invocation of the method {@link #add()}).
     *
     * @return True/false.
     */
    public final boolean isAllowAdd() {
        return allowAdd;
    }

    /**
     * Enable/disable adding of new instances.
     *
     * @param allowAdd True/false
     */
    public void setAllowAdd(boolean allowAdd) {
        this.allowAdd = allowAdd;
        changed();
    }

    /**
     * Can edit instances? (This will not affect the programmatic invocation of the method {@link #edit()}).
     *
     * @return True/false.
     */
    public final boolean isAllowEdit() {
        return allowEdit;
    }

    /**
     * Enable/disable editing of instances.
     *
     * @param allowEdit True/false.
     */
    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
        changed();
    }

    /**
     * Can delete instances?
     *
     * @return True/false.
     */
    public final boolean isAllowDelete() {
        return allowDelete;
    }

    /**
     * Enable/disable deleting of instances. (This will not affect the programmatic invocation of
     * the method {@link #delete()}).
     *
     * @param allowDelete True/false.
     */
    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
        changed();
    }

    /**
     * Enable/disable saving of all changes made. (This will not affect the programmatic invocation of
     * the method {@link #save()}).
     *
     * @param allowSaveAll True/false.
     */
    public void setAllowSaveAll(boolean allowSaveAll) {
        this.allowSaveAll = allowSaveAll;
        changed();
    }

    @Override
    void changed() {
        super.changed();
        boolean v = !isReadOnly();
        if(add != null) {
            add.setVisible(v && allowAdd);
        }
        if(edit != null) {
            edit.setVisible(v && allowEdit);
        }
        if(delete != null) {
            delete.setVisible(v && allowDelete);
        }
        if(saveAll != null) {
            saveAll.setVisible(allowSaveAll && getEditableList().isSavePending());
        }
    }

    /**
     * This method is invoked by the {@link #save()} method and if any exception is thrown, it will not carry out
     * the real "save action". Exceptions will be displayed as warning messages on the UI. This method is
     * typically overridden to raise user-friendly messages when data is not valid.
     *
     * @throws Exception If any the data is not valid.
     */
    @SuppressWarnings("RedundantThrows")
    public void validateData() throws Exception {
    }

    /**
     * Save the currently accumulated changes. Errors if any will be displayed in the UI.
     * <p>If a saver is set via {@link #setSaver(Function)}, that saver will be used for saving the changes.
     * Otherwise, all values are updated in the internal list.
     */
    public void save() {
        if(isReadOnly()) {
            return;
        }
        if(stillEditing()) {
            return;
        }
        try {
            validateData();
        } catch(Exception e) {
            warning(e);
            return;
        }
        if(saver == null) {
            provider().removeDeleted();
        }
        if(saver == null || saver.apply(this.getEditableList())) {
            provider().savedAll();
            changed();
        }
    }

    /**
     * Set a saver so that the {@link #save()} method can be customized.
     *
     * @param saver Set a saver that will be invoked to save the changes made.
     */
    public void setSaver(Function<EditableList<T>, Boolean> saver) {
        this.saver = saver;
    }

    private class Buttons extends ButtonLayout {

        Buttons() {
            add(saveAll, add, edit, delete);
            Editor<T> e = getEditor();
            e.addOpenListener(l -> saveAll.setVisible(false));
            e.addCloseListener(l -> changed());
            changed();
        }
    }
}