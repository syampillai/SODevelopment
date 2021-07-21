package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import com.storedobject.pdf.PDFElement;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ListGrid;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import org.apache.poi.ss.usermodel.Cell;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final Map<Integer, ColumnTextAlign> alignments = new HashMap<>();
    protected final ButtonLayout buttonPanel;
    protected Button pdf, excel, exit;
    private int pdfPageOrientation = PDF.ORIENTATION_LANDSCAPE;
    private ResultSet resultSet;

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
        this.resultSet = resultSet;
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        load();
        super.onAttach(attachEvent);
    }

    /**
     * Load the date from the result set if it is not already loaded.
     */
    public void load() {
        if(resultSet == null) {
            return;
        }
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
                    createColumn(columnName, qr -> convertValue(qr.getValue(columnIndex), columnIndex));
                }
            }
            ResultSet rs = resultSet;
            resultSet = null;
            reload(rs);
        } catch (Exception ignored) {
        }
        resultSet = null;
    }

    /**
     * Reload the grid with another result set. Existing entries will be cleared.
     * <p>Warning: The result set should contain the same number and type of columns that was
     * already set.</p>
     *
     * @param resultSet Result set from which entries to be reloaded.
     */
    public void reload(ResultSet resultSet) {
        if(this.resultSet != null) {
            if(this.resultSet == resultSet) {
                load();
                return;
            }
            load();
        }
        clear();
        if(pdf != null) {
            pdf.setVisible(false);
        }
        if(excel != null) {
            excel.setVisible(false);
        }
        Statement statement;
        try {
            statement = resultSet.getStatement();
        } catch(SQLException throwable) {
            statement = null;
        }
        while(true) {
            if(resultSet != null) {
                load(resultSet);
            }
            if(statement == null) {
                break;
            }
            try {
                if(statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT)) {
                    resultSet = statement.getResultSet();
                } else {
                    if(statement.getUpdateCount() >= 0) {
                        resultSet = null;
                        continue;
                    }
                    break;
                }
            } catch(SQLException e) {
                break;
            }
        }
    }

    /**
     * Load the grid with more rows from another result set.
     * <p>Warning: The result set should contain the same number and type of columns that was
     * already set.</p>
     *
     * @param resultSet Result set from which entries to be reloaded.
     */
    public void load(ResultSet resultSet) {
        if(this.resultSet != null) {
            if(this.resultSet == resultSet) {
                load();
                return;
            }
            load();
        }
        try {
            QueryResult qr;
            do {
                qr = QueryResult.create(resultSet, columnNames.keySet());
                if(qr != null) {
                    add(qr);
                }
            } while(resultSet.next());
        } catch(Throwable ignored) {
        } finally {
            IO.close(resultSet);
        }
        if(pdf != null) {
            pdf.setVisible(!isEmpty());
        }
        if(excel != null) {
            excel.setVisible(!isEmpty());
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
        for(int c: columnNames.keySet()) {
            getColumn(columnNames.get(c)).setTextAlign(alignment(rs.getValue(c), c));
        }
    }

    @Override
    public String getColumnCaption(String columnName) {
        try {
            String c = getColumnCaption(columnIndices.get(columnName));
            if(c != null) {
                return c;
            }
        } catch(Throwable error) {
            log("Error while getting caption for column " + columnName + "(Index: " + columnIndices.get(columnName) + ")", error);
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
        } catch(Throwable error) {
            log("Error while getting caption for column " + columnIndex + "(Name: " + name + ")", error);
        }
        return StringUtility.makeLabel(name);
    }

    @Override
    public int getColumnOrder(String columnName) {
        return columnIndices.get(columnName);
    }

    private ArrayList<Integer> oKeys() {
        ArrayList<Integer> keys = new ArrayList<>(columnNames.keySet());
        keys.removeIf(this::isHidden);
        Collections.sort(keys);
        return keys;
    }

    private boolean isHidden(int columnIndex) {
        return !getColumnByKey(columnNames.get(columnIndex)).isVisible();
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
     * Get the alignment of the column values.
     *
     * @param columnIndex Column index.
     * @return If null is returned, default alignment will be determined from the value (not from the converted value).
     */
    public ColumnTextAlign getTextAlign(int columnIndex) {
        return null;
    }

    ColumnTextAlign alignment(Object value, int columnIndex) {
        ColumnTextAlign a = alignments.get(columnIndex);
        if(a == null) {
            a = getTextAlign(columnIndex);
            if(a == null) {
                a = Utility.isRightAligned(value) ? ColumnTextAlign.END : ColumnTextAlign.START;
            }
            alignments.put(columnIndex, a);
        }
        return a;
    }

    /**
     * Query result corresponding to a row of the grid.
     *
     * @author Syam
     */
    public static class QueryResult extends HashMap<Integer, Object> {

        private QueryResult() {
        }

        private static QueryResult create(ResultSet rs, Set<Integer> columnIndices) {
            AtomicBoolean error = new AtomicBoolean(false);
            QueryResult qr = new QueryResult();
            columnIndices.forEach(i -> {
                try {
                    qr.put(i, rs.getObject(i));
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
            indices.forEach(i -> table.addCell(createCell(createTitleText(caption(i)), align(firstRow.get(i), i))));
            table.setHeaderRows(1);
            for(QueryResult qr : QueryGrid.this) {
                indices.forEach(i -> {
                    Object v = qr.get(i);
                    table.addCell(createCell(convertValue(v, i), align(v, i)));
                });
                add(table);
            }
            add(table);
        }

        private int align(Object value, int columnIndex) {
            return switch(QueryGrid.this.alignment(value, columnIndex)) {
                case END -> PDFElement.ALIGN_RIGHT;
                case CENTER -> PDFElement.ALIGN_CENTER;
                default -> PDFElement.ALIGN_LEFT;
            };
        }

        @Override
        public String getFileName() {
            return getCaption();
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
                    switch(alignment(v, i)) {
                        case END -> cell.setCellStyle(getRightAlignedStyle());
                        case CENTER -> cell.setCellStyle(getCenteredStyle());
                    }
                }
            });
            for(QueryResult qr : QueryGrid.this) {
                getNextRow();
                indices.forEach(i -> {
                    Object v = qr.get(i);
                    Cell cell = getNextCell();
                    switch(alignment(v, i)) {
                        case END -> cell.setCellStyle(getRightAlignedStyle());
                        case CENTER -> cell.setCellStyle(getCenteredStyle());
                    }
                    setCellValue(cell, convertValue(v, i));
                });
            }
            workbook.setSheetName(0, getCaption());
        }

        @Override
        public String getFileName() {
            return getCaption();
        }
    }
}