package com.storedobject.ui;

import com.storedobject.core.Filtered;
import com.storedobject.core.EditableList;
import com.storedobject.core.EditorAction;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
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
public abstract class AbstractEditableGrid<T> extends DataGrid<T> {

    static final String NONE_MARK = "", EDITED_MARK = "*", ADDED_MARK = "+", DELETED_MARK = "-";
    private Column<T> markerColumn;
    private Registration singleClick;
    private boolean fromClient = true;
    private final EditableList<T> eList;

    /**
     * Constructor that will generate columns from the Bean's properties.
     *
     * @param objectClass Bean type
     */
    public AbstractEditableGrid(Class<T> objectClass, Filtered<T> list) {
        this(objectClass, list, null);
    }

    /**
     * Constructor that will generate columns from the column names passed.
     *
     * @param objectClass Bean type
     * @param columns Column names
     */
    @SuppressWarnings("unchecked")
    public AbstractEditableGrid(Class<T> objectClass, Filtered<T> list, Iterable<String> columns) {
        super(objectClass, list, columns);
        //noinspection unchecked
        createColumn(EDITED_MARK, this::editMark);
        setClassNameGenerator(this::styleName);
        setSelectionMode(SelectionMode.SINGLE);
        getElement().getClassList().remove("so-grid");
        getElement().getClassList().add("so-editable-grid");
        getElement().setAttribute("theme", "wrap-cell-content");
        addConstructedListener(o -> addItemDoubleClickListener(e -> editItem(e.getItem(), e.getColumn())));
        eList = createEditableList();
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

    protected EditableList<T> createEditableList() {
        return new EList();
    }

    @Override
    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof EditableList;
    }

    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    public boolean isFromClient() {
        return fromClient;
    }

    /**
     * Get the editable list maintained by this grid.
     *
     * @return Editable list instance.
     */
    public final EditableList<T> getEditableList() {
        return eList;
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
        return eList.isEdited(item) ? EDITED_MARK : (eList.isAdded(item) ? ADDED_MARK
                : (eList.isDeleted(item) ? DELETED_MARK : NONE_MARK));
    }

    private String styleName(T item) {
        return switch(editMark(item)) {
            case ADDED_MARK -> "so-added";
            case EDITED_MARK -> "so-edited";
            case DELETED_MARK -> "so-deleted";
            default -> null;
        };
    }

    private EditableProvider<T> eprovider() {
        //noinspection unchecked
        return (EditableProvider<T>) getDataProvider();
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

    /**
     * Cancel the editing if it is active.
     */
    protected abstract void cancelEdit();

    /**
     * This method doesn't clear the items. Instead, it just invokes the {@link #fireChanged(Object, int)} method with
     * <code>null</code> as the item value and {@link EditorAction#ALL} as the "change action". Subclasses should
     * implement the real "clear" functionality.
     */
    public void clear() {
        cancelEdit();
        //noinspection unchecked
        ((EditableProvider<T>)getDataProvider()).clear();
        fireChanged(null, EditorAction.ALL);
    }

    /**
     * For internal use for firing change events.
     *
     * @param item Item that is changed. For changes affecting more than one item, this will be <code>null</code>.
     * @param changeAction Change action (One of the static values defined in the {@link EditorAction}). For changes
     *                     affecting more than one item, this will be {@link EditorAction#ALL}.
     */
    void fireChanged(T item, int changeAction) {
        switch(changeAction) {
            case EditorAction.APPEND -> super.add(item);
            case EditorAction.ADD -> doInsertAction(item);
            case EditorAction.EDIT -> doUpdateAction(item);
            case EditorAction.DELETE -> doDeleteAction(item);
            case EditorAction.RELOAD -> doReloadAction(item);
            case EditorAction.ALL -> doReloadAllAction();
        }
        changed();
        changed(item, changeAction);
    }

    @Override
    public boolean add(T item) {
        if(item != null) {
            itemInserted(item);
            return true;
        }
        return super.add(item);
    }

    public void delete(T item) {
        if(item != null) {
            itemDeleted(item);
        }
    }

    public void update(T item) {
        if(item != null) {
            itemUpdated(item);
        }
    }

    public void reload(T object) {
        cancelEdit();
        fireChanged(object, EditorAction.RELOAD);
    }

    public void reloadAll() {
        cancelEdit();
        fireChanged(null, EditorAction.ALL);
    }

    void changed() {
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

    public final boolean isAdded(T item) {
        return eprovider().isAdded(item);
    }

    public final boolean isDeleted(T item) {
        return eprovider().isDeleted(item);
    }

    public final boolean isEdited(T item) {
        return eprovider().isEdited(item);
    }

    public Stream<T> streamAll() {
        return eprovider().streamAll();
    }

    public Stream<T> streamAdded() {
        return eprovider().streamAdded();
    }

    public Stream<T> streamEdited() {
        return eprovider().streamEdited();
    }

    public Stream<T> streamDeleted() {
        return eprovider().streamDeleted();
    }

    public boolean isSavePending() {
        return eprovider().isSavePending();
    }

    class EList implements EditableList<T> {

        @Override
        public boolean contains(Object item) {
            return AbstractEditableGrid.this.contains(item);
        }

        @Override
        public boolean isAdded(T item) {
            return eprovider().isAdded(item);
        }

        @Override
        public boolean isDeleted(T item) {
            return eprovider().isDeleted(item);
        }

        @Override
        public boolean isEdited(T item) {
            return eprovider().isEdited(item);
        }

        @Override
        public Stream<T> stream() {
            return EditableList.super.stream();
        }

        @Override
        public Stream<T> streamAll() {
            return eprovider().streamAll();
        }

        @Override
        public Stream<T> streamAdded() {
            return eprovider().streamAdded();
        }

        @Override
        public Stream<T> streamEdited() {
            return eprovider().streamEdited();
        }

        @Override
        public Stream<T> streamDeleted() {
            return eprovider().streamDeleted();
        }

        @Override
        public int size() {
            return AbstractEditableGrid.this.size();
        }

        @Override
        public boolean append(T item) {
            cancelEdit();
            if(eprovider().append(item, false)) {
                fireChanged(item, EditorAction.APPEND);
                return true;
            }
            return false;
        }

        @Override
        public boolean add(T item) {
            cancelEdit();
            if(shouldInsert(item) && eprovider().add(item, false)) {
                fireChanged(item, EditorAction.NEW);
                select(item);
                return true;
            }
            return false;
        }

        @Override
        public boolean delete(T item) {
            cancelEdit();
            if(shouldDelete(item) && eprovider().delete(item, false)) {
                deselect(item);
                fireChanged(item, EditorAction.DELETE);
                return true;
            }
            return false;
        }

        @Override
        public boolean undelete(T item) {
            cancelEdit();
            if(shouldUndelete(item) && eprovider().undelete(item, false)) {
                fireChanged(item, EditorAction.RELOAD);
                return true;
            }
            return false;
        }

        @Override
        public boolean update(T item) {
            cancelEdit();
            if(shouldUpdate(item) && eprovider().update(item, false)) {
                fireChanged(item, EditorAction.EDIT);
                return true;
            }
            return false;
        }
    }
}