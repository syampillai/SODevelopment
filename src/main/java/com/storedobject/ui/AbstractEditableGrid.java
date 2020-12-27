package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.EditableList;
import com.storedobject.core.EditorAction;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.stream.Stream;

/**
 * An editable grid. It internally maintains an {@link EditableList} that provides status information on each row
 * of the grid. (See {@link #getEditableList()}).
 *
 * @param <T> Type of object to edit.
 * @author Syam
 */
@CssImport(value =  "./so/editable-grid/styles.css", themeFor = "vaadin-grid")
public abstract class AbstractEditableGrid<T> extends DataGrid<T> implements EditableList<T>, ResourceOwner {

    static final String NONE_MARK = "", EDITED_MARK = "*", ADDED_MARK = "+", DELETED_MARK = "-";
    private Column<T> markerColumn;
    private Registration singleClick;

    /**
     * Constructor that will generate columns from the Bean's properties.
     *
     * @param objectClass Bean type
     */
    public AbstractEditableGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    /**
     * Constructor that will generate columns from the column names passed.
     *
     * @param objectClass Bean type
     * @param columns Column names
     */
    public AbstractEditableGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, null);
    }

    /**
     * Constructor that will generate columns from the column names passed.
     *
     * @param objectClass Bean type
     * @param columns Column names
     * @param dataProvider Data provider.
     */
    @SuppressWarnings("unchecked")
    public AbstractEditableGrid(Class<T> objectClass, Iterable<String> columns, EditableList<T> dataProvider) {
        super(objectClass, columns);
        if(dataProvider instanceof ListDataProvider) {
            setItems((ListDataProvider<T>)dataProvider);
        } else if(dataProvider instanceof DataProvider) {
            setItems((DataProvider<T, Void>)dataProvider);
        }
        //noinspection unchecked
        createColumn(EDITED_MARK, this::editMark);
        setClassNameGenerator(this::styleName);
        setSelectionMode(SelectionMode.SINGLE);
        getElement().getClassList().remove("so-grid");
        getElement().getClassList().add("so-editable-grid");
        getElement().setAttribute("theme", "wrap-cell-content");
        addConstructedListener(o -> addItemDoubleClickListener(e -> editItem(e.getItem(), e.getColumn())));
    }

    @SuppressWarnings("unchecked")
    void resetProvider() {
        DataProvider<T, ?> dp = getDataProvider();
        if(dp instanceof ListDataProvider) {
            setItems((ListDataProvider<T>)dp);
        } else if(dp != null) {
            setItems((DataProvider<T, Void>)dp);
        }
    }

    /**
     * Normally editing is started when double-clicked on the row. This setting allows editing on single-click.
     *
     * @param editOnSingleClick True/false.
     */
    public void setEditOnSingleClick(boolean editOnSingleClick) {
        if(editOnSingleClick) {
            if(singleClick == null) {
                singleClick = addItemClickListener(e -> editItem(e.getItem(), e.getColumn()));
            }
        } else {
            if(singleClick != null) {
                singleClick.remove();
                singleClick = null;
            }
        }
    }

    /**
     * Get the field for the given column name.
     *
     * @param columnName Name of the column for which field is required.
     * @return Field if found, otherwise <code>null</code> is returned.
     */
    public HasValue<?, ?> getField(String columnName) {
        return null;
    }

    private void editItem(T item, Grid.Column<T> column) {
        if(editItem(item)) {
            if(column != null) {
                HasValue<?, ?> field = getField(column.getKey());
                if(field instanceof Focusable) {
                    ((Focusable<?>) field).focus();
                }
            }
        }
    }

    /**
     * Edit an item. Default implementation doesn't do anything and returns <code>false</code>. This method is
     * invoked whenever a row is double-clicked (or single-clicked when {@link #setEditOnSingleClick(boolean)} is set).
     *
     * @param item Item to be edited.
     * @return <code>True</code> if the editing is started, otherwise <code>false</code>.
     */
    public boolean editItem(T item) {
        return false;
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    @Override
    protected final Editor<T> createEditor() {
        Editor<T> editor = super.createEditor();
        editor.setBuffered(true);
        return editor;
    }

    private String editMark(T item) {
        EditableList<T> e = getEditableList();
        if(e == null) {
            return NONE_MARK;
        }
        return e.isEdited(item) ? EDITED_MARK : (e.isAdded(item) ? ADDED_MARK : (e.isDeleted(item) ? DELETED_MARK : NONE_MARK));
    }

    private String styleName(T item) {
        switch (editMark(item)) {
            case ADDED_MARK:
                return "so-added";
            case EDITED_MARK:
                return "so-edited";
            case DELETED_MARK:
                return "so-deleted";
        }
        return null;
    }

    @Override
    public final GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        GridDataView<T> gv = null;
        if(dataProvider instanceof EditableList) {
            DataProvider<T, ?> dp = super.getDataProvider();
            boolean different = dataProvider != dp;
            if(different && dp instanceof AutoCloseable) {
                IO.close((AutoCloseable) dp);
            }
            gv = super.setItems(dataProvider);
            if(different && dataProvider instanceof AutoCloseable) {
                ResourceDisposal.register(this);
            }
        }
        return gv;
    }

    @Override
    public final GridListDataView<T> setItems(ListDataProvider<T> dataProvider) {
        GridListDataView<T> gv = null;
        if(dataProvider instanceof EditableList) {
            DataProvider<T, ?> dp = super.getDataProvider();
            boolean different = dataProvider != dp;
            if(different && dp instanceof AutoCloseable) {
                IO.close((AutoCloseable) dp);
            }
            gv = super.setItems(dataProvider);
            if(different && dataProvider instanceof AutoCloseable) {
                ResourceDisposal.register(this);
            }
        }
        return gv;
    }

    @Override
    public final AutoCloseable getResource() {
        DataProvider<?,?> dp = getDataProvider();
        return dp instanceof ResourceOwner ? ((ResourceOwner)dp).getResource() : null;
    }

    /**
     * Get the "editable list" from this grid.
     *
     * @return The embedded "editable list".
     */
    public EditableList<T> getEditableList() {
        //noinspection unchecked
        return (EditableList<T>) getDataProvider();
    }

    @Override
    public int getColumnOrder(String columnName) {
        if("*".equals(columnName)) {
            return Integer.MIN_VALUE + 1;
        }
        return super.getColumnOrder(columnName);
    }

    @Override
    public void customizeColumn(String columnName, Column<T> column) {
        if(EDITED_MARK.equals(columnName)) {
            markerColumn = column;
            column.setHeader(NONE_MARK);
            column.setTextAlign(ColumnTextAlign.END);
            resizeMarkerColumn();
        }
    }

    void resizeMarkerColumn() {
        if(markerColumn != null) {
            markerColumn.setFlexGrow(1).setFrozen(false).setResizable(true).setAutoWidth(true);
            markerColumn.setWidth(getMarkerColumnWidth() + "px").setAutoWidth(false).setResizable(false).setFrozen(true).setFlexGrow(0);
        }
    }

    int getMarkerColumnWidth() {
        return 30;
    }

    @Override
    public boolean contains(T item) {
        return getEditableList().contains(item);
    }

    @Override
    public boolean isAdded(T item) {
        return getEditableList().isAdded(item);
    }

    @Override
    public boolean isDeleted(T item) {
        return getEditableList().isDeleted(item);
    }

    @Override
    public boolean isEdited(T item) {
        return getEditableList().isEdited(item);
    }

    @Override
    public Stream<T> streamAll() {
        return getEditableList().streamAll();
    }

    @Override
    public int size() {
        return getEditableList().size();
    }

    @Override
    public boolean append(T item) {
        cancelEdit();
        if(getEditableList().append(item)) {
            fireChanged(item, EditorAction.APPEND);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(T item) {
        cancelEdit();
        if(getEditableList().add(item)) {
            fireChanged(item, EditorAction.NEW);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(T item) {
        cancelEdit();
        deselect(item);
        if(getEditableList().delete(item)) {
            fireChanged(item, EditorAction.DELETE);
            return true;
        }
        return false;
    }

    @Override
    public boolean undelete(T item) {
        cancelEdit();
        if(getEditableList().undelete(item)) {
            fireChanged(item, EditorAction.RELOAD);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(T item) {
        cancelEdit();
        if(getEditableList().update(item)) {
            fireChanged(item, EditorAction.EDIT);
            return true;
        }
        return false;
    }

    /**
     * Cancel the editing if it is active.
     */
    protected abstract void cancelEdit();

    /**
     * For internal use only.
     */
    void changed() {
    }

    /**
     * This method doesn't clear the items. Instead, it just invokes the {@link #fireChanged(Object, int)} method with
     * <code>null</code> as the item value and {@link EditorAction#ALL} as the "change action". Sun-classes should
     * implement the real "clear" functionality.
     */
    public void clear() {
        fireChanged(null, EditorAction.ALL);
    }

    /**
     * For internal use only.
     */
    void fireChanged(T item, int changeAction) {
        changed();
        changed(item, changeAction);
    }

    /**
     * This method is invoked whenever any change happens so that other UI elements can be updated.
     *
     * @param item Item that is changed. For changes affecting more than one item, this will be <code>null</code>.
     * @param changeAction Change action (One of the static values defined in the {@link EditorAction}). For changes
     *                     affecting more than one item, this will be {@link EditorAction#ALL}.
     */
    public void changed(T item, int changeAction) {
    }
}