package com.storedobject.office.od.ods;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Iterator;

public class Row implements org.apache.poi.ss.usermodel.Row {

    Row(Sheet sheet, int rowIndex) {
    }

    @Override
    public Cell createCell(int index) {
        return createCell(index, null);
    }

    @Override
    public Cell createCell(int index, CellType cellType) {
        return null;
    }

    @Override
    public void removeCell(Cell cell) {
    }

    @Override
    public void setRowNum(int i) {
    }

    @Override
    public int getRowNum() {
        return 0;
    }

    @Override
    public Cell getCell(int index) {
        return getCell(index, null);
    }

    @Override
    public Cell getCell(int index, MissingCellPolicy missingCellPolicy) {
        return null;
    }

    @Override
    public short getFirstCellNum() {
        return 0;
    }

    @Override
    public short getLastCellNum() {
        return 0;
    }

    @Override
    public int getPhysicalNumberOfCells() {
        return 0;
    }

    @Override
    public void setHeight(short height) {
    }

    @Override
    public void setZeroHeight(boolean b) {
    }

    @Override
    public boolean getZeroHeight() {
        return false;
    }

    @Override
    public void setHeightInPoints(float points) {
    }

    @Override
    public short getHeight() {
        return 0;
    }

    @Override
    public float getHeightInPoints() {
        return 0;
    }

    @Override
    public boolean isFormatted() {
        return false;
    }

    @Override
    public CellStyle getRowStyle() {
        return null;
    }

    @Override
    public void setRowStyle(CellStyle cellStyle) {
    }

    @Override
    public Iterator<Cell> cellIterator() {
        return iterator();
    }

    @Override
    public Sheet getSheet() {
        return null;
    }

    @Override
    public int getOutlineLevel() {
        return 0;
    }

    @Override
    public void shiftCellsRight(int i, int i1, int i2) {
    }

    @Override
    public void shiftCellsLeft(int i, int i1, int i2) {
    }

    @Override
    public Iterator<Cell> iterator() {
        return null;
    }

    public void dispose() {
    }
}
