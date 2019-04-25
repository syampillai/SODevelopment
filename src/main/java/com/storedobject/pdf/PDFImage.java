package com.storedobject.pdf;

public abstract class PDFImage extends com.storedobject.pdf.PDFRectangle implements com.storedobject.pdf.PDFElement {

    private PDFImage() {
        super(0, 0, 0, 0);
    }

    public float getPlainHeight() {
        return 0;
    }

    public float getPlainWidth() {
        return 0;
    }

	public void setSpacingBefore(float spacing) {
    }

	public void setSpacingAfter(float spacing) {
    }

    public void setWidthPercentage(float width) {
    }

    public int[] getTransparency() {
        return null;
    }

    public int getAlignment() {
        return 0;
    }

    public void setAlignment(int alignment) {
    }

    public float getScaledWidth() {
        return 0;
    }

    public float getScaledHeight() {
        return 0;
    }

    public void scaleAbsolute(float width, float height) {
    }

    public void scaleAbsoluteWidth(float width) {
    }

    public void scaleAbsoluteHeight(float height) {
    }

    public void scalePercent(float percentage) {
    }

    public void scalePercent(float width, float height) {
    }

    public void scaleToFit(float width, float height) {
    }

    public float getImageRotation() {
        return 0;
    }

    public void setRotation(float p1) {
    }

    public void setRotationDegrees(float degrees) {
    }

    public float getInitialRotation() {
        return 0;
    }

    public void setInitialRotation(float degrees) {
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

    public float getWidthPercentage() {
        return 0;
    }

    public boolean isDeflated() {
        return false;
    }

    public void setDeflated(boolean deflated) {
    }

    public int getDPIX() {
        return 0;
    }

    public int getDPIY() {
        return 0;
    }

    public void setDPI(int x, int y) {
    }

    public float getXYRatio() {
        return 0;
    }

    public void setXYRatio(float ration) {
    }

    public int getColorspace() {
        return 0;
    }

    public boolean isInverted() {
        return false;
    }

    public void setInverted(boolean inverted) {
    }

    public void setTransparency(int[] transparency) {
    }
}
