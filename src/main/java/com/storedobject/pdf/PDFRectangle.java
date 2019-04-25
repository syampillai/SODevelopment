package com.storedobject.pdf;

public class PDFRectangle implements com.storedobject.pdf.PDFElement {

    public static int UNDEFINED = 0;
    public static int TOP = 0;
    public static int BOTTOM = 0;
    public static int LEFT = 0;
    public static int RIGHT = 0;
    public static int NO_BORDER = 0;
    public static int BOX = 0;

    public PDFRectangle(float x1, float y1, float x2, float y2) {
        this();
    }

    public PDFRectangle(float x1, float y1, float x2, float y2, int rotation) {
        this();
    }

    public PDFRectangle(float x2, float y2) {
        this();
    }

    public PDFRectangle(float x2, float y2, int rotation) {
        this();
    }

    public PDFRectangle(com.storedobject.pdf.PDFRectangle another) {
        this();
    }

    private PDFRectangle() {
    }

    public com.storedobject.pdf.PDFRectangle rotate() {
        return null;
    }

    public void setBorderWidth(float thickness) {
    }

    public void setBorder(int borders) {
    }

    public float getWidth() {
        return 0;
    }

    public float getHeight() {
        return 0;
    }

    public void setGrayFill(float fillValue) {
    }

    public void setRotation(int roatation) {
    }

    public void setLeft(float value) {
    }

    public float getLeft() {
        return 0;
    }

    public float getLeft(float margin) {
        return 0;
    }

    public void setRight(float value) {
    }

    public float getRight() {
        return 0;
    }

    public float getRight(float margin) {
        return 0;
    }

    public void setTop(float value) {
    }

    public float getTop() {
        return 0;
    }

    public float getTop(float margin) {
        return 0;
    }

    public void setBottom(float value) {
    }

    public float getBottom() {
        return 0;
    }

    public float getBottom(float margin) {
        return 0;
    }

    public int getRotation() {
        return 0;
    }

    public com.storedobject.pdf.PDFColor getBackgroundColor() {
        return null;
    }

    public void setBackgroundColor(com.storedobject.pdf.PDFColor color) {
    }

    public float getGrayFill() {
        return 0;
    }

    public int getBorder() {
        return 0;
    }

    public boolean hasBorders() {
        return false;
    }

    public boolean hasBorder(int borders) {
        return false;
    }

    public void enableBorderSide(int borders) {
    }

    public void disableBorderSide(int borders) {
    }

    public float getBorderWidth() {
        return 0;
    }

    public float getBorderWidthLeft() {
        return 0;
    }

    public void setBorderWidthLeft(float thickness) {
    }

    public float getBorderWidthRight() {
        return 0;
    }

    public void setBorderWidthRight(float thickness) {
    }

    public float getBorderWidthTop() {
        return 0;
    }

    public void setBorderWidthTop(float thickness) {
    }

    public float getBorderWidthBottom() {
        return 0;
    }

    public void setBorderWidthBottom(float thickness) {
    }

    public com.storedobject.pdf.PDFColor getBorderColor() {
        return null;
    }

    public void setBorderColor(com.storedobject.pdf.PDFColor color) {
    }

    public com.storedobject.pdf.PDFColor getBorderColorLeft() {
        return null;
    }

    public void setBorderColorLeft(com.storedobject.pdf.PDFColor color) {
    }

    public com.storedobject.pdf.PDFColor getBorderColorRight() {
        return null;
    }

    public void setBorderColorRight(com.storedobject.pdf.PDFColor color) {
    }

    public com.storedobject.pdf.PDFColor getBorderColorTop() {
        return null;
    }

    public void setBorderColorTop(com.storedobject.pdf.PDFColor color) {
    }

    public com.storedobject.pdf.PDFColor getBorderColorBottom() {
        return null;
    }

    public void setBorderColorBottom(com.storedobject.pdf.PDFColor color) {
    }
}
