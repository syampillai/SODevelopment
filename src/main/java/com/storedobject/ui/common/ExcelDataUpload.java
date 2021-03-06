package com.storedobject.ui.common;

import com.storedobject.common.SOException;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.office.Excel;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.StyledBuilder;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.UploadProcessorView;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Upload data contain in an Excel file and typically create entries in the system. The Excel file may contain
 * data rows in a specified area called "data boundary" and you can use the method {@link #setDataBoundary(String)}
 * to define the "data boundary". If the "data boundary" is not specified, the whole Excel sheet data will be
 * considered as the "data boundary". Processing of the data is done in the {@link #processData(ArrayList)} method
 * and data from the "data boundary" is passed to that method as a parameter.
 *
 * @author Syam
 */
public class ExcelDataUpload extends DataForm implements Transactional {

    private final static int MAX_SCAN = 1000;
    private final static Double ZERO = 0.0, ONE = 1.0;
    private boolean uploaded = false;
    private DataUpload dataUpload;
    private final ArrayList<Object[]> data = new ArrayList<>();
    private final Map<String, Object> cells = new HashMap<>();
    private int minimumRow = 0, minimumCell;

    /**
     * Constructor.
     *
     * @param caption Caption.
     */
    public ExcelDataUpload(String caption) {
        super(caption);
    }

    /**
     * Constructor. When created this way, it expects an Excel file containing class name in cell "A1",
     * attribute names in the second row (A2:...) and "data boundary" starting from cell "A3".
     */
    public ExcelDataUpload() {
        super("Excel Data Upload");
        add(new ELabel("Choose the Excel file to upload...", "blue"));
        defineCell("Class", "A1");
        defineCell("Attributes", "A2:ZZ2");
        setDataBoundary("A3");
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Upload");
        ok.setIcon(VaadinIcon.UPLOAD);
    }

    /**
     * If additional fields are required, create it in this method.
     */
    @Override
    protected void buildFields() {
    }

    /**
     * Process the data. This is where the processing happens internally and the extended classes should
     * override the {@link #processData(ArrayList)} method to do real processing of the data.
     *
     * @return True if the data was uploaded.
     */
    @Override
    protected final boolean process() {
        if(!uploaded) {
            dataUpload().execute(this);
            return false;
        }
        close();
        dataUpload.execute();
        dataUpload.message("");
        dataUpload.message("Processing...");
        if(getClass() == ExcelDataUpload.class) {
            processSOData();
        } else {
            processData(data);
        }
        dataUpload.message("Processing completed.");
        return true;
    }

    /**
     * Data boundary can be set using this method. For example: setDataBoundary("A5:H136"). The full rectangular boundary may not have to
     * be specified. Only the starting line needs to be specified and the rest will be detected automatically. For example: setBoundary("A5:H5").
     * If no gaps (blank cells) exist near the boundary area, you may even specify the starting cell. For example: setBoundary("A5"). If no
     * data boundary is set, it will try to detect automatically starting from cell "A1".
     *
     * @param rangeAddress Range address in Excel format.
     */
    public void setDataBoundary(String rangeAddress) {
        testBefore();
        dataUpload().setDataBoundary(rangeAddress);
    }

    private DataUpload dataUpload() {
        if(dataUpload == null) {
            dataUpload = new DataUpload();
        }
        return dataUpload;
    }

    /**
     * Show an informational message.
     *
     * @param any Message.
     * @return A style builder that can be used to append more messages.
     */
    public StyledBuilder blackMessage(Object any) {
        testAfter();
        return dataUpload.blackMessage(any);
    }

    /**
     * Show a highlighted message.
     *
     * @param any Message.
     * @return A style builder that can be used to append more messages.
     */
    public StyledBuilder blueMessage(Object any) {
        testAfter();
        return dataUpload.blueMessage(any);
    }

    /**
     * Show an error message.
     *
     * @param any Message.
     * @return A style builder that can be used to append more messages.
     */
    public StyledBuilder redMessage(Object any) {
        testAfter();
        return dataUpload.redMessage(any);
    }

    /**
     * Override this method for processing data. 'data' is already populated from the spreadsheet that was uploaded. The dimensions
     * of 'data' will be equal to the size of the data boundary defined.
     *
     * @param data Uploaded data
     */
    protected void processData(ArrayList<Object[]> data) {
    }

    @SuppressWarnings("unchecked")
    private void processSOData() {
        Class<? extends StoredObject> objectClass;
        try {
            objectClass = (Class<? extends StoredObject>) JavaClassLoader.getLogic((String) getCellValue("Class"));
        } catch(Throwable error) {
            redMessage("Not a valid class: " + getCellValue("Class"));
            return;
        }
        Transaction transaction = null;
        ArrayList<Object> attributes = (ArrayList<Object>) getCellValue("Attributes");
        StoredObject so;
        Object[] row;
        int a = -1;
        for(int r = 0; r < data.size(); r++) {
            row = data.get(r);
            try {
                so = objectClass.getDeclaredConstructor().newInstance();
                for(a = 0; a < attributes.size(); a++) {
                    so.setRawValue(attributes.get(a).toString(), row[a]);
                }
                if(transaction == null) {
                    transaction = getTransactionManager().createTransaction();
                }
                so.save(transaction);
            } catch(Throwable error) {
                if(transaction != null) {
                    transaction.rollback();
                    transaction = null;
                }
                StringBuilder e = new StringBuilder("Error in row");
                if(a >= 0 && a < attributes.size()) {
                    e.append(" (while setting ").append(attributes.get(a)).
                            append(" = ").append(row[a]).append(")");
                }
                e.append(": ");
                if(a < 0) {
                    e.append(getExcelRow(r));
                } else {
                    e.append(getExcelCell(r, a));
                }
                redMessage(e);
                redMessage(error);
                getApplication().log(e, error);
                break;
            }
        }
        if(transaction != null) {
            try {
                transaction.commit();
            } catch(Exception e) {
                redMessage(e);
            }
        }
    }

    /**
     * Save all objects at the given index in the 'data'.
     *
     * @param index Index
     */
    public void saveObjects(int index) {
        testAfter();
        transact(t -> {
            for(Object[] d: data) {
                if(d[index] instanceof StoredObject) {
                    ((StoredObject)d[index]).save(t);
                }
            }
        });
    }

    /**
     * Get the Excel row number for the given data row.
     * (Used for locating the some specific cell for debugging/displaying).
     *
     * @param dataRow Data row index.
     * @return Excel's row number.
     */
    public int getExcelRow(int dataRow) {
        return dataRow + minimumRow + 1;
    }

    /**
     * Get the column name of the Excel cell address for the given data row.
     * (Used for locating the some specific cell for debugging/displaying).
     *
     * @param columnIndex Data column index.
     * @return Column name of the Excel cell address.
     */
    public String getExcelColumn(int columnIndex) {
        CellReference cr = new CellReference(0, columnIndex + minimumCell);
        String[] s = cr.getCellRefParts();
        return s[2];
    }

    /**
     * Get the name of the excel cell address for the given data row.
     * (Used for locating the some specific cell for debugging/displaying).
     *
     * @param dataRow Data row index.
     * @param columnIndex Data column index.
     * @return Name of the Excel cell address.
     */
    public String getExcelCell(int dataRow, int columnIndex) {
        CellReference cr = new CellReference(dataRow + minimumRow, columnIndex + minimumCell);
        String[] s = cr.getCellRefParts();
        return s[2] + s[1];
    }

    /**
     * Check whether the given row is a main data row or not. By default every data row is considered as
     * main data row. However, an extended class can override this.
     *
     * @param row Row number.
     * @return True/false.
     * @throws SOException If an error occurs while determining the row status.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isMainDataRow(int row) throws SOException {
        return true;
    }

    /**
     * Check whether a specific data column contains null {@link StoredObject} or not.
     *
     * @param index Index to the data.
     * @throws SOException If the cell contains invalid data.
     */
    public void checkForNullObjects(int index) throws SOException {
        checkForNullObjects(index, false);
    }

    /**
     * Check whether a specific data column contains null {@link StoredObject} or not.
     *
     * @param index Index to the data.
     * @param mainDataRowsOnly Whether to check in main data rows only or not.
     * @throws SOException If the cell contains invalid data.
     */
    public void checkForNullObjects(int index, boolean mainDataRowsOnly) throws SOException {
        testAfter();
        int i = -1;
        for(Object[] objects: data) {
            ++i;
            if(mainDataRowsOnly && !isMainDataRow(i)) {
                continue;
            }
            if((objects[index] instanceof Id) && !Id.isNull((Id)objects[index])) {
                continue;
            }
            throw new SOException("Invalid data, Cell: " + getExcelCell(i, index));
        }
    }

    /**
     * Check whether a specific data column contains duplicate values or not.
     *
     * @param index Index to the data.
     * @throws SOException If the cell contains invalid data.
     */
    public void checkForDuplicates(int index) throws SOException {
        checkForDuplicates(index, false);
    }

    /**
     * Check whether a specific data column contains duplicate values or not.
     *
     * @param index Index to the data.
     * @param mainDataRowsOnly Whether to check in main data rows only or not.
     * @throws SOException If the cell contains invalid data.
     */
    public void checkForDuplicates(int index, boolean mainDataRowsOnly) throws SOException {
        checkForDuplicates(index, mainDataRowsOnly, false);
    }

    /**
     * Check whether a specific data column contains duplicate values or not.
     *
     * @param index Index to the data.
     * @param mainDataRowsOnly Whether to check in main data rows only or not.
     * @param ignoreEmpty Whether to ignore empty values or not.
     * @throws SOException If the cell contains invalid data.
     */
    public void checkForDuplicates(int index, boolean mainDataRowsOnly, boolean ignoreEmpty) throws SOException {
        testAfter();
        int i = -1;
        for(Object[] objects: data) {
            ++i;
            if(mainDataRowsOnly && !isMainDataRow(i)) {
                continue;
            }
            if(!(objects[index] instanceof String)) {
                throw new SOException("Invalid data type, Cell: " + getExcelCell(i, index) + " (must contain text only)");
            }
            objects[index] = ((String)objects[index]).trim();
        }
        String s1, s2;
        i = -1;
        int k;
        for(Object[] objects: data) {
            ++i;
            if(mainDataRowsOnly && !isMainDataRow(i)) {
                continue;
            }
            s1 = (String)objects[index];
            if(s1.isEmpty() && ignoreEmpty) {
                continue;
            }
            for(k = i + 1; k < data.size(); k++) {
                s2 = (String)data.get(k)[index];
                if(s1.equals(s2)) {
                    throw new SOException("Duplicate data, Cell: " + getExcelCell(i, index) + " & " + getExcelCell(k, index));
                }
            }
        }
    }

    /**
     * Fill blank data with the given value.
     * @param index Index of the data column to be filled.
     * @param value Value to fill the blank data.
     */
    public void fillBlankData(int index, String value) {
        testAfter();
        if(data.size() == 0) {
            return;
        }
        convertData(index, String.class);
        data.forEach(d -> {
            if(((String)d[index]).isEmpty()) d[index] = value;
        });
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
        return StoredObjectUtility.retrieveObject(objectClass, value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Converter createConverter(Class<?> type) {
        if(type == boolean.class || type == Boolean.class) {
            return new BooleanConverter();
        } else if(type == double.class || type == Double.class) {
            return new NumberConverter();
        } else if(type == int.class || type == Integer.class) {
            return new IntegerConverter();
        } else if(type == String.class) {
            return new StringConverter();
        } else if(StoredObject.class.isAssignableFrom(type)) {
            return new ObjectConverter(type);
        } else if(type == ComputedDate.class) {
            return new ComputedDateConverter();
        } else if(java.util.Date.class.isAssignableFrom(type)) {
            return new DateConverter();
        } else if(type == ComputedDouble.class) {
            return new ComputedDoubleConverter();
        } else if(type == ComputedMinute.class) {
            return new MinutesConverter();
        }
        throw new SORuntimeException("Don't know how to convert to " + type);
    }

    /**
     * Convert data to another type. All data corresponding to the index will be converted.
     *
     * @param index Index of the data to be converted.
     * @param type Class to which data to be converted.
     */
    public void convertData(int index, Class<?> type) {
        try {
            convertData(index, type, true, false);
        } catch(SOException ignored) {
        }
    }

    /**
     * Convert data to another type.
     *
     * @param index Index of the data to be converted.
     * @param type Class to which data to be converted.
     * @param allowNulls Whether to allow nulls or not.
     * @param mainRowsOnly Whether to consider in main data rows only or not.
     */
    public void convertData(int index, Class<?> type, boolean allowNulls, boolean mainRowsOnly) throws SOException {
        convertData(index, createConverter(type), allowNulls, mainRowsOnly);
    }

    public void packData(int index) {
        try {
            convertData(index, new PackedStringConverter(), true, false);
        } catch(SOException e) {
            throw new SORuntimeException(e.getMessage());
        }
    }

    private void convertData(int index, Converter c, boolean allowNulls, boolean mainRowsOnly) throws SOException {
        testAfter();
        if(data.size() == 0) {
            return;
        }
        Object o;
        int row = -1;
        for(Object[] d: data) {
            ++row;
            if(mainRowsOnly && !isMainDataRow(row)) {
                continue;
            }
            try {
                o = c.convert(d[index]);
            } catch(SORuntimeException re ) {
                throw new SORuntimeException("Cell: " + getExcelCell(row, index) + ", " + re.getMessage());
            } catch (Exception e) {
                if(!allowNulls) {
                    throw new SOException("Invalid value at Cell: " + getExcelCell(row, index));
                }
                o = c.nullValue();
            }
            d[index] = o;
        }
    }

    /**
     * Convert data to 'choice value'. All data corresponding to the index will be converted.
     *
     * @param index Index of the data to be converted.
     * @param objectClass Class to which data to be converted.
     * @param choiceFieldName Name of the Choice Field.
     */
    public void convertDataToChoice(int index, Class<? extends StoredObject> objectClass, String choiceFieldName) {
        convertDataToChoice(index, objectClass, choiceFieldName, false);
    }

    /**
     * Convert data to 'choice value'. All data corresponding to the index will be converted.
     *
     * @param index Index of the data to be converted.
     * @param objectClass Class to which data to be converted.
     * @param choiceFieldName Name of the Choice Field.
     * @param mainRowsOnly Whether applicable to main rows only or not
     */
    public void convertDataToChoice(int index, Class<? extends StoredObject> objectClass, String choiceFieldName, boolean mainRowsOnly) {
        testAfter();
        if(data.size() == 0) {
            return;
        }
        Method m;
        String[] choices = null;
        try {
            m = objectClass.getMethod("get" + choiceFieldName + "Values");
            int mod = m.getModifiers();
            if(Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                choices = (String[])m.invoke(null, (Object[])null);
            } else {
                m = null;
            }
        } catch(Exception e) {
            m = null;
        }
        if(m == null) {
            throw new SORuntimeException("Invalid field name '" + choiceFieldName + "'");
        }
        Converter c = new ChoiceConverter(choices);
        int row = -1;
        for(Object[] d: data) {
            ++row;
            if(mainRowsOnly) {
                try {
                    if(!isMainDataRow(row)) {
                        continue;
                    }
                } catch (SOException e) {
                    throw new SORuntimeException("Cell: " + getExcelCell(row, index) + ", please check data", e);
                }
            }
            try {
                d[index] = c.convert(d[index]);
            } catch (Exception e) {
                d[index] = 0;
            }
        }
    }

    private void testBefore() {
        if(uploaded) {
            throw new SORuntimeException("Data already uploaded, this can not be done at this stage");
        }
    }

    private void testAfter() {
        if(!uploaded) {
            throw new SORuntimeException("Data not yet uploaded");
        }
    }

    /**
     * Convert a given value to a specific type.
     *
     * @param value Value to convert.
     * @param type Class of the type of the result required.
     * @return Converted value or null if conversion is not possible.
     * @param <O> type of result.
     */
    public <O> Object convertValue(Object value, Class<O> type) {
        Converter c = createConverter(type);
        try {
            return c.convert(value);
        } catch (Exception ignored) {
        }
        return c.nullValue();
    }

    /**
     * Convert the value corresponding to a cell name variable defined via {@link #defineCell(String, String)}.
     * This can be used only after data is populated.
     *
     * @param cellName Cell name.
     * @param type Type to which data needs to be converted.
     */
    public void convertCell(String cellName, Class<?> type) {
        testAfter();
        if(!cells.containsKey(cellName)) {
            throw new SORuntimeException("Cell '" + cellName + "' not found");
        }
        cells.put(cellName, convertValue(cells.get(cellName), type));
    }

    /**
     * Define a cell name. A cell name acts like a "variable" that points to an Excel cell. The value can
     * be retrieved via {@link #getCellValue(String)}. This should be used before the data is populated.
     *
     * @param cellName Cell name to be defined.
     * @param cellAddress Excel cell address to which this variable should be mapped.
     */
    public void defineCell(String cellName, String cellAddress) {
        testBefore();
        cells.put(cellName, cellAddress);
    }

    /**
     * Get the value corresponding to a cell name variable defined via {@link #defineCell(String, String)}.
     * This can be used only after data is populated.
     *
     * @param cellName Cell name.
     * @return Value.
     */
    public Object getCellValue(String cellName) {
        testAfter();
        if(!cells.containsKey(cellName)) {
            throw new SORuntimeException("Cell '" + cellName + "' was not defined");
        }
        Object v = cells.get(cellName);
        if(v instanceof SORuntimeException) {
            throw (SORuntimeException)v;
        }
        return v;
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
        String v;
        try {
            v = (String) createConverter(String.class).convert(value);
        } catch (Exception e) {
            v = "";
        }
        return getObject(v, objectClass);
    }

    /**
     * Whether numeric values from the Excel should be formatted while populating the data or not.
     *
     * @return True/false. Default is false means numeric values are converted and will appear as String values
     * in the data passed to {@link #processData(ArrayList)}.
     */
    public boolean populateNumericValuesUntouched() {
        return false;
    }

    private interface Converter {

        Object convert(Object v) throws Exception;
        Object nullValue();
    }

    private static class BooleanConverter implements Converter {

        @Override
        public Object convert(Object v) throws Exception {
            if(v == null) {
                throw new Exception();
            }
            if(v instanceof Boolean) {
                return v;
            }
            if(v instanceof String) {
                switch (((String)v).trim().toLowerCase()) {
                    case "true":
                    case "yes":
                    case "ok":
                    case "1":
                        return true;
                }
                return false;
            }
            if(v instanceof Number) {
                return ((Number)v).intValue() > 0;
            }
            throw new Exception();
        }

        @Override
        public Object nullValue() {
            return false;
        }
    }

    private static class DateConverter implements Converter {

        @Override
        public Date convert(Object v) throws Exception {
            if(v instanceof Date) {
                return (Date)v;
            }
            if(v instanceof java.util.Date) {
                return DateUtility.create((java.util.Date)v);
            }
            if(v instanceof String && ((String)v).isEmpty()) {
                Date d = DateUtility.create((String)v);
                if(d != null) {
                    return d;
                }
            }
            throw new SORuntimeException("Can not convert value " + v + " to date");
        }

        @Override
        public Object nullValue() {
            return DateUtility.today();
        }
    }

    private static class ComputedDateConverter extends DateConverter {

        @Override
        public ComputedDate convert(Object v) throws Exception {
            return new ComputedDate(super.convert(v));
        }

        @Override
        public Object nullValue() {
            return new ComputedDate();
        }
    }

    private static class NumberConverter implements Converter {

        @Override
        public Object convert(Object v) throws Exception {
            if(v instanceof Number) {
                return ((Number) v).doubleValue();
            }
            if(v == null) {
                throw new Exception();
            }
            if(v instanceof Boolean) {
                return ((Boolean)v) ? ONE : ZERO;
            }
            if(v instanceof String) {
                try {
                    return Double.parseDouble((String)v);
                } catch(NumberFormatException ignored) {
                }
            }
            throw new Exception();
        }

        @Override
        public Object nullValue() {
            return ZERO;
        }
    }

    private static class ComputedDoubleConverter extends NumberConverter {

        @Override
        public ComputedDouble convert(Object v) throws Exception {
            return new ComputedDouble((Double)super.convert(v));
        }

        @Override
        public ComputedDouble nullValue() {
            return new ComputedDouble();
        }
    }

    private static class MinutesConverter extends NumberConverter {

        @Override
        public Object convert(Object v) throws Exception {
            Number t;
            try {
                t = (Number) super.convert(v);
            } catch(Throwable error) {
                throw new Exception();
            }
            return time(t.doubleValue());
        }

        @Override
        public Object nullValue() {
            return new ComputedMinute();
        }

        private static ComputedMinute time(double t) {
            long v = Math.round(t * 100.0);
            int m = (int)(v % 100);
            if(m >= 60) {
                throw new SORuntimeException("Invalid time (hh.mm) specified");
            }
            int h = (int)(v / 100);
            return new ComputedMinute((h * 60) + m);
        }
    }

    private static class IntegerConverter extends NumberConverter {

        @Override
        public Object convert(Object v) throws Exception {
            return ((Number)super.convert(v)).intValue();
        }

        @Override
        public Object nullValue() {
            return 0;
        }
    }

    private static class StringConverter implements Converter {

        @Override
        public String convert(Object v) {
            return StringUtility.toString(v).trim();
        }

        @Override
        public String nullValue() {
            return "";
        }
    }

    private static class PackedStringConverter extends StringConverter {

        @Override
        public String convert(Object v) {
            return StringUtility.pack((super.convert(v)));
        }
    }

    private class ObjectConverter<T extends StoredObject> implements Converter {

        private final Class<T> objectClass;

        private ObjectConverter(Class<T> objectClass) {
            this.objectClass = objectClass;
        }

        @Override
        public Object convert(Object v) throws Exception {
            if(v == null) {
                throw new Exception();
            }
            T so = convertObject(v, objectClass);
            if(so == null && v instanceof Id) {
                throw new Exception();
            }
            if(so != null) {
                Id id = so.getId();
                return Id.isNull(id) ? so : id;
            }
            if(!((String) convertValue(v, String.class)).isEmpty()) {
                throw new SORuntimeException("Can not convert '" + v + "' to " + StringUtility.makeLabel(objectClass));
            }
            return null;
        }

        @Override
        public Object nullValue() {
            return Id.ZERO;
        }
    }

    private static class ChoiceConverter implements Converter {

        private final StringList choices;

        private ChoiceConverter(String[] choices) {
            this.choices = StringList.create(choices);
        }

        @Override
        public Object convert(Object v) throws Exception {
            int i = -1;
            if(v instanceof Number) {
                i = ((Number)v).intValue();
            } else if(v == null) {
                throw new Exception();
            } else if(v instanceof String) {
                String s = v.toString().trim().toLowerCase();
                if(s.isEmpty()) {
                    throw new Exception();
                }
                i = choices.indexOf(e -> e.equalsIgnoreCase(s), false);
                if(i < 0) {
                    i = choices.indexOf(e -> e.toLowerCase().startsWith(s), true);
                }
            }
            if(i >= 0 && i < choices.size()) {
                return i;
            }
            throw new SORuntimeException("Invalid choice '" + v + "', must be one of: " + choices.toString(", "));
        }

        @Override
        public Object nullValue() {
            return 0;
        }
    }

    private class DataUpload extends UploadProcessorView {

        private Workbook workbook = null;
        private final int sheet = 0;
        private int maximumRow = -1;
        private int maximumCell = -1;
        private CellRangeAddress boundary;
        private boolean convertNumeric;

        private DataUpload() {
            super(ExcelDataUpload.this.getCaption(), "File");
        }

        private void setDataBoundary(String rangeAddress) {
            if(!Excel.validateRangeAddress(rangeAddress) && !Excel.validateCellAddress(rangeAddress)) {
                minimumRow = minimumCell = maximumRow = maximumCell = -1;
                return;
            }
            boundary = CellRangeAddress.valueOf(rangeAddress);
            if(workbook != null) {
                preprocessData();
            }
        }

        @Override
        protected void process(InputStream data, String mimeType) {
            convertNumeric = !populateNumericValuesUntouched();
            try {
                workbook = WorkbookFactory.create(data);
            } catch(IOException e) {
                redMessage("Error uploading file");
                workbook = null;
                return;
            }
            if(!preprocessData()) {
                return;
            }
            int r1, c1, r2, c2;
            String a;
            Object v;
            CellRangeAddress cr;
            for(String cellName: cells.keySet()) {
                a = (String)cells.get(cellName);
                if(!Excel.validateCellAddress(a) && !Excel.validateRangeAddress(a)) {
                    cells.put(cellName, new SORuntimeException("Invalid Cell Address '" + a + "'"));
                    continue;
                }
                cr = CellRangeAddress.valueOf(a);
                r1 = cr.getFirstRow();
                c1 = cr.getFirstColumn();
                r2 = cr.getLastRow();
                c2 = cr.getLastColumn();
                if(r1 == r2 && c1 == c2) {
                    v = getCellData(getSheet(sheet).getRow(r1).getCell(c1));
                } else if(r1 == r2) {
                    ArrayList<Object> columnValues = new ArrayList<>();
                    for(int c = c1; c <= c2; c++) {
                        v = getCellData(getSheet(sheet).getRow(r1).getCell(c));
                        if(v == null) {
                            break;
                        }
                        columnValues.add(v);
                    }
                    v = columnValues;
                } else if(c1 == c2) {
                    ArrayList<Object> rowValues = new ArrayList<>();
                    for(int r = r1; r <= r2; r++) {
                        v = getCellData(getSheet(sheet).getRow(r).getCell(c1));
                        if(v == null) {
                            break;
                        }
                        rowValues.add(v);
                    }
                    v = rowValues;
                } else {
                    ArrayList<Object> columnValues;
                    ArrayList<ArrayList<Object>> rowValues = new ArrayList<>();
                    for(int r = r1; r <= r2; r++) {
                        columnValues = new ArrayList<>();
                        for(int c = c1; c <= c2; c++) {
                            v = getCellData(getSheet(sheet).getRow(r1).getCell(c));
                            if(v == null) {
                                break;
                            }
                            columnValues.add(v);
                        }
                        if(columnValues.isEmpty()) {
                            break;
                        }
                        rowValues.add(columnValues);
                    }
                    v = rowValues;
                }
                cells.put(cellName, v);
                blackMessage(cellName + " (from " + a + ") = " + v);
            }
            processData();
        }

        private Sheet getSheet(int sheet) {
            Sheet s = null;
            try {
                s = workbook.getSheetAt(sheet);
            } catch(Throwable ignored) {
            }
            return s;
        }

        private Sheet createSheet(int sheet) {
            Sheet s = getSheet(sheet);
            if(s != null) {
                return s;
            }
            try {
                workbook.createSheet();
            } catch(Throwable ignored) {
            }
            return createSheet(sheet);
        }

        private Row getRow(int row) {
            Sheet s = getSheet(sheet);
            return s == null ? null : s.getRow(row);
        }

        private Row createRow(int row) {
            Row r = getRow(row);
            return r == null ? createSheet(sheet).createRow(row) : r;
        }

        private Cell getCell(int cell, int row) {
            Row r = getRow(row);
            if(r == null) {
                return null;
            }
            Cell c = r.getCell(cell);
            return c == null || c.getCellType() == CellType.BLANK ? null : c;
        }

        private Cell createCell(int cell, int row) {
            Cell c = getCell(cell, row);
            return c == null ? createRow(row).createCell(cell) : c;
        }

        private boolean cellNotEmpty(int cell, int row) {
            return getCell(cell, row) != null;
        }

        private boolean preprocessData() {
            if(boundary != null) {
                minimumRow = boundary.getFirstRow();
                maximumRow = boundary.getLastRow();
                minimumCell = boundary.getFirstColumn();
                maximumCell = boundary.getLastColumn();
            }
            if(minimumRow < 0) {
                minimumRow = findFilledRow();
            }
            if(minimumCell < 0) {
                minimumCell = findFilledColumn();
            }
            if(maximumRow < 0 || maximumRow == minimumRow) {
                maximumRow = findUnfilledRow();
            }
            if(maximumCell < 0 || maximumCell == minimumCell) {
                maximumCell = findUnfilledColumn();
            }
            if(minimumRow < 0 || maximumRow < 0 || minimumCell < 0 || maximumCell < 0) {
                redMessage("Could not identify data boundary");
                return false;
            }
            blueMessage("Data Boundary: " + createCell(minimumCell, minimumRow).getAddress() + ":" +
                    createCell(maximumCell, maximumRow).getAddress());
            return true;
        }

        private int findFilledRow() {
            int r, c = minimumCell;
            if(c < 0) {
                c = 0;
            }
            for(; c < MAX_SCAN; c++) {
                for(r = 0; r < MAX_SCAN; r++) {
                    if(cellNotEmpty(c, r)) {
                        return r;
                    }
                }
            }
            return -1;
        }

        private int findFilledColumn() {
            int r = minimumRow, c;
            if(r < 0) {
                r = 0;
            }
            for(; r < MAX_SCAN; r++) {
                for(c = 0; c < MAX_SCAN; c++) {
                    if(cellNotEmpty(c, r)) {
                        return c;
                    }
                }
            }
            return -1;
        }

        private int findUnfilledRow() {
            if(minimumRow < 0 || minimumCell < 0) {
                return -1;
            }
            int r = minimumRow + 1, c;
            boolean found;
            while(true) {
                found = false;
                for(c = minimumCell; c < MAX_SCAN; c++) {
                    if(cellNotEmpty(c, r)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    return r - 1;
                }
                r++;
            }
        }

        private int findUnfilledColumn() {
            if(minimumRow < 0 || maximumRow < 0 || minimumCell < 0) {
                return -1;
            }
            int r, c = minimumCell + MAX_SCAN;
            boolean found;
            while(true) {
                found = false;
                for(r = minimumRow; r <= maximumRow; r++) {
                    if(cellNotEmpty(c, r)) {
                        found = true;
                        break;
                    }
                }
                if(found) {
                    return c;
                }
                c--;
            }
        }

        private Object getCellData(Cell cell) {
            if(cell != null) {
                CellType cellType = cell.getCellType();
                if(cellType == CellType.FORMULA) {
                    cellType = cell.getCachedFormulaResultType();
                }
                switch(cellType) {
                    case BOOLEAN:
                        return cell.getBooleanCellValue();
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        if(DateUtil.isCellDateFormatted(cell)) {
                            return cell.getDateCellValue();
                        }
                        if(convertNumeric) {
                            DataFormatter df = new DataFormatter();
                            return df.formatCellValue(cell);
                        }
                        return cell.getNumericCellValue();
                    default:
                        return null;
                }
            }
            return null;
        }

        private void readRowData(int row) {
            Object[] values = new Object[maximumCell - minimumCell + 1];
            for(int c = minimumCell; c <= maximumCell; c++) {
                values[c - minimumCell] = getCellData(getCell(c, row));
            }
            data.add(values);
        }

        private void processData() {
            blackMessage("Reading data...");
            for(int r = minimumRow; r <= maximumRow; r++) {
                readRowData(r);
            }
            uploaded = true;
            getApplication().access(() -> {
                ok.setText("Process");
                ok.setIcon(new Icon(VaadinIcon.TOOLS));
            });
            newLine();
            redMessage("Please close this after reading the messages.");
            blueMessage("Number of rows read from the file: " + data.size());
            blueMessage("You may now process the data for creating entries in the system.");
        }
    }
}