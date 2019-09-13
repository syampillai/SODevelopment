package com.storedobject.ui;

import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.vaadin.AbstractDataEditor;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;

import java.util.function.BiFunction;

public class ObjectEditor<T extends StoredObject> extends AbstractDataEditor<T> implements Transactional, ObjectSetter, ObjectChangedListener<T>, ObjectEditorListener {

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass, int actions, String title, boolean skipTools) {
        return null;
    }

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

    public boolean linkFieldEdited() {
        return true;
    }

    public boolean isEditing() {
        return false;
    }

    public void doSave() {
    }

    protected boolean save() throws Exception {
        return true;
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
    public void inserted(ObjectMasterData<T> object) {
    }

    /**
     * This method is invoked whenever a new object is updated in the database.
     *
     * @param object Object being updated
     */
    @Override
    public void updated(ObjectMasterData<T> object) {
    }

    /**
     * This method is invoked whenever a new object is deleted in the database.
     *
     * @param object Object being deleted
     */
    @Override
    public void deleted(ObjectMasterData<T> object) {
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
    public void setObject(StoredObject object) {
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

    public void setSaver(BiFunction<ObjectMasterData<T>, TransactionManager, Boolean> saver) {
    }

    public void setDeleter(BiFunction<ObjectMasterData<T>, TransactionManager, Boolean> deleter) {
    }

    public ObjectMasterData<T> getObjectData() {
        return null;
    }

    public final boolean isViewOnly() {
        return false;
    }

    public void setLinkEditing(boolean on) {
    }

    public boolean isLinkEditing() {
        return false;
    }

    protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
        return null;
    }

    @Override
    protected void attachField(String fieldName, HasValue<?, ?> field) {
    }

    protected void attachLinkField(ObjectLinkField<?> field) {
    }

    public ObjectLinkField<?> getLinkField(String fieldName) {
        return null;
    }

    public AttachmentField getAttachmentField(String fieldName) {
        return null;
    }
}