package com.storedobject.ui;

import com.storedobject.core.Financial;
import com.vaadin.flow.component.HasValue;

import java.util.stream.Stream;

/**
 * Methods for "editable" data grids.
 *
 * @param <T> Type of data in the grid.
 * @author Syam
 */
public interface EditableDataGrid<T> {

    /**
     * Whether a given column is editable or not. A field will be created for editing the value only if this
     * method returns true for a particular column.
     *
     * @param columnName Column name.
     * @return True/false.
     */
    boolean isColumnEditable(String columnName);

    /**
     * Get the stream of editable fields of this grid.
     *
     * @return Stream of editable fields.
     */
    Stream<HasValue<?, ?>> streamEditableFields();

    /**
     * Check whether an item can be edited or not. (This control will be applied only for user interaction).
     *
     * @param item Item.
     * @return True/false.
     */
    default boolean canEdit(T item) {
        return true;
    }

    /**
     * Check whether an item can be deleted or not. (This control will be applied only for user interaction).
     *
     * @param item Item to delete.
     * @return True/false.
     */
    default boolean canDelete(T item) {
        return true;
    }

    /**
     * Check whether financial entries can be created for the item or not. (This control will be applied for user
     * interaction).
     *
     * @param item Item for which financial entries to be created.
     * @return True/false.
     */
    default boolean canPostLedger(T item) {
        return item instanceof Financial;
    }

    /**
     * Check whether financial entries of this item can be viewed or not. (This control will be applied for user
     * interaction).
     *
     * @param item Item for which financial entries to be viewed.
     * @return True/false.
     */
    default boolean canViewLedger(T item) {
        return item instanceof Financial;
    }
}
