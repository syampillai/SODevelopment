package com.storedobject.ui;

import com.vaadin.flow.component.HasValue;

import java.util.stream.Stream;

public interface EditableDataGrid {
    boolean isColumnEditable(String columnName);
    Stream<HasValue<?, ?>> streamEditableFields();
}
