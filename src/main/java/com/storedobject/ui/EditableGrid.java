package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.ui.util.SOFieldCreator;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * An editable grid that can be used to edit a list of objects of any type.
 *
 * @param <T> Type of object.
 */
public class EditableGrid<T> extends AbstractEditableGrid<T> implements EditableDataGrid<T> {

    /**
     * Currently editing item.
     */
    T editingItem;
    private boolean autoSave = false;
    private final Binder<T> binder;
    private boolean readOnly = false;
    private final Map<String, HasValue<?, ?>> fields = new HashMap<>();
    private final Refresher refresher = new Refresher();

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     */
    public EditableGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param columns Columns for the grid.
     */
    public EditableGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns, new Data<>());
        binder = new Binder<>(objectClass);
        refresher.change();
        setHeight("100%");
        setWidth("100%");
        addConstructedListener(o -> con());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refresher.set();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        refresher.remove();
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

    /**
     * All fields are editable by default unless a field can't be created for the column.
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
     * If this method returns <code>null</code>, field will be created by inspecting the return value of
     * the getXXX(T) method (where XXX is the name of the field) if such a method exists. If getXXX(T)
     * method doesn't exist, it will try getXXX() method
     * in the object class. If that also doesn't exist, no field will be created. (getXXX(T)/getXXX() methods and
     * setXXX(T, value)/setXXX(value) methods are used to read from the object and write back to the object.
     * If no setXXX method exists but a getXXX method exists, the field will be read-only).
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

    /**
     * This will be invoked just before accepting the changes when editing. If any exception is thrown, editing
     * mode will not be changed and the error will be displayed as a warning message.
     *
     * @throws Exception Any exception to denote the error.
     */
    @SuppressWarnings("RedundantThrows")
    public void validateFieldValues() throws Exception {
    }

    private void con() {
        constructEditor();
        Editor<T> editor = getEditor();
        editor.setBinder(binder);
        editor.addSaveListener(e -> getEditableList().update(e.getItem()));
    }

    private void constructEditor() {
        SOFieldCreator<T> fieldCreator = new SOFieldCreator<>();
        fieldCreator = fieldCreator.create(getDataClass());
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
                Method m = getM(fieldName);
                field = getColumnField(fieldName);
                if(field == null && m != null) {
                    field = fieldCreator.createField(fieldName, m.getReturnType(), null);
                }
                if(field != null) {
                    if(m != null) {
                        binder.bind(field, funG(m), funS(fieldName, m));
                    }
                    if(!"*".equals(fieldName)) {
                        fields.put(fieldName, field);
                    }
                    c.setEditorComponent((Component)field);
                }
            }
        }
    }

    private Method getM(String columnName) {
        Method m;
        try {
            m = getClass().getMethod("get" + columnName, getDataClass());
            if(!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                return m;
            }
        } catch(NoSuchMethodException ignored) {
        }
        try {
            m = getDataClass().getMethod("get" + columnName);
            if(!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                return m;
            }
        } catch(NoSuchMethodException ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <V> ValueProvider<T, V> funG(Method getM) {
        if(getM.getDeclaringClass().isAssignableFrom(getDataClass())) {
            return t -> {
                try {
                    return (V)getM.invoke(t);
                } catch(IllegalAccessException | InvocationTargetException ignored) {
                }
                return null;
            };
        }
        return t -> {
            try {
                return (V)getM.invoke(EditableGrid.this, t);
            } catch(IllegalAccessException | InvocationTargetException ignored) {
            }
            return null;
        };
    }

    private <V> Setter<T, V> funS(String columnName, Method getM) {
        Method m;
        try {
            m = getClass().getMethod("set" + columnName, getDataClass(), getM.getReturnType());
            if(!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                Method finalM = m;
                return (obj, v) -> {
                    try {
                        finalM.invoke(EditableGrid.this, obj, v);
                    } catch(IllegalAccessException | InvocationTargetException ignored) {
                    }
                };
            }
        } catch(NoSuchMethodException ignored) {
        }
        try {
            m = getDataClass().getMethod("set" + columnName, getM.getReturnType());
            if(!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                Method finalM = m;
                return (obj, v) -> {
                    try {
                        finalM.invoke(obj, v);
                    } catch(IllegalAccessException | InvocationTargetException ignored) {
                    }
                };
            }
        } catch(NoSuchMethodException ignored) {
        }
        return null;
    }

    @Override
    int getMarkerColumnWidth() {
        return readOnly ? 5 : super.getMarkerColumnWidth();
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

    /**
     * Check the read-only status.
     *
     * @return True/false.
     */
    public final boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Clear all the entries.
     */
    public void clear() {
        ((Data<T>)getEditableList()).clear();
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
            Editor<T> e = getEditor();
            if(e.isOpen()) {
                return false;
            }
            editingItem = item;
            if(item != null) {
                deselectAll();
                e.editItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Is editing still going on?
     *
     * @return True/false.
     */
    boolean stillEditing() {
        if(autoSave) {
            saveEdited();
            return getEditor().isOpen();
        }
        cancelEdit();
        return false;
    }

    @Override
    public void cancelEdit() {
        Editor<?> e = getEditor();
        if(e.isOpen()) {
            e.cancel();
            e.closeEditor();
        }
        editingItem = null;
    }

    /**
     * Save the currently editing item. Editor will be closed only if the item is saved successfully because
     * {@link #validateFieldValues()} will be invoked to validate the field values.
     */
    public void saveEdited() {
        if(editingItem != null) {
            T item = editingItem;
            Editor<T> e = getEditor();
            if(e.isOpen()) {
                try {
                    validateFieldValues();
                } catch(Exception exception) {
                    warning(exception);
                    return;
                }
                e.save();
            }
            editingItem = null;
            e.closeEditor();
            select(item);
        }
    }

    /**
     * Get the item being currently edited.
     *
     * @return Item that is being edited. Will be null if no editing is going on.
     */
    public final T getEditingItem() {
        return editingItem;
    }

    private class Refresher implements DataList.RefreshListener<T> {

        private Registration registration;

        void change() {
            if(registration != null) {
                registration.remove();
                registration = ((Data<T>)getEditableList()).data.addRefreshListener(this);
                refresh();
            }
        }

        void set() {
            if(registration == null) {
                registration = ((Data<T>)getEditableList()).data.addRefreshListener(this);
                refresh();
            }
        }

        void remove() {
            if(registration != null) {
                registration.remove();
                registration = null;
            }
        }

        @Override
        public void refresh() {
            EditableGrid.this.refresh();
        }

        @Override
        public void refresh(T item) {
            EditableGrid.this.refresh(item);
        }
    }

    static class Data<O> extends ListDataProvider<O> implements EditableList<O> {

        private final DataList<O> data;
        private final Set<O> added = new HashSet<>();
        private final Set<O> edited = new HashSet<>();
        private final Set<O> deleted = new HashSet<>();
        private boolean fromClient = true;

        public Data() {
            this(new DataList<>());
        }

        private Data(DataList<O> data) {
            super(data);
            this.data = data;
        }

        public boolean isChanged() {
            return !added.isEmpty() || !edited.isEmpty() || !deleted.isEmpty();
        }

        public void setFromClient(boolean fromClient) {
            this.fromClient = fromClient;
        }

        public boolean isFromClient() {
            return fromClient;
        }

        public void clear() {
            savedAll();
            data.clear();
        }

        public void savedAll() {
            added.clear();
            deleted.clear();
            edited.clear();
        }

        @Override
        public boolean contains(O item) {
            return data.contains(item);
        }

        @Override
        public boolean isAdded(O item) {
            return added.contains(item);
        }

        @Override
        public boolean isDeleted(O item) {
            return deleted.contains(item);
        }

        @Override
        public boolean isEdited(O item) {
            return edited.contains(item);
        }

        @Override
        public Stream<O> streamAll() {
            return data.stream();
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public boolean append(O item) {
            return data.add(item);
        }

        @Override
        public boolean add(O item) {
            added.add(item);
            return data.add(item);
        }

        @Override
        public boolean delete(O item) {
            if(isDeleted(item)) {
                return false;
            }
            if(isAdded(item)) {
                added.remove(item);
                return true;
            }
            deleted.add(item);
            return true;
        }

        @Override
        public boolean undelete(O item) {
            if(!isDeleted(item)) {
                return false;
            }
            deleted.remove(item);
            return true;
        }

        @Override
        public boolean update(O item) {
            if(isAdded(item) || isEdited(item)) {
                return false;
            }
            if(isDeleted(item)) {
                deleted.remove(item);
            }
            edited.add(item);
            return true;
        }

        public void removeDeleted() {
            data.removeIf(deleted::contains);
            deleted.clear();
        }
    }
}
