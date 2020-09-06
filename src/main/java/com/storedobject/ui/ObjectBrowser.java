package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;

import java.util.List;
import java.util.stream.Stream;

import static com.storedobject.core.EditorAction.ALL;
import static com.storedobject.core.EditorAction.ALLOW_ANY;

public class ObjectBrowser<T extends StoredObject> extends ObjectGrid<T> implements EditableDataGrid {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, search, filter, load, view, report, excel, audit, exit, save, cancel;

    public ObjectBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    public ObjectBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, (String)null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, actions, caption, dataProvider);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, dataProvider);
    }

    ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, (actions & ALLOW_ANY) == ALLOW_ANY);
    }

    @SuppressWarnings("unchecked")
    public ObjectBrowser(String className) throws Exception {
        this((Class<T>)JavaClassLoader.getLogic(className), null, 0,
                null,null, ObjectEditor.allowedActions(className));
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, null, actions, title);
    }
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns, int actions, String title) {
        return new ObjectBrowser<>(objectClass);
    }

    public ObjectSearchBuilder<T> createSearchBuilder(StringList searchColumns) {
        return null;
    }

    protected boolean isActionAllowed(String action) {
        return false;
    }

    protected void removeAllowedAction(String action) {
    }

    int filterActionsInternal(int actions) {
        return filterActions(actions);
    }

    protected int filterActions(int actions) {
        return actions;
    }

    protected void createExtraButtons() {
    }

    protected void addExtraButtons() {
    }

    protected boolean canDelete(T object) {
        return true;
    }

    protected boolean canEdit(T object) {
        return true;
    }

    protected boolean canAdd() {
        return true;
    }

    protected boolean canSearch() {
        return true;
    }

    @Override
    public List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        return null;
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
    }

    public final ObjectEditor<T> getObjectEditor() {
        return null;
    }

    public final ObjectEditor<T> getRowEditor() {
        return null;
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    @Override
    public boolean isSearchMode() {
        return false;
    }

    @Override
    public ObjectSearchBuilder<T> getSearchBuilder() {
        return null;
    }

    public void setReadOnly(boolean readOnly) {
    }

    public final boolean isReadOnly() {
        return false;
    }

    @Override
    protected final Editor<T> createEditor() {
        return null;
    }

    public void editRow(T item) {
    }

    public void cancelRowEdit() {
    }

    public void saveEditedRow() {
    }

    public final T getEditingItem() {
        return null;
    }

    @Override
    public boolean isColumnEditable(String columnName) {
        return false;
    }

    @Override
    public Stream<HasValue<?, ?>> streamEditableFields() {
        return null;
    }
}
