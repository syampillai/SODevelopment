package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.pdf.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.LitRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Create a PDF report from the grid rows.
 *
 * @param <T> Type of object in the grid.
 * @author Syam
 */
public class ObjectGridReport<T extends StoredObject> extends PDFReport {

    /**
     * The associated grid.
     */
    protected final ObjectListGrid<T> grid;
    /**
     * The main table.
     */
    protected PDFTable table;
    private TableHeader tableHeader;
    private int columnIndex;

    /**
     * Constructor.
     *
     * @param grid Associated grid.
     */
    public ObjectGridReport(ObjectListGrid<T> grid) {
        super(Application.get());
        this.grid = grid;
    }

    private List<Grid.Column<T>> columns() {
        List<Grid.Column<T>> columns = grid.getColumns(), columnsToPrint = new ArrayList<>();
        Grid.Column<T> column;
        int[] w = new int[columns.size()];
        for(int i = 0; i < w.length; i++) {
            column = columns.get(i);
            if(!column.isVisible() || !(column.getRenderer() instanceof LitRenderer<?>)) {
                w[i] = -1;
                continue;
            }
            w[i] = getWidth(column.getKey());
            if(w[i] > 0) {
                columnsToPrint.add(column);
            }
        }
        if(columnsToPrint.isEmpty()) {
            return null;
        }
        String[] headers = new String[columnsToPrint.size()];
        for(int i = 0; i < columnsToPrint.size(); i++) {
            headers[i] = getCaption(columnsToPrint.get(i).getKey());
        }
        tableHeader = new TableHeader(headers);
        tableHeader.setWidths(w);
        String columnName;
        for(int i = 0; i < columnsToPrint.size(); i++) {
            columnName = columnsToPrint.get(i).getKey();
            tableHeader.setHorizontalAlignment(i,  getHorizontalAlignment(columnName));
            tableHeader.setVerticalAlignment(i, getVerticalAlignment(columnName));
        }
        tableHeader.setCellCustomizer(getCellCustomizer());
        return columnsToPrint;
    }

    @Override
    public void generateContent() throws Exception {
        List<Grid.Column<T>> columns = columns();
        if(columns == null) {
            add("Nothing to print!");
            return;
        }
        table = tableHeader.createTable(this);
        String columnName;
        for(T object: grid) {
            if(canPrint(object)) {
                for(columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    columnName = columns.get(columnIndex).getKey();
                    doPrint(columnName, object, grid.getColumnDetail(columnName).getValueFunction().apply(object));
                }
                rowPrinted(object);
            }
        }
        add(table);
    }

    /**
     * Get the caption of the column (Default implementation returns corresponding grid column caption).
     *
     * @param columnName Column name.
     * @return Caption.
     */
    protected String getCaption(String columnName) {
        return grid.getColumnDetail(columnName).getCaption();
    }

    /**
     * Get the relative width of the column (Default implementation returns 10).
     * <p>Note: Return 0 if you don't want to print this column.</p>
     *
     * @param columnName Column name.
     * @return Relative width.
     */
    protected int getWidth(String columnName) {
        return 10;
    }

    /**
     * Get the horizontal alignment. Should return one of {@link PDFElement#ALIGN_LEFT},
     * {@link PDFElement#ALIGN_CENTER} or {@link PDFElement#ALIGN_RIGHT}. Default implementation returns a value
     * based on the alignment of the grid's column.
     *
     * @param columnName Column name.
     * @return Alignment.
     */
    protected int getHorizontalAlignment(String columnName) {
        return switch(grid.getColumnByKey(columnName).getTextAlign()) {
            case CENTER -> PDFElement.ALIGN_CENTER;
            case END -> PDFElement.ALIGN_RIGHT;
            default -> PDFElement.ALIGN_LEFT;
        };
    }

    /**
     * Get the vertical alignment. Should return one of {@link PDFElement#ALIGN_BOTTOM},
     * {@link PDFElement#ALIGN_MIDDLE} or {@link PDFElement#ALIGN_TOP}. Default implementation returns
     * {@link PDFElement#ALIGN_MIDDLE}.
     *
     * @param columnName Column name.
     * @return Alignment.
     */
    protected int getVerticalAlignment(String columnName) {
        return PDFElement.ALIGN_MIDDLE;
    }

    /**
     * This will be invoked before printing each row. Row will be skipped if returned false from this method.
     *
     * @param object The object in the current row.
     * @return True/false.
     */
    protected boolean canPrint(T object) {
        return true;
    }

    /**
     * This method is invoked after a row is printed so that any extra printing or computation can be done here.
     *
     * @param object The object in the current row.
     */
    protected void rowPrinted(T object) {
    }

    private void doPrint(String columnName, T object, Object cellValue) {
        printCell(columnName, object, customizeCellValue(columnName, object, cellValue));
    }

    /**
     * This method is invoked to print a cell value of the row.
     *
     * @param columnName Column name.
     * @param object The object in the current row.
     * @param cellValue Cell value.
     */
    protected void printCell(String columnName, T object, Object cellValue) {
        tableHeader.addCells(columnIndex, cellValue);
    }

    /**
     * This method is invoked before printing a cell value to obtain its customized value. Default implementation
     * returns the same value without any changes.
     *
     * @param columnName Column name.
     * @param object The object in the current row.
     * @param cellValue Cell value.
     * @return Customized cell value.
     */
    protected Object customizeCellValue(String columnName, T object, Object cellValue) {
        return cellValue;
    }

    /**
     * Get a customizer for the cells that will be created for adding to the report.
     *
     * @return  Cell customizer.
     */
    public Function<PDFCell, PDFCell> getCellCustomizer() {
        return null;
    }
}
