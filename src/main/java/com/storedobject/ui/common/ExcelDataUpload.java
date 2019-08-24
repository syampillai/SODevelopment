package com.storedobject.ui.common;

import com.storedobject.common.SOException;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.StyledBuilder;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

import java.util.ArrayList;

public abstract class ExcelDataUpload extends DataForm implements Transactional {

    public ExcelDataUpload(String caption) {
        super(caption);
    }

    @Override
    protected void buildButtons() {
    }

    /**
     * If additional fields are required, create it in this method.
     */
    @Override
    protected void buildFields() {
    }

    @Override
    protected final boolean process() {
        return true;
    }

    /**
     * Data boundary can be set using this method. For example: setDataBoundary("A5:H136"). The full rectangular boundary may not have to
     * be specified. Only the starting line can be specified and the rest will be detected automatically. For example: setBoundary("A5:H5").
     * If no gaps (blank cells) exist near the boundary area, you may even specify the starting cell. For example: setBoundary("A5"). If no
     * data boundary is set, it will try to detect automatically starting from cell "A1".
     *
     * @param rangeAddress Range address in Excel format.
     */
    public void setDataBoundary(String rangeAddress) {
    }

    public StyledBuilder blackMessage(Object any) {
        return null;
    }

    public StyledBuilder blueMessage(Object any) {
        return null;
    }

    public StyledBuilder redMessage(Object any) {
        return null;
    }

    /**
     * Implement this method for processing data. 'data' is already populated from the spreadsheet that was uploaded. The dimentions
     * of 'data' will be equal to the size of the data boundary defined.
     *
     * @param data Uploaded data
     */
    protected abstract void processData(ArrayList<Object[]> data);

    /**
     * Save all objects at the given index in the 'data'.
     *
     * @param index Index
     */
    public void saveObjects(int index) {
    }

    public int getExcelRow(int dataRow) {
        return 0;
    }

    public String getExcelColumn(int columnIndex) {
        return null;
    }

    public String getExcelCell(int dataRow, int columnIndex) {
        return null;
    }

    public boolean isMainDataRow(@SuppressWarnings("unused") int row) throws SOException {
        return true;
    }

    public void checkForNullObjects(int index) throws SOException {
    }

    public void checkForNullObjects(int index, boolean mainDataRowsOnly) throws SOException {
    }

    public void checkForDuplicates(int index) throws SOException {
    }

    public void checkForDuplicates(int index, boolean mainDataRowsOnly) throws SOException {
    }

    public void checkForDuplicates(int index, boolean mainDataRowsOnly, boolean ignoreEmpty) throws SOException {
    }

    /**
     * Fill blank data with the given value.
     * @param index Index of the data column to be filled.
     * @param value Value to fill the blank data.
     */
    public void fillBlankData(int index, String value) {
    }

    /**
     * Converts the given value to an object. The default implementation returns the value from the static method get(String), if available.
     * One may override this method to create the object on the fly.
     *
     * @param value Value to be converted.
     * @param objectClass Object class.
     * @param <T> Type of Stored object
     * @return Stored object.
     */
    public <T extends StoredObject> T getObject(String value, Class<T> objectClass) {
        return null;
    }

    /**
     * Convert data to another type. All data corresponding to the index will be converted.
     * @param index Index of the data to be converted.
     * @param type Class to which data to be converted.
     */
    public void convertData(int index, Class<?> type) {
    }

    public void convertData(int index, Class<?> type, boolean allowNulls, boolean mainRowsOnly) throws SOException {
    }

    public void packData(int index) {
    }

    /**
     * Convert data to 'choice value'. All data corresponding to the index will be converted.
     * @param index Index of the data to be converted.
     * @param objectClass Class to which data to be converted.
     * @param choiceFieldName Name of the Choice Field.
     */
    public void convertDataToChoice(int index, Class<? extends StoredObject> objectClass, String choiceFieldName) {
    }

    /**
     * Convert data to 'choice value'. All data corresponding to the index will be converted.
     * @param index Index of the data to be converted.
     * @param objectClass Class to which data to be converted.
     * @param choiceFieldName Name of the Choice Field.
     * @param mainRowsOnly Whether applicable to main rows only or not
     */
    public void convertDataToChoice(int index, Class<? extends StoredObject> objectClass, String choiceFieldName, boolean mainRowsOnly) {
    }

    public Object convertValue(Object value, Class<?> type) {
        return null;
    }

    public void convertCell(String cellName, Class<?> type) {
    }

    public void defineCell(String cellName, String cellAddress) {
    }

    public Object getCellValue(String cellName) {
        return null;
    }

    /**
     * Converts the given value to an object. The default implementation returns the value from the static method get(String), if
     * available - parameter to the 'get' method is created by converting 'value' to a 'string'. One may override this method
     * to create the object on the fly.
     *
     * @param value Value to be converted.
     * @param objectClass Object class.
     * @param <T> Type of Stored object
     * @return Stored object.
     */
    public <T extends StoredObject> T convertObject(Object value, Class<T> objectClass) {
        return null;
    }
}