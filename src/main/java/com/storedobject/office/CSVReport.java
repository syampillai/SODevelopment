package com.storedobject.office;

import com.storedobject.common.CSV;
import com.storedobject.core.*;

import java.io.IOException;

public class CSVReport extends TextReport {

    /**
     * Constructor.
     *
     * @param device Device
     * @param columnCount Column count.
     */
    public CSVReport(Device device, int columnCount) {
        super(device, new CSV(columnCount) {
            @Override
            public void generateContent() throws Exception {
            }
        });
    }

    /**
     * Set a value at a specific column in the current row.
     *
     * @param column Column,
     * @param value Value to set.
     */
    public final void setValue(int column, Object value) {
    }

    /**
     * Set values starting from a specific column in the current row.
     *
     * @param startingColumn Column,
     * @param values Values to set.
     */
    public final void setValuesFrom(int startingColumn, Object... values) {
    }

    /**
     * Set values starting from the first column in the current row.
     *
     * @param values Values to set.
     */
    public final void setValues(Object... values) {
    }

    /**
     * Write out the current row.
     */
    public void writeRow() throws IOException {
    }
}