package com.storedobject.ui;

import com.storedobject.core.ObjectLoader;
import com.storedobject.core.*;
import com.vaadin.flow.component.Component;
import org.vaadin.stefan.table.Table;
import org.vaadin.stefan.table.TableBody;
import org.vaadin.stefan.table.TableDataCell;
import org.vaadin.stefan.table.TableRow;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * ObjectTable is similar to {@link ObjectGrid} but instead of the {@link com.vaadin.flow.component.grid.Grid}, it
 * uses {@link Table} to render the rows.
 * <p>Note: Please note that {@link ObjectTable} is not a {@link com.storedobject.vaadin.View} but is a
 * {@link com.vaadin.flow.component.Component}. So, you set it as a {@link com.vaadin.flow.component.Component}
 * to a {@link com.storedobject.vaadin.View} like {@link com.storedobject.vaadin.View#setComponent(Component)}.</p>
 *
 * @param <T> Type of the data object.
 */
public class ObjectTable<T extends StoredObject> extends Table implements ObjectLoader<T> {

    private final Class<T> objectClass;
    private int size = 0;
    private final ObjectLoadFilter<T> loadFilter = new ObjectLoadFilter<>();
    private final TableBody body;
    private final List<Function<T, ?>> colFunctions = new ArrayList<>();
    private final List<String> columns = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     */
    public ObjectTable(Class<T> objectClass) {
        this(objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param columns Columns to render.
     */
    public ObjectTable(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param any Whether to include subclasses of the data class or not.
     */
    public ObjectTable(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param columns Columns to render.
     * @param any Whether to include subclasses of the data class or not.
     */
    public ObjectTable(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this.objectClass = objectClass;
        DataGrid.columns(objectClass, columns).forEach(this.columns::add);
        body = getBody();
        loadFilter.setAny(any);
        build();
    }

    private void build() {
        Application a = Application.get();
        TableRow r = getHead().addRow();
        String caption;
        Function<T, ?> fun;
        for(String c: columns) {
            caption = getColumnCaption(c);
            r.addCells(caption == null ? StringUtility.makeLabel(c) : caption);
            fun = getColumnFunction(c);
            if(fun == null) {
                StoredObjectUtility.MethodList m = StoredObjectUtility.createMethodList(objectClass, c);
                fun = o -> m.display(a).apply(o);
            }
            colFunctions.add(fun);
        }
    }

    /**
     * Get the caption for the column.
     *
     * @param columnName Column name.
     * @return Caption.
     */
    public String getColumnCaption(String columnName) {
        return null;
    }

    /**
     * Get the object class.
     *
     * @return Object class.
     */
    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * Get the function to obtain the cell value for the column.
     *
     * @param columnName Column name.
     * @return Function.
     */
    public Function<T, ?> getColumnFunction(String columnName) {
        return null;
    }

    /**
     * Get column count.
     *
     * @return Colum count.
     */
    public final int getColumnCount() {
        return columns.size();
    }

    /**
     * Get the number of object instances rendered. The actual row-count may be different if extra rows are
     * inserted. To get the actual row-count you may use {@link #getRowCount()}.
     *
     * @return Count of the object instances currently rendered.
     */
    @Override
    public final int size() {
        return size;
    }

    /**
     * Get the row-count.
     *
     * @return Number of rows.
     */
    public final int getRowCount() {
        return getRows().size();
    }

    @Override
    public void clear() {
        removeAllRows();
        size = 0;
    }

    @Override
    public void load(ObjectIterator<T> objectIterator) {
        clear();
        for(T o: objectIterator) {
            ++size;
            addRow(o);
        }
    }

    @Override
    public void applyFilterPredicate() {
        load();
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return loadFilter;
    }

    private void addRow(T o) {
        addingRow(o);
        Application a = Application.get();
        TableRow r = body.addRow();
        TableDataCell c;
        String v, columnName;
        for(int i = 0; i < columns.size(); i++) {
            columnName = columns.get(i);
            v = customizeCellValue(columnName, o, StoredObjectUtility.toDisplay(a, colFunctions.get(i).apply(o)));
            c = r.addDataCell();
            c.add(v);
            customizeCell(columnName, o, c);
        }
        rowAdded(o);
    }

    /**
     * This method is invoked just before adding an object-row.
     *
     * @param object Object to add.
     */
    protected void addingRow(T object) {
    }

    /**
     * This method is invoked just after an object-row is added.
     *
     * @param object Object added.
     */
    protected void rowAdded(T object) {
    }

    /**
     * This method is invoked to get the customized value for a cell.
     *
     * @param columnName Column name.
     * @param object Object being added.
     * @param cellValue Cell value to customize.
     * @return Customized cell value.
     */
    protected String customizeCellValue(String columnName, T object, String cellValue) {
        return cellValue;
    }

    /**
     * This method is invoked to customize the currently added cell.
     *
     * @param columnName Column name.
     * @param object Object being added.
     * @param cell Cell that is currently added.
     */
    protected void customizeCell(String columnName, T object, TableDataCell cell) {
    }
}
