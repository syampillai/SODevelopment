package com.storedobject.pdf;

@SuppressWarnings("serial")
public class PDFParagraph extends com.storedobject.pdf.PDFPhrase {

    public PDFParagraph() {
    }

    public PDFParagraph(com.storedobject.pdf.PDFChunk chunk) {
        this();
    }

    public PDFParagraph(float p1, com.storedobject.pdf.PDFChunk chunk) {
        this();
    }

    public PDFParagraph(java.lang.String text) {
        this();
    }

    public PDFParagraph(java.lang.String text, com.storedobject.pdf.PDFFont font) {
        this();
    }

    public PDFParagraph(com.storedobject.pdf.PDFPhrase phrase) {
        this();
    }

	@Override
	public boolean add(com.storedobject.pdf.PDFElement element) {
        return false;
    }

	public void setSpacingBefore(float spacing) {
    }

	public void setSpacingAfter(float spacing) {
    }

    public int getAlignment() {
        return 0;
    }

    public void setAlignment(int alignment) {
    }

	public float getIndentationLeft() {
        return 0;
    }

	public void setIndentationLeft(float indent) {
    }

	public float getIndentationRight() {
        return 0;
    }

	public void setIndentationRight(float indent) {
    }

	public float getSpacingBefore() {
        return 0;
    }

	public float getSpacingAfter() {
        return 0;
    }

    public void setKeepTogether(boolean keep) {
    }

    public boolean getKeepTogether() {
        return false;
    }

    public float getFirstLineIndent() {
        return 0;
    }

    public void setFirstLineIndent(float indent) {
    }
}
