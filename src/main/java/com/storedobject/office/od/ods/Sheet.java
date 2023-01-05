package com.storedobject.office.od.ods;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;

import java.util.*;

public class Sheet implements org.apache.poi.ss.usermodel.Sheet {

    Sheet() {
    }

    public Workbook getWorkbook() {
        return null;
    }

    @Override
    public String getSheetName() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public CellRange<? extends org.apache.poi.ss.usermodel.Cell> setArrayFormula(String s, CellRangeAddress cellRangeAddress) {
        return null;
    }

    @Override
    public CellRange<? extends org.apache.poi.ss.usermodel.Cell> removeArrayFormula(org.apache.poi.ss.usermodel.Cell cell) {
        return null;
    }

    @Override
    public DataValidationHelper getDataValidationHelper() {
        return null;
    }

    @Override
    public List<? extends DataValidation> getDataValidations() {
        return null;
    }

    @Override
    public void addValidationData(DataValidation dataValidation) {
    }

    @Override
    public AutoFilter setAutoFilter(CellRangeAddress cellRangeAddress) {
        return null;
    }

    @Override
    public SheetConditionalFormatting getSheetConditionalFormatting() {
        return null;
    }

    @Override
    public CellRangeAddress getRepeatingRows() {
        return null;
    }

    @Override
    public CellRangeAddress getRepeatingColumns() {
        return null;
    }

    @Override
    public void setRepeatingRows(CellRangeAddress cellRangeAddress) {
    }

    @Override
    public void setRepeatingColumns(CellRangeAddress cellRangeAddress) {
    }

    @Override
    public int getColumnOutlineLevel(int i) {
        return 0;
    }

    @Override
    public Hyperlink getHyperlink(int i, int i1) {
        return null;
    }

    @Override
    public Hyperlink getHyperlink(CellAddress cellAddress) {
        return null;
    }

    @Override
    public List<? extends Hyperlink> getHyperlinkList() {
        return null;
    }

    @Override
    public CellAddress getActiveCell() {
        return null;
    }

    @Override
    public void setActiveCell(CellAddress cellAddress) {
    }

    public Cell getCell(int row, int column) {
        return null;
    }

    public Cell getCell(String address) {
        return null;
    }

    public void delete() {
    }

    public void dispose() {
    }

    @Override
    public org.apache.poi.ss.usermodel.Row createRow(int i) {
        return null;
    }

    @Override
    public void removeRow(org.apache.poi.ss.usermodel.Row row) {
        if(row == null) {
            return;
        }
        ((Row)row).dispose();
    }

    @Override
    public org.apache.poi.ss.usermodel.Row getRow(int index) {
        return null;
    }

    @Override
    public int getPhysicalNumberOfRows() {
        return 0;
    }

    @Override
    public int getFirstRowNum() {
        return 0;
    }

    @Override
    public int getLastRowNum() {
        return 0;
    }

    @Override
    public void setColumnHidden(int index, boolean b) {
    }

    @Override
    public boolean isColumnHidden(int index) {
        return false;
    }

    @Override
    public void setRightToLeft(boolean b) {
    }

    @Override
    public boolean isRightToLeft() {
        return false;
    }

    @Override
    public void setColumnWidth(int i, int i1) {
    }

    @Override
    public int getColumnWidth(int index) {
        return 0;
    }

    @Override
    public float getColumnWidthInPixels(int index) {
        return 0;
    }

    @Override
    public void setDefaultColumnWidth(int i) {
    }

    @Override
    public int getDefaultColumnWidth() {
        return 0;
    }

    @Override
    public short getDefaultRowHeight() {
        return 0;
    }

    @Override
    public float getDefaultRowHeightInPoints() {
        return 0;
    }

    @Override
    public void setDefaultRowHeight(short i) {
    }

    @Override
    public void setDefaultRowHeightInPoints(float v) {
    }

    @Override
    public CellStyle getColumnStyle(int i) {
        return null;
    }

    @Override
    public int addMergedRegion(CellRangeAddress cellRangeAddress) {
        return 0;
    }

    @Override
    public int addMergedRegionUnsafe(CellRangeAddress cellRangeAddress) {
        return 0;
    }

    @Override
    public void validateMergedRegions() {
    }

    @Override
    public void setVerticallyCenter(boolean b) {
    }

    @Override
    public void setHorizontallyCenter(boolean b) {
    }

    @Override
    public boolean getHorizontallyCenter() {
        return false;
    }

    @Override
    public boolean getVerticallyCenter() {
        return false;
    }

    @Override
    public void removeMergedRegion(int i) {
    }

    @Override
    public void removeMergedRegions(Collection<Integer> collection) {
    }

    @Override
    public int getNumMergedRegions() {
        return 0;
    }

    @Override
    public CellRangeAddress getMergedRegion(int i) {
        return null;
    }

    @Override
    public List<CellRangeAddress> getMergedRegions() {
        return null;
    }

    @Override
    public Iterator<org.apache.poi.ss.usermodel.Row> rowIterator() {
        return null;
    }

    @Override
    public void setForceFormulaRecalculation(boolean b) {
    }

