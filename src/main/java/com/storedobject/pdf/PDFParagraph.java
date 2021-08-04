package com.storedobject.pdf;

public class PDFParagraph extends PDFPhrase {

    public PDFParagraph() {
    }

    public PDFParagraph(PDFChunk chunk) {
        this();
    }

    public PDFParagraph(float p1, PDFChunk chunk) {
        this();
    }

    public PDFParagraph(String text) {
        this();
    }

    public PDFParagraph(String text, PDFFont font) {
        this();
    }

    public PDFParagraph(PDFPhrase phrase) {
        this();
    }

	@Override
	public boolean add(PDFElement element) {
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
