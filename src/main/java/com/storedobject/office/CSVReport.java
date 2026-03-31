package com.storedobject.office;

import com.storedobject.common.CSV;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

import java.io.IOException;
import java.io.Writer;

public class CSVReport extends TextReport {

    private final Report csv;

    /**
     * Constructor.
     *
     * @param device Device
     */
    public CSVReport(Device device) {
        this(device, new Report());
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param columnCount Column count.
     */
    public CSVReport(Device device, int columnCount) {
        this(device, new Report(columnCount));
    }

    private CSVReport(Device device, Report csv) {
        super(device, csv);
        this.csv = csv;
        this.csv.report = this;
    }

    /**
     * Sets the number of columns for the CSV report.
     *
     * @param columnCount The number of columns to be set.
     */
    public void setColumnCount(int columnCount) {
        csv.setColumnCount(columnCount);
    }

    /**
     * Set a value at a specific column in the current row.
     *
     * @param column Column,
     * @param value Value to set.
     */
    public final void setValue(int column, Object value) {
        csv.setValue(column, value);
    }

    /**
     * Set values starting from a specific column in the current row.
     *
     * @param startingColumn Column,
     * @param values Values to set.
     */
    public final void setValuesFrom(int startingColumn, Object... values) {
        csv.setValuesFrom(startingColumn, values);
    }

    /**
     * Set values starting from the first column in the current row.
     *
     * @param values Values to set.
     */
    public final void setValues(Object... values) {
        csv.setValues(values);
    }

    /**
     * Write out the current row.
     */
    public void writeRow() throws IOException {
        csv.writeRow();
    }

    private static class Report extends CSV {

        private CSVReport report;
        private boolean generated = false;

        public Report() {
            super();
        }

        public Report(int columnCount) {
            super(columnCount);
        }

        @Override
        public void setColumnCount(int columnCount) {
            if(generated) {
                throw new SORuntimeException("Cannot change column count after generation started");
            }
            super.setColumnCount(columnCount);
        }

        @Override
        public void generateContent() throws Exception {
            generated = true;
            report.generateContent();
        }

        @Override
        protected Writer getWriter() {
            return report.getWriter();
        }
    }
}