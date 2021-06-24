package com.storedobject.pdf;

import java.util.function.Function;

/**
 * Table object that can be added to PDF document.
 */
public class PDFTable implements PDFElement {

    private PDFTable() {
    }

    public PDFTable(float[] relativeWidths) {
        this();
    }

    public PDFTable(int columnCount) {
        this();
    }

    /**
     * Add a blank cell.
     */
    public void addBlankCell() {
        addCell(new PDFPhrase());
    }

    /**
     * Add a blank cell.
     *
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addBlankCell(Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Add a number of blank cells.
     *
     * @param count Number of blank cells to add.
     */
    public void addBlankCell(int count) {
    }

    /**
     * Add a number of blank cells.
     *
     * @param count Number of blank cells to add.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addBlankCell(int count, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    public void addCell(PDFTable table) {
    }

    public void addCell(PDFImage image) {
    }

    public void addCell(PDFPhrase phrase) {
    }

    /**
     * Add a cell to the table.
     *
     * @param cell The cell to add.
     */
    public void addCell(PDFCell cell) {
        addCell(cell, null);
    }

    /**
     * Add a cell to the table.
     *
     * @param cell The cell to add.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addCell(PDFCell cell, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Add a cell spanning the entire row.
     *
     * @param cell Cell to be added.
     */
    public void addRowCell(PDFCell cell) {
    }

    /**
     * Add a cell spanning the entire row.
     *
     * @param cell Cell to add.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addRowCell(PDFCell cell, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    public PDFCell getDefaultCell() {
        return null;
    }

    public void setHorizontalAlignment(int alignment) {
    }

    public void setTotalWidth(float totalWidth) {
    }

    public void setTotalWidth(float[] columnWidth) throws Exception {
    }

    public float getTotalHeight() {
        return 0;
    }

    public float getTotalWidth() {
        return 0;
    }

    public void setWidths(float[] relativeWidths) throws PDFException {
    }

    public void setWidths(int[] relativeWidths) throws PDFException {
    }

    public int getNumberOfColumns() {
        return 0;
    }

    public int getNumberOfRows() {
        return 0;
    }

	public void setSpacingBefore(float spacing) {
    }

	public void setSpacingAfter(float spacing) {
    }

    public void setWidthPercentage(float[] columnWidth, PDFRectangle pageSize) throws PDFException {
    }

    public void setWidthPercentage(float widthPercentage) {
    }

    public void deleteBodyRows() {
    }

    public void setSkipFirstHeader(boolean skip) {
    }

    public java.util.List < PDFChunk > getChunks() {
        return null;
    }

	public float getSpacingBefore() {
        return 0;
    }

	public float getSpacingAfter() {
        return 0;
    }

    public float getWidthPercentage() {
        return 0;
    }

    public boolean deleteRow(int rowIndex) {
        return false;
    }

    public void setHeaderRows(int rows) {
    }

    public float getRowHeight(int rowImdex) {
        return 0;
    }

    public int getTextDirection() {
        return 0;
    }

    public void setTextDirection(int direction) {
    }

    public float getHeaderHeight() {
        return 0;
    }

    public float getFooterHeight() {
        return 0;
    }

    public boolean deleteLastRow() {
        return false;
    }

    public int getHeaderRows() {
        return 0;
    }

    public int getHorizontalAlignment() {
        return 0;
    }

    public void setBreakPoints(int[] breakPoints) {
    }

    public void keepRowsTogether(int[] rows) {
    }

    public void keepRowsTogether(int fromRow, int toRow) {
    }

    public void keepRowsTogether(int fromRow) {
    }

    public float[] getAbsoluteWidths() {
        return null;
    }

    public boolean isSkipFirstHeader() {
        return false;
    }

    public boolean isSkipLastFooter() {
        return false;
    }

    public void setSkipLastFooter(boolean skip) {
    }

    public boolean isSplitRows() {
        return false;
    }

    public void setSplitRows(boolean split) {
    }

    public void setKeepTogether(boolean keepTogether) {
    }

    public boolean getKeepTogether() {
        return false;
    }

    public int getFooterRows() {
        return 0;
    }

    public void setFooterRows(int rows) {
    }

    /**
     * Add a blank row.
     */
    public void addBlankRow() {
    }

    /**
     * Add a blank row.
     *
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addBlankRow(Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Add a blank row.
     *
     * @param fromColumn From column.
     */
    public void addBlankRow(int fromColumn) {
    }

    /**
     * Add a blank row.
     *
     * @param fromColumn From column.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addBlankRow(int fromColumn, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Add a blank row.
     *
     * @param fromColumn From column.
     * @param toColumn To column.
     */
    public void addBlankRow(int fromColumn, int toColumn) {
    }

    /**
     * Add a blank row.
     *
     * @param fromColumn From column.
     * @param toColumn To column.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addBlankRow(int fromColumn, int toColumn, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Set the border width for the left side. Default is no border.
     *
     * @param borderThickness Border width.
     */
    public void setBorderWidthLeft(float borderThickness) {
    }

    /**
     * Set the border width for the right side. Default is no border.
     *
     * @param borderThickness Border width.
     */
    public void setBorderWidthRight(float borderThickness) {
    }

    /**
     * Set the border width for the top side. Default is no border.
     *
     * @param borderThickness Border width.
     */
    public void setBorderWidthTop(float borderThickness) {
    }

    /**
     * Set the border width for the bottom side. Default is no border.
     *
     * @param borderThickness Border width.
     */
    public void setBorderWidthBottom(float borderThickness) {
    }

    /**
     * Set the border width for the all sides. Default is no border.
     *
     * @param borderThickness Border width.
     */
    public void setBorderWidth(float borderThickness) {
    }
}
