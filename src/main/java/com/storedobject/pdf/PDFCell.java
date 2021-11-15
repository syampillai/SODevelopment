package com.storedobject.pdf;

/**
 * Represents a "cell" of the {@link PDFTable}. {@link PDFTable} has many methods to create "cells" and
 * that is the preferred way to create {@link PDFCell} instances rather than directly using the constructors
 * of this method.
 *
 * @author Syam
 */
public class PDFCell extends PDFRectangle implements PDFElement {

    /**
     * Constructor.
     */
    public PDFCell() {
        super(0, 0, 0, 0);
    }

    /**
     * Constructor from a phrase as its content.
     *
     * @param phrase Phrase.
     */
    public PDFCell(PDFPhrase phrase) {
        this();
    }

    /**
     * Construct from an image as its content.
     *
     * @param image Image.
     */
    public PDFCell(PDFImage image) {
        this();
    }

    /**
     * Construct from an image as its content.
     *
     * @param image Image.
     * @param autoFit Whether to auto-fit it within the cell or not.
     */
    public PDFCell(PDFImage image, boolean autoFit) {
        this();
    }

    /**
     * Construct from a table as its content.
     *
     * @param table Table.
     */
    public PDFCell(PDFTable table) {
        this();
    }

    /**
     * Construct from a table as its content.
     *
     * @param table Table.
     * @param styleToCopy Copy the styles from this cell.
     */
    public PDFCell(PDFTable table, PDFCell styleToCopy) {
        this();
    }

    /**
     * Construct from another cell.
     *
     * @param cell Cell to copy from.
     */
    public PDFCell(PDFCell cell) {
        this();
    }

    /**
     * Add an {@link PDFElement} to this cell.
     *
     * @param element Element to add.
     */
    public void addElement(PDFElement element) {
    }

    /**
     * Set minimum height.
     *
     * @param height Height.
     */
    public void setMinimumHeight(float height) {
    }

    /**
     * Set number of columns to span (by default only 1 column of the {@link PDFTable} will be occupied).
     *
     * @param numbreOfColumns Number of columns.
     */
    public void setColumnSpan(int numbreOfColumns) {
    }

    /**
     * Set horizontal alignment. ({@link PDFElement#ALIGN_RIGHT} for example).
     *
     * @param alignment Alignment.
     */
    public void setHorizontalAlignment(int alignment) {
    }

    /**
     * Set padding at the top.
     *
     * @param padding Padding.
     */
    public void setPaddingTop(float padding) {
    }

    /**
     * Set vertical alignment. ({@link PDFElement#ALIGN_BOTTOM} for example).
     *
     * @param alignment Alignment.
     */
    public void setVerticalAlignment(int alignment) {
    }

    /**
     * Get the image contained in this cell.
     *
     * @return Image if exists, otherwise <code>null</code>.
     */
    public PDFImage getImage() {
        return null;
    }

    /**
     * Get the vertical alignment.
     *
     * @return Alignment.
     */
    public int getVerticalAlignment() {
        return 0;
    }

    /**
     * Set padding on the right side.
     *
     * @param padding Padding.
     */
    public void setPaddingRight(float padding) {
    }

    /**
     * Set padding on the left side.
     *
     * @param padding Padding.
     */
    public void setPaddingLeft(float padding) {
    }

    /**
     * Get the number of columns the cell spans.
     *
     * @return Number of columns.
     */
    public int getColumnSpan() {
        return 0;
    }

    /**
     * Get the text direction. (Example: {@link PDFElement#TEXT_DIRECTION_RTL}).
     *
     * @return Text direction.
     */
    public int getTextDirection() {
        return 0;
    }

    /**
     * Set the text direction. (Example: {@link PDFElement#TEXT_DIRECTION_RTL}).
     *
     * @param direction  Text direction.
     */
    public void setTextDirection(int direction) {
    }

    /**
     * Get the number of rows occupied by this cell.
     *
     * @return Number of rows occupied by this cell.
     */
    public int getRowSpan() {
        return 0;
    }

    /**
     * Set an image as the content of this cell.
     *
     * @param image Image to set.
     */
    public void setImage(PDFImage image) {
    }

    /**
     * Set an phrase as the content of this cell.
     *
     * @param phrase Phrase to set.
     */
    public void setPhrase(PDFPhrase phrase) {
    }

    /**
     * Get the maximum height.
     *
     * @return Height.
     */
    public float getMaxHeight() {
        return 0;
    }

