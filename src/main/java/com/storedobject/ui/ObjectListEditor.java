package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.EditableList;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.Transaction;
import com.storedobject.ui.util.AcceptAbandonButtons;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

import java.util.function.Function;

/**
 * This class can be used to edit a list of {@link StoredObject}s. This may be used directly as a
 * {@link com.storedobject.vaadin.View} (since it is a {@link com.storedobject.vaadin.DataGrid}) or
 * it can be embedded in other layouts.
 *
 * @param <T> Type of objects to edit.
 * @author Syam
 */
public class ObjectListEditor<T extends StoredObject> extends EditableObjectGrid<T> {

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
     * "Reload" button.
     */
    protected final Button reload;
    /**
     * "Reload All" button.
     */
    protected final Button reloadAll;
    /**
     * "View" button.
     */
    protected final Button view;
    /**
     * "Save All" button.
     */
    protected final Button saveAll;
    private boolean allowView = true, allowAdd = true, allowEdit = true, allowDelete = true,
            allowReload = true, allowReloadAll = true, rowEditing = false, allowSaveAll = true;
    private ObjectEditor<T> viewer;
    private final AcceptAbandonButtons acceptAbandonButtons;
    private Function<EditableList<T>, Boolean> saver;

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     */
    public ObjectListEditor(Class<T> objectClass) {
        this(objectClass, null);
    }


    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     * @param columns Columns to edit.
     */
    public ObjectListEditor(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of objects.
     * @param columns Columns to edit.
     */
    public ObjectListEditor(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, columns == null ? StoredObjectUtility.browseColumns(objectClass) : columns, any);
        buttonPanel = new ButtonLayout();
        saveAll = new Button("Save Changes", "save", e -> save()).asSmall().asPrimary();
        add = new Button("Add", e -> addIf()).asSmall();
        edit = new Button("Edit", e -> editIf()).asSmall();
        delete = new Button("Delete", e -> deleteIf()).asSmall();
        reload = new Button("Undo", e -> reloadIf()).asSmall();
        reloadAll = new Button("Undo All", e -> reloadAllIf()).asSmall();
        view = new Button("View", e -> viewIf()).asSmall();
        buttonPanel.add(saveAll, add, edit, delete, view, reload, reloadAll);
        Editor<T> e = getEditor();
        e.addOpenListener(l -> setRowEditing(true));
        e.addCloseListener(l -> setRowEditing(false));
        changedInt();
        acceptAbandonButtons = new AcceptAbandonButtons(this::saveEdited, this::cancelEdit);
        addValueChangeTracker((editableList, fromClient) -> changed());
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
     * Hide the Save/Cancel buttons when row editing is active.
     */
    public void hideRowEditorButtons() {
        acceptAbandonButtons.hideButtons();
    }

    private boolean releaseEd() {
        if(getEditingItem() == getSelected()) {
            return true;
        }
        return !stillEditing();
    }

    /**
     * Add a new instance via the UI.
     */
    public void add() {
        cancelEdit();
        getObjectEditor().addObject(getView());
    }

    private void addIf() {
        if(releaseEd() && isAllowAdd()) {
            add();
        }
    }

    /**
     * Edit a selected instance via the UI.
     */
    public void edit() {
        cancelEdit();
        T object = selected();
        if(object == null) {
            return;
        }
        if(canEdit(object)) {
            getObjectEditor().editObject(object, getView());
        }
    }

    private void editIf() {
        if(releaseEd() && isAllowEdit()) {
            edit();
        }
    }

    /**
     * Delete a selected instance.
     */
    public void delete() {
        cancelEdit();
        T object = selected();
        if(object == null) {
            return;
        }
        if(canDelete(object)) {
            itemDeleted(object);
        }
    }

    private void deleteIf() {
        if(releaseEd() && isAllowDelete()) {
            delete();
        }
    }

    /**
     * Reload a selected instance. This will abandon all the changes already made to the instance. If the instance
     * was a newly added one, it will be removed.
     */
    public void reload() {
        cancelEdit();
        T selected = selected();
        if(selected == null) {
            return;
        }
        if(isAdded(selected)) {
            delete();
            return;
        }
        reload(selected);
    }

    private void reloadIf() {
        if(releaseEd() && isAllowReload()) {
            reload();
        }
    }

    private void reloadAllIf() {
        if(releaseEd() && isAllowReloadAll()) {
            reloadAll();
        }
    }

    private void viewIf() {
        if(!stillEditing() && isAllowView()) {
            view();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        changedInt();
    }

    @Override
    public void clear() {
        cancelEdit();
        super.clear();
    }

    /**
     * Get the viewer for this grid. If <code>null</code> is returned by this method a viewer will be
     * automatically crated by creating an instance of the {@link ObjectEditor}.
     *
     * @return Default implementation returns <code>null</code>.
     */
    public ObjectEditor<T> getObjectViewer() {
        return null;
    }

    private ObjectEditor<T> viewer() {
        if(viewer == null) {
            viewer = getObjectViewer();
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(getObjectClass());
        }
        return viewer;
    }

    @Override
    public T selected() {
        return selected(getEditor());
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
        changedInt();
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
        changedInt();
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
        changedInt();
    }

    /**
     * Can reload/restore instances?
     *
     * @return True/false.
     */
    public final boolean isAllowReload() {
        return allowReload;
    }

    /**
     * Enable/disable reloading/restoring of instances. (This will not affect the programmatic invocation of
     * the method {@link #reload()}).
     *
     * @param allowReload True/false.
     */
    public void setAllowReload(boolean allowReload) {
        this.allowReload = allowReload;
        changedInt();
    }

    /**
     * Can reload/restore all instances?
     *
     * @return True/false.
     */
    public final boolean isAllowReloadAll() {
        return allowReloadAll;
    }

    /**
     * Enable/disable reloading/restoring of all instances. (This will not affect the programmatic invocation of
     * the method {@link #reloadAll()}).
     *
     * @param allowReloadAll True/false.
     */
    public void setAllowReloadAll(boolean allowReloadAll) {
        this.allowReloadAll = allowReloadAll;
        changedInt();
    }

    /**
     * Can view instances?
     *
     * @return True/false.
     */
    public final boolean isAllowView() {
        return allowView;
    }

    /**
     * Enable/disable viewing of instances. (This will not affect the programmatic invocation of the
     * method {@link #view()}).
     *
     * @param allowView True/false.
     */
    public void setAllowView(boolean allowView) {
        this.allowView = allowView;
        changedInt();
    }

    /**
     * Can save all changes?
     *
     * @return True/false.
     */
    public boolean isAllowSaveAll() {
        return allowSaveAll;
    }

    /**
     * Enable/disable saving of all changes made. (This will not affect the programmatic invocation of
     * the methods {@link #save()} and {@link #save(Transaction)}).
     *
     * @param allowSaveAll True/false.
     */
    public void setAllowSaveAll(boolean allowSaveAll) {
        this.allowSaveAll = allowSaveAll;
        changedInt();
    }

    @Override
    void changed() {
        super.changed();
        changedInt();
    }

    /**
     * This method should be overridden to control the visibility of various buttons. Visibility of the following
     * buttons can be set: {@link #add}, {@link #edit}, {@link #delete}, {@link #reload}, {@link #reloadAll},
     * {@link #view}, {@link #saveAll}.
     */
    public void setButtonVisibility() {
    }

    private void changedInt() {
        if(view == null) {
            return;
        }
        view.setVisible(allowView);
        boolean v = !isReadOnly();
        add.setVisible(v && allowAdd);
        edit.setVisible(v && allowEdit);
        delete.setVisible(v && allowDelete);
        v = getEditableList().isSavePending();
        reload.setVisible(v && allowReload);
        reloadAll.setVisible(v && allowReloadAll);
        saveAll.setVisible(v && allowSaveAll);
        setButtonVisibility();
        view.setVisible(view.isVisible() && allowView);
        v = !isReadOnly();
        add.setVisible(add.isVisible() && v && allowAdd);
        edit.setVisible(edit.isVisible() && v && allowEdit);
        delete.setVisible(delete.isVisible() && v && allowDelete);
        v = getEditableList().isSavePending();
        reload.setVisible(reload.isVisible() && v && allowReload);
        reloadAll.setVisible(reloadAll.isVisible() && v && allowReloadAll);
        saveAll.setVisible(saveAll.isVisible() && v && allowSaveAll);
    }

    private void setRowEditing(boolean rowEditing) {
        if(this.rowEditing == rowEditing) {
            return;
        }
        this.rowEditing = rowEditing;
        changedInt();
    }

    /**
     * Customize the editor that will be used for editing the instances. You can get the editor
     * by invoking the method {@link #getObjectEditor()}. However, you have to make sure to call the super
     * method if you override this method.
     */
    @Override
    protected void customizeObjectEditor() {
        ObjectEditor<T> editor = getObjectEditor();
        editor.setIncludeFieldChecker(fieldName -> !fieldName.endsWith(".l") && !fieldName.endsWith(".a")
                && !fieldName.endsWith(".c"));
    }

    @Override
    void setUpEditor(ObjectEditor<T> editor) {
        super.setUpEditor(editor);
        editor.setDoNotSave(true);
    }

    /**
     * Set an external editor to edit objects of this list editor.
     *
     * @param editor Editor to set.
     */
    public void setExternalEditor(ObjectEditor<T> editor) {
        setUpEditor(editor);
    }

    /**
     * View the currently selected instance.
     */
    public void view() {
        cancelEdit();
        T selected = selected();
        if(selected != null) {
            viewer().viewObject(selected);
        }
    }

    /**
     * This method is invoked by the {@link #save()} method and if any exception is thrown, it will not carry out
     * the real "save action". Exceptions will be displayed as warning messages on the UI. This method is
     * typically overridden to raise user-friendly messages when data is not valid. (Please note that this method
     * will not be invoked by the {@link #save(Transaction)} method).
     *
     * @throws Exception If any the data is not valid.
     */
    @SuppressWarnings("RedundantThrows")
    public void validateData() throws Exception {
    }

    /**
     * Save the currently accumulated changes to the database. (Please note that this method will not
     * use any "saver" set by the {@link #setSaver(Function)} method. In fact, a "saver" may call this method
     * to carry out the real "save action" if required.)
     *
     * @param transaction Transaction.
     * @throws Exception Will be thrown if the save is not successful.
     */
    public void save(Transaction transaction) throws Exception {
        if(isReadOnly()) {
            return;
        }
        if(stillEditing()) {
            return;
        }
        saveEdited();
        aboutToSave(transaction);
        if(saver == null) {
            transaction.addCommitListener(t -> savedAll());
        }
        try {
            streamAll().forEach(o -> {
                    try {
                        boolean added;
                        if((added = isAdded(o)) || isEdited(o)) {
                            if(!skipSave(transaction, o)) {
                                if(added) {
                                    if(o.isVirtual()) {
                                        o.makeNew();
                                    }
                                }
                                o.save(transaction);
                                saved(transaction, o);
                            }
                        } else if(isDeleted(o)) {
                            if(!skipDelete(transaction, o)) {
                                o.delete(transaction);
                                deleted(transaction, o);
                            }
                        }
                    } catch(Exception e) {
                        throw new SORuntimeException(e);
                    }
            });
            reload.setVisible(false);
            reloadAll.setVisible(false);
        } catch(SORuntimeException re) {
            throw (Exception)re.getCause();
        }
    }

    /**
     * This method is invoked from {@link #save(Transaction)} when changes are about to be saved to the database.
     * You can override this method to save any related objects that are required before saving the edited items.
     *
     * @param transaction Transaction.
     * @throws Exception if any error occurs and the save should be aborted.
     */
    @SuppressWarnings("RedundantThrows")
    protected void aboutToSave(Transaction transaction) throws Exception {
    }

    /**
     * This method is invoked from {@link #save(Transaction)} when the item is about to save to the database. If
     * this method returns <code>false</code>, the item is not saved.
     *
     * @param transaction Transaction.
     * @param object Item being saved. (It may be still virtual).
     * @return True/false.
     * @throws Exception if any error occurs and the save should be aborted.
     */
    @SuppressWarnings("RedundantThrows")
    protected boolean skipSave(Transaction transaction, T object) throws Exception {
        return false;
    }

    /**
     * This method is invoked from {@link #save(Transaction)} when the item is about to delete in the database. If
     * this method returns <code>false</code>, the item is not deleted.
     *
     * @param transaction Transaction.
     * @param object Item being saved.
     * @return True/false.
     * @throws Exception if any error occurs and the save should be aborted.
     */
    @SuppressWarnings("RedundantThrows")
    protected boolean skipDelete(Transaction transaction, T object) throws Exception {
        return false;
    }

    /**
     * This is invoked when an instance is saved. You can carry out further save-related actions if any.
     *
     * @param object Instance that is saved.
     * @throws Exception If an exception is thrown, transaction will be aborted.
     */
    protected void saved(Transaction transaction, T object) throws Exception {
    }

    /**
     * This is invoked when an instance is deleted while saving. You can carry out further save-related actions if any.
     *
     * @param object Instance that is deleted.
     * @throws Exception If an exception is thrown, transaction will be aborted.
     */
    protected void deleted(Transaction transaction, T object) throws Exception {
    }

    private void savedAll() {
        //noinspection unchecked
        ((EditableProvider<T>)getDataProvider()).savedAll();
        setItems(getDataProvider());
        saveCompleted();
    }

    /**
     * This method is invoked after the changes are completely saved to the database.
     */
    protected void saveCompleted() {
    }

    /**
     * Save the currently accumulated changes to the database. Errors if any will be displayed in the UI.
     * <p>If a saver is set via {@link #setSaver(Function)}, that saver will be used for saving the changes.
     * Otherwise, a {@link Transaction} is created and {@link #save(Transaction)} is invoked with
     * that transaction to save the changes.</p>
     */
    public void save() {
        if(isReadOnly()) {
            return;
        }
        if(stillEditing()) {
            return;
        }
        saveEdited();
        try {
            validateData();
        } catch(Exception e) {
            warning(e);
            return;
        }
        if(saver == null) {
            transact(this::save);
            return;
        }
        boolean saved = saver.apply(this.getEditableList());
        if(saved) {
            reload.setVisible(false);
            reloadAll.setVisible(false);
            savedAll();
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

    @Override
    protected void insertedNow(T object) {
        reloadAll.setVisible(allowReloadAll);
    }

    @Override
    protected void updatedNow(T object) {
        reload.setVisible(allowReload);
        reloadAll.setVisible(allowReloadAll);
    }

    @Override
    protected void deletedNow(T object) {
        changed();
    }

    @Override
    protected void reloadedAllNow() {
        changed();
    }
}