    @Override
    public boolean getForceFormulaRecalculation() {
        return false;
    }

    @Override
    public void setAutobreaks(boolean b) {
    }

    @Override
    public void setDisplayGuts(boolean b) {
    }

    @Override
    public void setDisplayZeros(boolean b) {
    }

    @Override
    public boolean isDisplayZeros() {
        return false;
    }

    @Override
    public void setFitToPage(boolean b) {
    }

    @Override
    public void setRowSumsBelow(boolean b) {
    }

    @Override
    public void setRowSumsRight(boolean b) {
    }

    @Override
    public boolean getAutobreaks() {
        return false;
    }

    @Override
    public boolean getDisplayGuts() {
        return false;
    }

    @Override
    public boolean getFitToPage() {
        return false;
    }

    @Override
    public boolean getRowSumsBelow() {
        return false;
    }

    @Override
    public boolean getRowSumsRight() {
        return false;
    }

    @Override
    public boolean isPrintGridlines() {
        return false;
    }

    @Override
    public void setPrintGridlines(boolean b) {
    }

    @Override
    public boolean isPrintRowAndColumnHeadings() {
        return false;
    }

    @Override
    public void setPrintRowAndColumnHeadings(boolean b) {
    }

    @Override
    public PrintSetup getPrintSetup() {
        return null;
    }

    @Override
    public Header getHeader() {
        return null;
    }

    @Override
    public Footer getFooter() {
        return null;
    }

    @Override
    public void setSelected(boolean b) {
    }

    @Override
    public double getMargin(short i) {
        return 0;
    }

    @Override
    public double getMargin(PageMargin pageMargin) {
        return 0;
    }

    @Override
    public void setMargin(short i, double v) {
    }

    @Override
    public void setMargin(PageMargin pageMargin, double v) {
    }

    @Override
    public boolean getProtect() {
        return false;
    }

    @Override
    public void protectSheet(String s) {
    }

    @Override
    public boolean getScenarioProtect() {
        return false;
    }

    @Override
    public void setZoom(int i) {
    }

    @Override
    public short getTopRow() {
        return 0;
    }

    @Override
    public short getLeftCol() {
        return 0;
    }

    @Override
    public void showInPane(int i, int i1) {
    }

    @Override
    public void shiftRows(int i, int i1, int i2) {
    }

    @Override
    public void shiftRows(int i, int i1, int i2, boolean b, boolean b1) {
    }

    @Override
    public void shiftColumns(int i, int i1, int i2) {
    }

    @Override
    public void createFreezePane(int i, int i1, int i2, int i3) {
    }

    @Override
    public void createFreezePane(int i, int i1) {
    }

    @Override
    public void createSplitPane(int i, int i1, int i2, int i3, int i4) {
    }

    @Override
    public void createSplitPane(int i, int i1, int i2, int i3, PaneType paneType) {
    }

    @Override
    public PaneInformation getPaneInformation() {
        return null;
    }

    @Override
    public void setDisplayGridlines(boolean b) {
    }

    @Override
    public boolean isDisplayGridlines() {
        return false;
    }

    @Override
    public void setDisplayFormulas(boolean b) {
    }

    @Override
    public boolean isDisplayFormulas() {
        return false;
    }

    @Override
    public void setDisplayRowColHeadings(boolean b) {
    }

    @Override
    public boolean isDisplayRowColHeadings() {
        return false;
    }

    @Override
    public void setRowBreak(int i) {
    }

    @Override
    public boolean isRowBroken(int i) {
        return false;
    }

    @Override
    public void removeRowBreak(int i) {
    }

    @Override
    public int[] getRowBreaks() {
        return new int[0];
    }

    @Override
    public int[] getColumnBreaks() {
        return new int[0];
    }

    @Override
    public void setColumnBreak(int i) {
    }

    @Override
    public boolean isColumnBroken(int i) {
        return false;
    }

    @Override
    public void removeColumnBreak(int i) {
    }

    @Override
    public void setColumnGroupCollapsed(int i, boolean b) {
    }

    @Override
    public void groupColumn(int i, int i1) {
    }

    @Override
    public void ungroupColumn(int i, int i1) {
    }

    @Override
    public void groupRow(int i, int i1) {
    }

    @Override
    public void ungroupRow(int i, int i1) {
    }

    @Override
    public void setRowGroupCollapsed(int i, boolean b) {
    }

    @Override
    public void setDefaultColumnStyle(int i, CellStyle cellStyle) {
    }

    @Override
    public void autoSizeColumn(int i) {
    }

    @Override
    public void autoSizeColumn(int i, boolean b) {
    }

    @Override
    public Comment getCellComment(CellAddress cellAddress) {
        return null;
    }

    @Override
    public Map<CellAddress, ? extends Comment> getCellComments() {
        return null;
    }

    @Override
    public Drawing<?> getDrawingPatriarch() {
        return null;
    }

    @Override
    public Drawing<?> createDrawingPatriarch() {
        return null;
    }

    @Override
    public Iterator<org.apache.poi.ss.usermodel.Row> iterator() {
        return null;
    }
}