    /**
     * Get the horizontal alignment.
     *
     * @return Alignment.
     */
    public int getHorizontalAlignment() {
        return 0;
    }

    /**
     * Get the padding of this cell.
     *
     * @param padding Padding.
     */
    public void setPadding(float padding) {
    }
    
    /**
     * Set the leading to fixed.
     *
     * @param leading Leading.
     */
    public void setLeading(final float leading) {
    }

    /**
     * Set the leading to fixed and variable part.
     * <p>The resultant leading will be: fixedLeading + multipliedLeading * maxFontSize</p>
     * where maxFontSize is the size of the biggest font in the line.
     *
     * @param fixedLeading Fixed leading.
     * @param multipliedLeading Variable part of the leading.
     */
    public void setLeading(float fixedLeading, float multipliedLeading) {
    }

    /**
     * Get the leading.
     *
     * @return Leading (fixed part).
     */
    public float getLeading() {
        return 0;
    }

    /**
     * Get the variable leading.
     *
     * @return Leading (variable part).
     */
    public float getMultipliedLeading() {
        return 0;
    }

    /**
     * Get the extra space between paragraphs.
     *
     * @return The extra space between paragraphs.
     */
    public float getExtraParagraphSpace() {
        return 0;
    }

    /**
     * Set the extra space between paragraphs.
     *
     * @param extraParagraphSpace The extra space between paragraphs.
     */
    public void setExtraParagraphSpace(float extraParagraphSpace) {
    }

    /**
     * Set indent.
     *
     * @param indent Indent to set.
     */
    public void setIndent(float indent) {
    }

    /**
     * Get the indent.
     *
     * @return The indent.
     */
    public float getIndent() {
        return 0;
    }

    /**
     * Set the following indent.
     *
     * @param indent Indent to set.
     */
    public void setFollowingIndent(float indent) {
    }

    /**
     * Get the following indent.
     *
     * @return The indent.
     */
    public float getFollowingIndent() {
        return 0;
    }

    /**
     * Set the right indent.
     *
     * @param indent Indent to set.
     */
    public void setRightIndent(float indent) {
    }

    /**
     * Get the right indent.
     *
     * @return The indent.
     */
    public float getRightIndent() {
        return 0;
    }

    /**
     * Get the phrase inside the cell.
     *
     * @return The phrase.
     */
    public PDFPhrase getPhrase() {
        return null;
    }

    /**
     * Get the left padding of this cell.
     *
     * @return Padding.
     */
    public float getPaddingLeft() {
        return 0;
    }

    /**
     * Get the right padding of this cell.
     *
     * @return Padding.
     */
    public float getPaddingRight() {
        return 0;
    }

    /**
     * Get the top padding of this cell.
     *
     * @return Padding.
     */
    public float getPaddingTop() {
        return 0;
    }

    /**
     * Get the bottom padding of this cell.
     *
     * @return Padding.
     */
    public float getPaddingBottom() {
        return 0;
    }

    /**
     * Set the bottom padding of this cell.
     *
     * @param padding Padding to set.
     */
    public void setPaddingBottom(float padding) {
    }

    /**
     * Set fixed height for the cell.
     *
     * @param height Height.
     */
    public void setFixedHeight(float height) {
    }

    /**
     * Get fixed height for the cell.
     *
     * @return Height.
     */
    public float getFixedHeight() {
        return 0;
    }

    /**
     * Get minimum height for the cell.
     *
     * @return Height.
     */
    public float getMinimumHeight() {
        return 0;
    }

    /**
     * Does the content wrap in this cell?
     *
     * @return True/false.
     */
    public boolean isWrap() {
        return false;
    }

    /**
     * Set the content to wrap.
     *
     * @param wrap True if wrapping to be turned on.
     */
    public void setWrap(boolean wrap) {
    }

    /**
     * Set the number of rows of the table this cell to occupy.
     *
     * @param rows Number of rows.
     */
    public void setRowSpan(int rows) {
    }

    /**
     * Set a background image in this cell (The cell content will go above this image).
     *
     * @param image Background image to set.
     */
    public void setBackgroundImage(PDFImage image) {
        setBackgroundImage(image, false);
    }

    /**
     * Set a background image in this cell (The cell content will go above this image).
     *
     * @param image Background image to set.
     * @param scale Whether to scale image to the cell size or not. If <code>false</code> is passed, it will
     *              make sure that the cell has at least the height of the image.
     */
    public void setBackgroundImage(PDFImage image, boolean scale) {
    }
}
