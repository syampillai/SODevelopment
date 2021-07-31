package com.storedobject.office.od.ods;

import com.storedobject.common.SORuntimeException;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Cell implements org.apache.poi.ss.usermodel.Cell {

    Cell(Row row, int cellIndex) throws IndexOutOfBoundsException {
    }

    @Deprecated
    @Override
    public void setCellType(CellType cellType) {
        throw new SORuntimeException("Unsupported");
    }

    @Override
    public void setBlank() {
        setCellValue("");
    }

    @Override
    public CellType getCellType() {
        return CellType._NONE;
    }

    @Override
    public CellType getCachedFormulaResultType() {
        return null;
    }

    @Override
    public void setCellValue(double value) {
    }

    @Override
    public void setCellValue(Date date) {
    }

    @Override
    public void setCellValue(LocalDateTime localDateTime) {
    }

    @Override
    public void setCellValue(Calendar calendar) {
    }

    @Override
    public void setCellValue(RichTextString richTextString) {
    }

    @Override
    public void setCellValue(String value) {
    }

    @Override
    public void setCellFormula(String formula) throws FormulaParseException, IllegalStateException {
    }

    @Override
    public void removeFormula() throws IllegalStateException {
    }

    @Override
    public String getCellFormula() {
        return "";
    }

    @Override
    public double getNumericCellValue() {
        return 0;
    }

    @Override
    public Date getDateCellValue() {
        return null;
    }

    @Override
    public LocalDateTime getLocalDateTimeCellValue() {
        return null;
    }

    @Override
    public RichTextString getRichStringCellValue() {
        return null;
    }

    @Override
    public String getStringCellValue() {
        return "";
    }

    @Override
    public void setCellValue(boolean b) {

    }

    @Override
    public void setCellErrorValue(byte b) {
    }

    @Override
    public boolean getBooleanCellValue() {
        return false;
    }

    @Override
    public byte getErrorCellValue() {
        return 0;
    }

    @Override
    public void setCellStyle(CellStyle cellStyle) {

    }

    @Override
    public CellStyle getCellStyle() {
        return null;
    }

    @Override
    public void setAsActiveCell() {

    }

    @Override
    public CellAddress getAddress() {
        return new CellAddress(0, 0);
    }

    @Override
    public void setCellComment(Comment comment) {

    }

    @Override
    public Comment getCellComment() {
        return null;
    }

    @Override
    public void removeCellComment() {

    }

    @Override
    public Hyperlink getHyperlink() {
        return null;
    }

    @Override
    public void setHyperlink(Hyperlink hyperlink) {

    }

    @Override
    public void removeHyperlink() {

    }

    @Override
    public CellRangeAddress getArrayFormulaRange() {
        return null;
    }

    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return false;
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public int getRowIndex() {
        return 0;
    }

    public Sheet getSheet() {
        return new Sheet();
    }

    @Override
    public Row getRow() {
        return new Row(new Sheet(), 0);
    }

    public void dispose() {
    }
}