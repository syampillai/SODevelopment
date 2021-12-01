package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
/**
 * An editable grid of objects. It internally maintains an {@link EditableList} that provides status information on each row
 * of the grid. (See {@link #getEditableList()}).
 *
 * @param <T> Type of object to edit.
 * @author Syam
 */
public class EditableObjectGrid<T extends StoredObject> extends AbstractEditableGrid<T>
        implements ObjectGridData<T, T>, EditableDataGrid<T> {

    private String orderBy;
    private List<ObjectChangedListener<T>> objectChangedListeners;
    private List<ObjectEditorListener> objectEditorListeners;
    private ObjectEditor<T> editor;
    T editingItem;
    private boolean autoSave = false;
    private boolean rowMode = false;
    private boolean readOnly = false;
    private final Map<String, HasValue<?, ?>> fields = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Span> spans = new HashMap<>();

    public EditableObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public EditableObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public EditableObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, new ObjectMemoryList<>(objectClass, any),
                columns == null ? StoredObjectUtility.browseColumns(objectClass) : columns);
        addConstructedListener(g ->
                getRowEditor().addConstructedListener(e -> getEditor().setBinder(editor.getForm().getBinder())));
    }

    @Override
    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof ObjectListProvider && super.isValid(dataProvider);
    }

    @Override
    protected EditableObjectListProvider<T> createListDataProvider(DataList<T> data) {
        return new EditableObjectListProvider<>(getObjectClass(), data);
    }

    @Override
    public EditableObjectListProvider<T> getEditableList() {
        return (EditableObjectListProvider<T>) super.getEditableList();
    }

    public Registration addValueChangeTracker(BiConsumer<EditableObjectListProvider<T>, Boolean> tracker) {
        return getEditableList().addValueChangeTracker(tracker);
    }

    @Override
    public ObjectListProvider<T> getObjectLoader() {
        return (ObjectListProvider<T>) super.getDataProvider();
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        if(objectChangedListeners == null && create) {
            objectChangedListeners = new ArrayList<>();
        }
        return objectChangedListeners;
    }

    @Override
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
    }

    @Override
    public List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        if(objectEditorListeners == null && create) {
            objectEditorListeners = new ArrayList<>();
        }
        return objectEditorListeners;
    }

    /**
     * Set the auto-save mode. If this mode is set to <code>true</code>, values will be saved from the field
     * when jumping between rows. By default, it is off and field values will not be saved when jumping between rows.
     *
     * @param autoSave True/false.
     */
    public void setAutoSaveOnMove(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public void reload(T object) {
        cancelEdit();
        getEditableList().reload(object);
        fireChanged(object, EditorAction.RELOAD);
    }

    public void reloadAll() {
        cancelEdit();
        getEditableList().reload();
        fireChanged(null, EditorAction.ALL);
    }

    public void setFromClient(boolean fromClient) {
        getEditableList().setFromClient(fromClient);
    }

    /**
     * All fields are editable by default unless a field for editing the value of a particular column
     * can not be determined from its {@link ObjectEditor}.
     *
     * @param columnName Column name.
     * @return True/false.
     */
    @Override
    public boolean isColumnEditable(String columnName) {
        return !"*".equals(columnName);
    }

    /**
     * Check if a field is editable or not. (For internal purpose only).
     *
     * @param columnName Column name.
     * @return True/false.
     */
    boolean isColumnEditableInternal(String columnName) {
        return isColumnEditable(columnName);
    }

    /**
     * Get the field for the column. (Used while editing the row). In general, it is not required to override this.
     * If this method returns <code>null</code>, field will be created from the respective Object Editor.
     *
     * @param columnName Column name for which field needs to be obtained.
     * @return Default implementation returns <code>null</code>.
     */
    protected HasValue<?, ?> getColumnField(String columnName) {
        return null;
    }

    @Override
    public final HasValue<?, ?> getField(String columnName) {
        return fields.get(columnName);
    }

    @Override
    public final Stream<HasValue<?, ?>> streamEditableFields() {
        return fields.values().stream();
    }

    private void constructEditor() {
        editor = createObjectEditor();
        if(editor == null) {
            editor = constructObjectEditor();
        }
        if(editor == null) {
            editor = ObjectEditor.create(getObjectClass());
        }
        customizeObjectEditor();
        editor.getComponent();
        String fieldName;
        HasValue<?, ?> field;
        int p;
        for(Column<?> c: getColumns()) {
            fieldName = c.getKey();
            p = fieldName.indexOf(' ');
            if(p > 0) {
                fieldName = fieldName.substring(0, p);
            }
            if(isColumnEditableInternal(fieldName)) {
                field = getColumnField(fieldName);
                if(field == null) {
                    field = editor.getField(fieldName);
                }
                if(field != null) {
                    labels.put(fieldName, editor.getFieldLabel(fieldName));
                    fields.put(fieldName, field);
                    Span span = new Span();
                    spans.put(fieldName, span);
                    c.setEditorComponent(span);
                }
            }
        }
        editor.grid = this;
        editor.addValidator(this::validate);
        editor.setBuffered(true);
    }

    public final ObjectEditor<T> getRowEditor() {
        if(editor == null) {
            constructEditor();
        }
        if(!rowMode) {
            fields.keySet().forEach(fieldName -> {
                HasValue<?, ?> field = fields.get(fieldName);
                editor.setFieldLabel(field, null);
                Span span = spans.get(fieldName);
                span.add((Component) field);
                rowMode = true;
            });
        }
        return editor;
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    public final ObjectEditor<T> getObjectEditor() {
        if(editor == null) {
            constructEditor();
        }
        if(!rowMode) {
            return editor;
        }
        if(getEditor().isOpen()) {
            cancelEdit();
        }
        String fieldName;
        HasValue<?, ?> field;
        for(ObjectEditor.FieldPosition p: editor.fieldPositions()) {
            fieldName = p.name();
            field = fields.get(fieldName);
            if(field != null) {
                spans.get(fieldName).removeAll();
                editor.setFieldLabel(field, labels.get(fieldName));
                p.container().addComponentAtIndex(p.position(), (Component)field);
            }
        }
        rowMode = false;
        return editor;
    }

    /**
     * Customize the editor that will be used for editing the instances. You can get the editor
     * by invoking the method {@link #getObjectEditor()}.
     */
    protected void customizeObjectEditor() {
    }

    /**
     * Set read only.
     *
     * @param readOnly If true, rows will not be editable.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            cancelEdit();
        }
        resizeMarkerColumn();
    }

    @Override
    int getMarkerColumnWidth() {
        return readOnly ? 5 : super.getMarkerColumnWidth();
    }

    public final boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Edit a given item.
     *
     * @param item Item to edit.
     * @return True if editing is started, otherwise false.
     */
    @Override
    public boolean editItem(T item) {
        if(!readOnly && editingItem != item) {
            if(stillEditing()) {
                return false;
            }
            if(getRowEditor().editItem(item)) {
                editingItem = item;
                return true;
            }
        }
        return false;
    }

    boolean stillEditing() {
        return !autoSave && getEditor().isOpen();
    }

    public void cancelEdit() {
        T item = editingItem;
        Editor<?> e = getEditor();
        if(e.isOpen()) {
            e.cancel();
        }
        if(editor != null) {
            getRowEditor().doCancel();
        }
        editingItem = null;
        if(item != null) {
            select(item);
        }
    }

    public void saveEdited() {
        T item = editingItem;
        if(editor != null && item != null && getRowEditor().saveEdited()) {
            editingItem = null;
            getEditableList().fireChanges();
            select(item);
        }
    }

    public final T getEditingItem() {
        return editingItem;
    }

    /**
     * This method is invoked to validate the item being added/edited via the UI.
     *
     * @param item Item being added/edited.
     * @throws Exception Throw exceptions if the item is not valid.
     */
    public void validateData(T item) throws Exception {
    }

    private boolean validate(T item) {
        try {
            validateData(item);
            return true;
        } catch(Exception e) {
            warning(e);
        }
        return false;
    }

    @Override
    public void clear() {
        ObjectGridData.super.clear();
        super.clear();
    }
}