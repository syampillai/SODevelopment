package com.storedobject.ui;

import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.ui.util.AttachmentField;
import com.storedobject.vaadin.AbstractDataEditor;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;

import java.util.function.Function;

public class ObjectEditor<T extends StoredObject> extends AbstractDataEditor<T> implements Transactional, ObjectSetter<T>,
        ObjectChangedListener<T>, ObjectEditorListener, ObjectProvider<T>, AlertHandler, TransactionCreator {

    protected HasComponents buttonPanel;

    public ObjectEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    public ObjectEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, actions, caption, null);
    }

    @SuppressWarnings("unchecked")
    public ObjectEditor(String className) throws Exception {
        this((Class<T>)JavaClassLoader.getLogic(className), 0, null, allowedActions(className));
    }

    ObjectEditor(Class<T> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, caption);
    }

    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass) {
        return null;
    }

    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass, int actions) {
        return null;
    }

    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass, int actions, String title) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return null;
    }

    protected int filterActions(int actions) {
        return actions;
    }

    protected boolean isActionAllowed(String action) {
        return true;
    }

    protected void removeAllowedAction(String action) {
    }

    protected static String allowedActions(String className) {
        return null;
    }

    @Override
    public boolean skipFirstFocus(Focusable<?> skipFocus) {
        return false;
    }

    public void setSetNotAllowed(String fieldName) {
    }

    public void setNewObjectGenerator(NewObject<T> newObject) {
    }

    public void addObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public void addObjectEditorListener(ObjectEditorListener listener) {
    }

    public void removeObjectEditorListener(ObjectEditorListener listener) {
    }

    protected void createExtraButtons() {
    }

    protected void addExtraButtons() {
    }

    protected void addExtraEditingButtons() {
    }

    public boolean isEditing() {
        return false;
    }

    public void doSave() {
    }

    protected boolean save() throws Exception {
        return true;
    }

    protected void save(Transaction t) throws Exception {
    }

    public void doCancel() {
    }

    public void validateData() throws Exception {
    }

    public void validateAnchorValues(T object) throws SOException {
    }

    public void doAdd() {
    }

    public void doEdit() {
    }

    public void doDelete() {
    }

    public void doSearch() {
    }

    public void doReport() {
    }

    public void doAudit() {
    }

    public ObjectSearcher<T> getSearcher() {
        return null;
    }

    protected boolean delete() throws Exception {
        return false;
    }

    @Override
    public void editingStarted() {
    }

    @Override
    public void editingEnded() {
    }

    @Override
    public void editingCancelled() {
    }

    public boolean canDelete() {
        return false;
    }

    public boolean canEdit() {
        return false;
    }

    public boolean canAdd() {
        return false;
    }

    public boolean canSearch() {
        return false;
    }

    public void reload() {
    }

    /**
     * This method is invoked whenever a new object is inserted in the database.
     *
     * @param object Object being inserted
     */
    @Override
    public void inserted(T object) {
    }

    /**
     * This method is invoked whenever a new object is updated in the database.
     *
     * @param object Object being updated
     */
    @Override
    public void updated(T object) {
    }

    /**
     * This method is invoked whenever a new object is deleted in the database.
     *
     * @param object Object being deleted
     */
    @Override
    public void deleted(T object) {
    }

    public void viewObject() {
    }

    public void viewObject(T object) {
    }

    public void viewObject(T object, com.storedobject.vaadin.View parent) {
    }

    public void viewObject(T object, com.storedobject.vaadin.View parent, boolean doNotLock) {
    }

    public void editObject(T object) {
    }

    public void editObject(T object, com.storedobject.vaadin.View parent) {
    }

    public void editObject(T object, com.storedobject.vaadin.View parent, boolean doNotLock) {
    }

    public void addObject() {
    }

    public void addObject(com.storedobject.vaadin.View parent) {
    }

    public void addObject(com.storedobject.vaadin.View parent, boolean doNotLock) {
    }

    public void deleteObject() {
    }

    public void deleteObject(T object) {
    }

    @Override
    public void setObject(T object, boolean load) {
    }

    public void setParentObject(StoredObject parentObject, int parentLinkType) {
    }

    public final StoredObject getParentObject() {
        return null;
    }

    public final int getParentLinkType() {
        return 0;
    }

    public void setSaver(Function<ObjectEditor<T>, Boolean> saver) {
    }

    public void setDeleter(Function<ObjectEditor<T>, Boolean> deleter) {
    }

    public final boolean isViewOnly() {
        return false;
    }

    /**
     * For internal use only. If set, saving will be skipped! (It is internally used to switch on/off link
     * editing depending on whether this is a child of another editor or not).
     *
     * @param on True or false.
     */
    public final void setDoNotSave(boolean on) {
    }

    /**
     * Allow/disallow "Do not save" option. (See {@link #setDoNotSave(boolean)}.
     *
     * @param allowDoNotSave True if link editing needs to be allowed
     */
    public void setAllowDoNotSave(boolean allowDoNotSave) {
    }

    /**
     * Check whether "Do not save" option is allowed or not.
     *
     * @return True/false.
     */
    public boolean isDoNotSaveAllowed() {
        return false;
    }

    protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
        return null;
    }

    @Override
    protected void attachField(String fieldName, HasValue<?, ?> field) {
    }

    protected void customizeLinkField(ObjectLinkField<?> field) {
    }

    protected void attachLinkField(ObjectLinkField<?> field) {
    }

    public ObjectLinkField<?> getLinkField(String fieldName) {
        return null;
    }

    public AttachmentField getAttachmentField(String fieldName) {
        return null;
    }

    public HasValue<?, String> getContactField(String fieldName) {
        return null;
    }

    @Override
    public Transaction getTransaction(boolean create) {
        return null;
    }

    @Override
    public void setTransactionCreator(TransactionCreator transactionCreator) {
    }

    public void setRawObject(StoredObject object) {
    }
}