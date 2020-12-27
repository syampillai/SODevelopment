package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ListGrid;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import org.apache.poi.ss.usermodel.Cell;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A grid populated from a {@link Query} or a {@link ResultSet}.
 *
 * @author Syam
 */
public class QueryGrid extends ListGrid<QueryGrid.QueryResult> {

    private final Map<Integer, String> columnNames = new HashMap<>();
    private final Map<String, Integer> columnIndices = new HashMap<>();
    protected final ButtonLayout buttonPanel;
    protected Button pdf, excel, exit;
    private int pdfPageOrientation = PDF.ORIENTATION_LANDSCAPE;

    /**
     * Constructor.
     *
     * @param query Query to be used to populate the grid.
     */
    public QueryGrid(Query query) {
        this(query, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param query Query to be used to populate the grid.
     * @param actions Action (As specified in {@link EditorAction}). Only {@link EditorAction#PDF} and
     *                {@link EditorAction#EXCEL} are valid.
     */
    public QueryGrid(Query query, int actions) {
        this(query.getResultSet(), actions);
        query.close();
    }

    /**
     * Constructor.
     *
     * @param resultSet SQL result set  to be used to populate the grid.
     */
    public QueryGrid(ResultSet resultSet) {
        this(resultSet, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param resultSet SQL result set  to be used to populate the grid.
     * @param actions Action (As specified in {@link EditorAction}). Only {@link EditorAction#PDF} and
     *                {@link EditorAction#EXCEL} are valid.
     */
    public QueryGrid(ResultSet resultSet, int actions) {
        this(null, resultSet, actions);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param query Query to be used to populate the grid.
     */
    public QueryGrid(String caption, Query query) {
        this(query, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param query Query to be used to populate the grid.
     * @param actions Action (As specified in {@link EditorAction}). Only {@link EditorAction#PDF} and
     *                {@link EditorAction#EXCEL} are valid.
     */
    public QueryGrid(String caption, Query query, int actions) {
        this(query.getResultSet(), actions);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param resultSet SQL result set  to be used to populate the grid.
     */
    public QueryGrid(String caption, ResultSet resultSet) {
        this(resultSet, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param resultSet SQL result set  to be used to populate the grid.
     * @param actions Action (As specified in {@link EditorAction}). Only {@link EditorAction#PDF} and
     *                {@link EditorAction#EXCEL} are valid.
     */
    public QueryGrid(String caption, ResultSet resultSet, int actions) {
        super(QueryResult.class, StringList.EMPTY);
        setCaption(caption == null ? "Result" : caption);
        addConstructedListener(o -> con());
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                if(includeColumn(i)) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    if(columnName != null) {
                        columnName = columnName.trim();
                    }
                    if(columnName == null || columnName.isEmpty()) {
                        columnName = "" + i;
                    }
                    while(columnIndices.get(columnName) != null) {
                        columnName += " ";
                    }
                    columnNames.put(i, columnName);
                    columnIndices.put(columnName, i);
                    int columnIndex = i;
                    //noinspection unchecked
                    createColumn(columnName, queryResult -> queryResult.getValue(columnIndex));
                }
            }
            reload(resultSet);
        } catch (Exception ignored) {
        }
        if(actions == EditorAction.ALL || (actions & EditorAction.PDF) == EditorAction.PDF) {
            pdf = new Button("PDF", e -> new PDF().execute(getCaption()));
        }
        if(actions == EditorAction.ALL || (actions & EditorAction.EXCEL) == EditorAction.EXCEL) {
            excel = new Button("Excel", e -> new Excel().execute());
        }
        if(!isCloseable()) {
            exit = new Button("Exit", e -> close());
            exit.setVisible(false);
        }
        if(pdf != null || excel != null || exit != null) {
            buttonPanel = new ButtonLayout();
            buttonPanel.add(pdf, excel, exit);
        } else {
            buttonPanel = null;
        }
    }

    /**
     * Reload the grid with another result set.
     * <p>Warning: The result set should contain the same number and type of columns that was
     * already set.</p>
     *
     * @param resultSet Result set from which entries to be reloaded.
     */
    public void reload(ResultSet resultSet) {
        clear();
        try {
            QueryResult qr;
            do {
                qr = QueryResult.create(resultSet, columnNames.keySet(), this);
                if(qr != null) {
                    add(qr);
                }
            } while(resultSet.next());
        } catch(Throwable ignored) {
        } finally {
            IO.close(resultSet);
        }
    }

    @Override
    public View getView(boolean create) {
        if(exit != null) {
            exit.setVisible(true);
        }
        return super.getView(create);
    }

    @Override
    public Component createHeader() {
        return buttonPanel;
    }

    private void con() {
        if(isEmpty()) {
            return;
        }
        QueryResult rs = get(0);
        Object columnValue;
        for(int c: columnNames.keySet()) {
            columnValue = rs.getValue(c);
            if(columnValue == null) {
                continue;
            }
            if(Utility.isRightAligned(columnValue)) {
                getColumn(columnNames.get(c)).setTextAlign(ColumnTextAlign.END);
            }
        }
    }

    @Override
    public String getColumnCaption(String columnName) {
        try {
            String c = getColumnCaption(columnIndices.get(columnName));
            if(c != null) {
                return c;
            }
        } catch(Throwable ignored) {
        }
        return super.getColumnCaption(columnName);
    }

    /**
     * Get the caption for the column.
     *
     * @param columnIndex Index of the column.
     * @return Caption.
     */
    public String getColumnCaption(int columnIndex) {
        return null;
    }

    private String caption(int columnIndex) {
        String name = columnNames.get(columnIndex);
        String c;
        try {
            c = getColumnCaption(name);
            if(c != null) {
                return c;
            }
        } catch(Throwable ignored) {
        }
        return StringUtility.makeLabel(name);
    }

    @Override
    public int getColumnOrder(String columnName) {
        return columnIndices.get(columnName);
    }

    private ArrayList<Integer> oKeys() {
        ArrayList<Integer> keys = new ArrayList<>(columnNames.keySet());
        Collections.sort(keys);
        return keys;
    }

    /**
     * Whether to include the given column in the grid or not.
     *
     * @param columnIndex Column index.
     * @return True/false. Default implementation returns <code>true</code>.
     */
    public boolean includeColumn(int columnIndex) {
        return true;
    }

    /**
     * Convert the value for a given column for displaying it on the grid.
     *
     * @param value Value to convert.
     * @param columnIndex Column index.
     * @return Converted value. The default implementation returns the same value.
     */
    public Object convertValue(Object value, int columnIndex) {
        return value;
    }

    /**
     * Query result corresponding to a row of the grid.
     *
     * @author Syam
     */
    public static class QueryResult extends HashMap<Integer, Object> {

        private QueryResult() {
        }

        private static QueryResult create(ResultSet rs, Set<Integer> columnIndices, QueryGrid queryGrid) {
            AtomicBoolean error = new AtomicBoolean(false);
            QueryResult qr = new QueryResult();
            columnIndices.forEach(i -> {
                try {
                    qr.put(i, queryGrid.convertValue(rs.getObject(i), i));
                } catch(SQLException e) {
                    error.set(true);
                }
            });
            return error.get() ? null : qr;
        }

        /**
         * Get the value for a given column index.
         *
         * @param columnIndex Column index.
         * @return Value.
         */
        public Object getValue(int columnIndex) {
            return get(columnIndex);
        }
    }

    /**
     * Set the orientation fo the PDF report. The parameter can be
     * {@link com.storedobject.pdf.PDF#ORIENTATION_LANDSCAPE} or {@link com.storedobject.pdf.PDF#ORIENTATION_PORTRAIT}.
     *
     * @param pageOrientation Orientation.
     */
    public void setPDFPageOrientation(int pageOrientation) {
        this.pdfPageOrientation = pageOrientation;
    }

    private class PDF extends PDFReport {

        public PDF() {
            super(Application.get());
        }

        @Override
        public Object getTitle() {
            return getCaption();
        }

        @Override
        public void generateContent() throws Exception {
            ArrayList<Integer> indices = oKeys();
            if(indices.isEmpty()) {
                return;
            }
            int[] w = new int[indices.size()];
            int ii;
            for(ii = 0; ii < w.length; ii++) {
                w[ii] = caption(indices.get(ii)).length();
            }
            QueryGrid.this.stream().limit(60).forEach(qr -> {
                for(int i = 0; i < w.length; i++) {
                    Object v = qr.get(indices.get(i));
                    if(v != null) {
                        v = v.toString();
                        if(v != null) {
                            if(w[i] < ((String) v).length()) {
                                w[i] = ((String) v).length();
                            }
                        }
                    }
                }
            });
            for(ii = 0; ii < w.length; ii++) {
                if(w[ii] <= 0) {
                    w[ii] = 1;
                }
            }
            PDFTable table = createTable(w);
            QueryResult firstRow = get(0);
            indices.forEach(i -> table.addCell(createCell(createTitleText(caption(i)), Utility.isRightAligned(firstRow.get(i)))));
            table.setHeaderRows(1);
            for(QueryResult qr : QueryGrid.this) {
                indices.forEach(i -> {
                    Object v = qr.get(i);
                    table.addCell(createCell(createCell(v), Utility.isRightAligned(v)));
                });
                if(table.getNumberOfRows() > 60) {
                    addTable(table);
                }
            }
            addTable(table);
        }

        @Override
        public int getPageOrientation() {
            return pdfPageOrientation;
        }
    }

    private class Excel extends ExcelReport {

        public Excel() {
            super(Application.get());
        }

        @Override
        public void generateContent() throws Exception {
            ArrayList<Integer> indices = oKeys();
            if(indices.isEmpty()) {
                return;
            }
            indices.forEach(i -> {
                Cell cell = getNextCell();
                setCellValue(cell, caption(i));
                if(!QueryGrid.this.isEmpty()) {
                    Object v = QueryGrid.this.get(0).get(i);
                    if(Utility.isRightAligned(v)) {
                        cell.setCellStyle(getRightAlignedStyle());
                    }
                }
            });
            for(QueryResult qr : QueryGrid.this) {
                getNextRow();
                indices.forEach(i -> {
                    Object v = qr.get(i);
                    Cell cell = getNextCell();
                    if(Utility.isRightAligned(v)) {
                        cell.setCellStyle(getRightAlignedStyle());
                    }
                    setCellValue(cell, v);
                });
            }
            workbook.setSheetName(0, getCaption());
        }
    }
}