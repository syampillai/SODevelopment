package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.StreamContentProducer;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public abstract class AbstractSpreadSheet extends StreamContentProducer {

    public static int MAX_ROWS = 65635;
    protected Workbook workbook;

    /**
     * Constructor.
     *
     * @param in  Input stream containing a spreadsheet file with some content.
     * @param out Output is written to this stream.
     */
    public AbstractSpreadSheet(InputStream in, OutputStream out) {
    }

    protected abstract Workbook createWorkbook(InputStream in) throws Exception;

    public void save(OutputStream out) throws IOException {
    }

    public void save(String fileName) throws IOException {
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public int getSheetIndex() {
        return -1;
    }

    public Sheet getSheet() {
        return null;
    }

    public Sheet getSheet(int sheet) {
        return null;
    }

    public Sheet getNextSheet() {
        return null;
    }

    public Sheet getPreviousSheet() {
        return null;
    }

    public int getRowIndex() {
        return 0;
    }

    public Row getRow() {
        return null;
    }

    public Row getRow(int row) {
        return null;
    }

    public Row getRow(int row, int sheet) {
        return null;
    }

    public Row getNextRow() {
        return null;
    }

    public Row getPreviousRow() {
        return null;
    }

    public boolean moveLeft() {
        return true;
    }

    public void moveRight() {
    }

    public boolean moveUp() {
        return true;
    }

    public void moveDown() {
    }

    public int getCellIndex() {
        return 0;
    }

    public Cell getCell() {
        return null;
    }

    public Cell getCell(int cell, int row) {
        return null;
    }

    public Cell getCell(int cell, int row, int sheet) {
        return null;
    }

    public Cell getCell(String cellAddress) {
        return null;
    }

    public Cell[][] getCells(String rangeAddress) {
        return null;
    }

    public Cell[] getColumnCells(String rangeAddress) {
        return null;
    }

    public Cell[] getRowCells(String rangeAddress) {
        return null;
    }

    public Cell getNextCell() {
        return null;
    }

    public Cell getPreviousCell() {
        return null;
    }

    public void goToCell(String cellAddress) {
    }

    public void goToCell(int cell, int row, int sheet) {
    }

    public void goToCell(int cell, int row) {
    }

    public void goToCell(int cell) {
    }

    public void goToRow(int row) {
    }

    /**
     * Set a value to the current cell.
     * If the value can't be set because the type is not supported, its stringified version will be set.
     *
     * @param cellValue Value to set.
     */
    public void setCellValue(Object cellValue) {
    }

    /**
     * Set cell values to consecutive cells from left to right, starting from the current cell.
     *
     * @param cellValues Cell values to set.
     */
    public void setCellValues(Object... cellValues) {
        if(cellValues != null) {
            for(Object v: cellValues) {
                setCellValue(v);
                getNextCell();
            }
        }
    }

    /**
     * Set a value to a cell. If the value can't be set because the type is not supported, its stringified version
     * will be set.
     *
     * @param cell Cell to which value needs to be set.
     * @param cellValue Value to set.
     */
    public void setCellValue(Cell cell, Object cellValue) {
    }

    /*
     * Generate the content of the excel in this method. Variable 'workbook' (which is of type Workbook is available here).
     */
    @SuppressWarnings("RedundantThrows")
    @Override
    public void generateContent() throws Exception {
    }

    public CellStyle getDateStyle() {
        return workbook.createCellStyle();
    }

    public CellStyle getDateTimeStyle() {
        return workbook.createCellStyle();
    }

    public CellStyle getHourStyle() {
        return workbook.createCellStyle();
    }

    public CellStyle getPercentageStyle() {
        return workbook.createCellStyle();
    }

    public CellStyle getPercentage2Style() {
        return workbook.createCellStyle();
    }

    public CellStyle getNumericStyle(int width, int decimals) {
        return workbook.createCellStyle();
    }

    public CellStyle getNumericStyle(int width, int decimals, boolean separated) {
        return workbook.createCellStyle();
    }

    public CellStyle getRightAlignedStyle() {
        return workbook.createCellStyle();
    }

    public CellStyle getCenteredStyle() {
        return workbook.createCellStyle();
    }

    public static boolean validateCellAddress(String cellAddress) {
        return false;
    }

    public static boolean validateRangeAddress(String rangeAddress) {
        return false;
    }

    public static boolean validateRowAddress(String rangeAddress) {
        return false;
    }

    public static boolean validateColumnAddress(String rangeAddress) {
        return false;
    }

    public static boolean isSameSize(String rangeAddress1, String rangeAddress2) {
        return false;
    }

    /**
     * Re-evaluate all formula in the worksheet.
     */
    public abstract void recompute();

    public Device getDevice() {
        return null;
    }
}
