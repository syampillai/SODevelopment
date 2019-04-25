package com.storedobject.pdf;

public class PDFChunk implements com.storedobject.pdf.PDFElement {

    public PDFChunk() {
    }

    public PDFChunk(com.storedobject.pdf.PDFChunk chunk) {
        this();
    }

    public PDFChunk(java.lang.String text, com.storedobject.pdf.PDFFont font) {
        this();
    }

    public PDFChunk(java.lang.String text) {
        this();
    }

    public PDFChunk(char c, com.storedobject.pdf.PDFFont font) {
        this();
    }

    public PDFChunk(char c) {
        this();
    }

    public PDFChunk(com.storedobject.pdf.PDFImage image, float xOffset, float yOffset) {
        this();
    }

    public java.lang.StringBuffer append(java.lang.String text) {
        return null;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isWhitespace() {
        return false;
    }

    public float getWidthPoint() {
        return 0;
    }

    public com.storedobject.pdf.PDFImage getImage() {
        return null;
    }

	public java.util.List < com.storedobject.pdf.PDFChunk > getChunks() {
        return null;
    }

    public void setFont(com.storedobject.pdf.PDFFont font) {
    }

    public com.storedobject.pdf.PDFChunk setBackground(com.storedobject.pdf.PDFColor color) {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setBackground(com.storedobject.pdf.PDFColor color, float extraLeft, float extraBottom, float extraRight, float extraTop) {
        return null;
    }

    public com.storedobject.pdf.PDFFont getFont() {
        return null;
    }

    public float getCharacterSpacing() {
        return 0;
    }

    public float getHorizontalScaling() {
        return 0;
    }

    public com.storedobject.pdf.PDFChunk setCharacterSpacing(float spacing) {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setHorizontalScaling(float scaling) {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setTextRise(float rise) {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setNewPage() {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setUnderline(float thickness, float yPosition) {
        return null;
    }

    public float getTextRise() {
        return 0;
    }

    public com.storedobject.pdf.PDFChunk setSkew(float firstAngle, float secondAngle) {
        return null;
    }

    public com.storedobject.pdf.PDFChunk setLineHeight(float height) {
        return null;
    }
}
