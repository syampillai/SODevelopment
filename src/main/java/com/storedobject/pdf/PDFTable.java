package com.storedobject.pdf;

/**
 * Table than can be added to PDF document.
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

    public void addCell(PDFCell cell) {
    }

    /**
     * Adds a blank cell
     */
    public void addBlankCell() {
        addCell(new PDFPhrase());    	
    }

    /**
     * Adds a number of blank cells
     * 
     * @param count Number of blank cells to add
     */
    public void addBlankCell(int count) {
    }

    /**
     * Adds a cell element.
     *
     * @param text The text for the cell
     * @deprecated Use addBlanckCell() if you want to add a blank cell, otherwise use addCell(createCell(...))
     */
    @Deprecated
    public void addCell(final String text) {
    }

    public void addCell(PDFTable table) {
    }

    public void addCell(PDFImage image) {
    }

    public void addCell(PDFPhrase phrase) {
    }
    
    public void addRowCell(PDFCell cell) {
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

    public int getRunDirection() {
        return 0;
    }

    public void setRunDirection(int direction) {
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

	public void addBlankRow() {
	}

	public void addBlankRow(int fromColumn) {
	}

	public void addBlankRow(int fromColumn, int toColumn) {
	}
	
	public void addBlankRow(PDF pdf) {
	}

	public void addBlankRow(PDF pdf, int fromColumn) {
	}

	public void addBlankRow(PDF pdf, int fromColumn, int toColumn) {
	}
}
