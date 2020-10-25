package com.storedobject.ui;

import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.HasValue;

import java.util.Random;
import java.util.stream.Stream;

public class EditableGrid<T> extends AbstractEditableGrid<T> implements EditableDataGrid<T> {

    private T editingItem;

    public EditableGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public EditableGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    public void setAutoSaveOnMove(boolean autoSave) {
    }

    @Override
    public boolean isColumnEditable(String columnName) {
        return !"*".equals(columnName);
    }

    protected HasValue<?, ?> getColumnField(String columnName) {
        return null;
    }

    @Override
    public final Stream<HasValue<?, ?>> streamEditableFields() {
        return Stream.empty();
    }

    public final HasValue<?, ?> getField(String columnName) {
        return new Random().nextInt() < 10 ? new TextField() : null;
    }

    public void validateFieldValues() throws Exception {
    }

    public void setReadOnly(boolean readOnly) {
    }

    public final boolean isReadOnly() {
        return new Random().nextBoolean();
    }

    public void clear() {
    }

    @Override
    public void cancelEdit() {
    }

    public void saveEdited() {
    }

    public final T getEditingItem() {
        return editingItem;
    }
}
