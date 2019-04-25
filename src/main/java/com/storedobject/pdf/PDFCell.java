package com.storedobject.pdf;

public class PDFCell extends com.storedobject.pdf.PDFRectangle implements com.storedobject.pdf.PDFElement {

    public PDFCell() {
        super(0, 0, 0, 0);
    }

    public PDFCell(com.storedobject.pdf.PDFPhrase phrase) {
        this();
    }

    public PDFCell(com.storedobject.pdf.PDFImage image) {
        this();
    }

    public PDFCell(com.storedobject.pdf.PDFImage image, boolean fit) {
        this();
    }

    public PDFCell(com.storedobject.pdf.PDFTable table) {
        this();
    }

    public PDFCell(com.storedobject.pdf.PDFTable table, com.storedobject.pdf.PDFCell styleToCopy) {
        this();
    }

    public PDFCell(com.storedobject.pdf.PDFCell cell) {
        this();
    }

    public void addElement(com.storedobject.pdf.PDFElement element) {
    }

    public void setMinimumHeight(float height) {
    }

    public void setColumnSpan(int span) {
    }

    public void setHorizontalAlignment(int alignment) {
    }

    public void setPaddingTop(float padding) {
    }

    public void setVerticalAlignment(int alignment) {
    }

    public com.storedobject.pdf.PDFImage getImage() {
        return null;
    }

    @Override
	public void setRotation(int rotation) {
    }

    @Override
	public int getRotation() {
        return 0;
    }

    public int getVerticalAlignment() {
        return 0;
    }

    public void setPaddingRight(float padding) {
    }

    public void setPaddingLeft(float padding) {
    }

    public int getColumnSpan() {
        return 0;
    }

    public int getRunDirection() {
        return 0;
    }

    public void setRunDirection(int direction) {
    }

    public int getRowSpan() {
        return 0;
    }

    public void setImage(com.storedobject.pdf.PDFImage image) {
    }

    public void setPhrase(com.storedobject.pdf.PDFPhrase phrase) {
    }

    public float getMaxHeight() {
        return 0;
    }

    public int getHorizontalAlignment() {
        return 0;
    }

    public void setPadding(float padding) {
    }
    
    /**
     * Sets the leading to fixed.
     *
     * @param leading the leading
     */
    public void setLeading(final float leading) {
    }

    /**
     * Sets the leading fixed and variable.
     * The resultant leading will be:
     * fixedLeading+multipliedLeading*maxFontSize
     * where maxFontSize is the size of the biggest font in the line.
     *
     * @param fixedLeading the fixed leading
     * @param multipliedLeading the variable leading
     */
    public void setLeading(float fixedLeading, float multipliedLeading) {
    }

    /**
     * Gets the fixed leading.
     *
     * @return the leading
     */
    public float getLeading() {
        return 0;
    }

    /**
     * Gets the variable leading.
     *
     * @return the leading
     */
    public float getMultipliedLeading() {
        return 0;
    }

    /**
     * Gets the extra space between paragraphs.
     *
     * @return the extra space between paragraphs
     */
    public float getExtraParagraphSpace() {
        return 0;
    }

    /**
     * Sets the extra space between paragraphs.
     *
     * @param extraParagraphSpace the extra space between paragraphs
     */
    public void setExtraParagraphSpace(float extraParagraphSpace) {
    }

    /**
     * Tells you whether the cell has a fixed height.
     *
     * @return	true is a fixed height was set.
     */
    public boolean hasFixedHeight() {
    	return getFixedHeight() > 0;
    }

    /**
     * Tells you whether the cell has a minimum height.
     *
     * @return	true if a minimum height was set.
     */
    public boolean hasMinimumHeight() {
    	return getMinimumHeight() > 0;
    }

    public void setIndent(float indent) {
    }

    public float getIndent() {
        return 0;
    }

    public void setFollowingIndent(float followingIndent) {
    }

    public float getFollowingIndent() {
        return 0;
    }

    public void setRightIndent(float indent) {
    }

    public float getRightIndent() {
        return 0;
    }

    public com.storedobject.pdf.PDFPhrase getPhrase() {
        return null;
    }

    public float getPaddingLeft() {
        return 0;
    }

    public float getPaddingRight() {
        return 0;
    }

    public float getPaddingTop() {
        return 0;
    }

    public float getPaddingBottom() {
        return 0;
    }

    public void setPaddingBottom(float padding) {
    }

    public void setFixedHeight(float height) {
    }

    public float getFixedHeight() {
        return 0;
    }

    public float getMinimumHeight() {
        return 0;
    }

    public boolean isWrap() {
        return false;
    }

    public void setWrap(boolean wrap) {
    }

    public void setRowSpan(int span) {
    }
}
