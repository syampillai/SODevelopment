package com.storedobject.pdf;

/**
 * Representation of "PDF rectangle".
 *
 * @author Syam
 */
public class PDFRectangle implements PDFElement {

    /**
     * Uses to represent an "undefined" value.
     */
    public static int UNDEFINED = 0;
    /**
     * Represents top side.
     */
    public static int TOP = 0;
    /**
     * Represents bottom side.
     */
    public static int BOTTOM = 0;
    /**
     * Represents left side.
     */
    public static int LEFT = 0;
    /**
     * Represents right side.
     */
    public static int RIGHT = 0;
    /**
     * Represents "no border".
     */
    public static int NO_BORDER = 0;
    /**
     * Represents all sides (as a box).
     */
    public static int BOX = 0;

    /**
     * Constructor.
     *
     * @param x1 Coordinate x1 (Lower left).
     * @param y1 Coordinate y1 (Lower left).
     * @param x2 Coordinate x2 (Upper right).
     * @param y2 Coordinate y2 (Upper right).
     */
    public PDFRectangle(float x1, float y1, float x2, float y2) {
        this();
    }

    /**
     * Constructor.
     *
     * @param x1 Coordinate x1 (Lower left).
     * @param y1 Coordinate y1 (Lower left).
     * @param x2 Coordinate x2 (Upper right).
     * @param y2 Coordinate y2 (Upper right).
     * @param rotation Rotation in degrees (multiples fo 90 only).
     */
    public PDFRectangle(float x1, float y1, float x2, float y2, int rotation) {
        this();
    }

    /**
     * Constructor. (x1, y1) will be (0, 0).
     *
     * @param x2 Coordinate x2 (Upper right).
     * @param y2 Coordinate y2 (Upper right).
     */
    public PDFRectangle(float x2, float y2) {
        this();
    }

    /**
     * Constructor. (x1, y1) will be (0, 0).
     *
     * @param x2 Coordinate x2 (Upper right).
     * @param y2 Coordinate y2 (Upper right).
     * @param rotation Rotation in degrees (multiples fo 90 only).
     */
    public PDFRectangle(float x2, float y2, int rotation) {
        this();
    }

    /**
     * Construct from another.
     *
     * @param another Another instance.
     */
    public PDFRectangle(PDFRectangle another) {
        this();
    }

    private PDFRectangle() {
    }

    /**
     * Create a rotated instance. (by 90 degree)
     *
     * @return Rotated instance.
     */
    public PDFRectangle rotate() {
        return null;
    }

    /**
     * Set the border thickness.
     *
     * @param thickness Border thickness.
     */
    public void setBorderWidth(float thickness) {
    }

    /**
     * Set borders. (Can be ORed values of {@link #TOP}, {@link #BOTTOM} etc.)
     *
     * @param borders Borders.
     */
    public void setBorder(int borders) {
    }

    /**
     * Get the width.
     *
     * @return Width.
     */
    public float getWidth() {
        return 0;
    }

    /**
     * Get the height.
     *
     * @return Height.
     */
    public float getHeight() {
        return 0;
    }

    /**
     * Set the gray fill value (between 0.0 and 1.0).
     *
     * @param fillValue Gray fill value.
     */
    public void setGrayFill(float fillValue) {
    }

    /**
     * Set rotation in degrees.
     *
     * @param rotation Rotation (multiples of 90).
     */
    public void setRotation(int rotation) {
    }

    /**
     * Set the left value.
     *
     * @param value Left.
     */
    public void setLeft(float value) {
    }

    public float getLeft() {
        return 0;
    }

    /**
     * Set the right value.
     *
     * @param value Right.
     */
    public void setRight(float value) {
    }

    /**
     * Get the right value.
     *
     * @return Right.
     */
    public float getRight() {
        return 0;
    }

    /**
     * Set the top value.
     *
     * @param value Top.
     */
    public void setTop(float value) {
    }

    /**
     * Get the top value.
     *
     * @return Top.
     */
    public float getTop() {
        return 0;
    }

    /**
     * Set the bottom value.
     *
     * @param value Bottom.
     */
    public void setBottom(float value) {
    }

    /**
     * Get the bottom value.
     *
     * @return Bottom.
     */
    public float getBottom() {
        return 0;
    }

    /**
     * Get the rotation value.
     *
     * @return Rotation.
     */
    public int getRotation() {
        return 0;
    }

    /**
     * Get the background color.
     *
     * @return Background color.
     */
    public PDFColor getBackgroundColor() {
        return null;
    }

    /**
     * Set the background color.
     *
     * @param color Color.
     */
    public void setBackgroundColor(PDFColor color) {
    }

    /**
     * Get the gray fill value.
     *
     * @return Gray fill.
     */
    public float getGrayFill() {
        return 0;
    }

    /**
     * Get the border value.
     *
     * @return Border.
     */
    public int getBorder() {
        return 0;
    }

    /**
     * Get the border thickness.
     *
     * @return Border thickness.
     */
    public float getBorderWidth() {
        return 0;
    }

    /**
     * Get the thickness of the left border.
     *
     * @return Border thickness.
     */
    public float getBorderWidthLeft() {
        return 0;
    }

    /**
     * Set the thickness of the left border.
     *
     * @param thickness Thickness.
     */
    public void setBorderWidthLeft(float thickness) {
    }

    /**
     * Get the thickness of the right border.
     *
     * @return Border thickness.
     */
    public float getBorderWidthRight() {
        return 0;
    }

    /**
     * Set the thickness of the right border.
     *
     * @param thickness Thickness.
     */
    public void setBorderWidthRight(float thickness) {
    }

    /**
     * Get the thickness of the top border.
     *
     * @return Border thickness.
     */
    public float getBorderWidthTop() {
        return 0;
    }

    /**
     * Set the thickness of the top border.
     *
     * @param thickness Thickness.
     */
    public void setBorderWidthTop(float thickness) {
    }

    /**
     * Get the thickness of the bottom border.
     *
     * @return Border thickness.
     */
    public float getBorderWidthBottom() {
        return 0;
    }

    /**
     * Set the thickness of the bottom border.
     *
     * @param thickness Thickness.
     */
    public void setBorderWidthBottom(float thickness) {
    }

    /**
     * Get the border color.
     *
     * @return Color.
     */
    public PDFColor getBorderColor() {
        return null;
    }

    /**
     * Set the border color.
     *
     * @param color Color.
     */
    public void setBorderColor(PDFColor color) {
    }

    /**
     * Get the color for the left border.
     *
     * @return Color.
     */
    public PDFColor getBorderColorLeft() {
        return null;
    }

    /**
     * Set the color for the left border.
     *
     * @param color Color.
     */
    public void setBorderColorLeft(PDFColor color) {
    }

    /**
     * Get the color for the right border.
     *
     * @return Color.
     */
    public PDFColor getBorderColorRight() {
        return null;
    }

    /**
     * Set the color for the right border.
     *
     * @param color Color.
     */
    public void setBorderColorRight(PDFColor color) {
    }

    /**
     * Get the color for the top border.
     *
     * @return Color.
     */
    public PDFColor getBorderColorTop() {
        return null;
    }

    /**
     * Set the color for the top border.
     *
     * @param color Color.
     */
    public void setBorderColorTop(PDFColor color) {
    }

    /**
     * Get the color for the bottom border.
     *
     * @return Color.
     */
    public PDFColor getBorderColorBottom() {
        return null;
    }

    /**
     * Set the color for the bottom border.
     *
     * @param color Color.
     */
    public void setBorderColorBottom(PDFColor color) {
    }
}
