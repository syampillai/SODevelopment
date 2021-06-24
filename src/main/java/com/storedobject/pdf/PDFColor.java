package com.storedobject.pdf;

/**
 * Represents a "PDF color".
 *
 * @author Syam
 */
public class PDFColor {

    public static PDFColor WHITE = null;
    public static PDFColor LIGHT_GRAY = null;
    public static PDFColor GRAY = null;
    public static PDFColor DARK_GRAY = null;
    public static PDFColor BLACK = null;
    public static PDFColor RED = null;
    public static PDFColor PINK = null;
    public static PDFColor ORANGE = null;
    public static PDFColor YELLOW = null;
    public static PDFColor GREEN = null;
    public static PDFColor MAGENTA = null;
    public static PDFColor CYAN = null;
    public static PDFColor BLUE = null;

    /**
     * Create a color from the RGB values and alpha (All values are 8 bits - 0 to 255).
     *
     * @param red Red value.
     * @param green Green value.
     * @param blue Blue value.
     * @param alpha Alpha value.
     */
    public PDFColor(int red, int green, int blue, int alpha) {
        this();
    }

    /**
     * Create a color from the RGB values and alpha = 1 (All values are 8 bits - 0 to 255).
     *
     * @param red Red value.
     * @param green Green value.
     * @param blue Blue value.
     */
    public PDFColor(int red, int green, int blue) {
        this();
    }

    private PDFColor() {
    }

    /**
     * Get the red value.
     *
     * @return Red value.
     */
    public int getRed() {
        return 0;
    }

    /**
     * Get the green value.
     *
     * @return Green value.
     */
    public int getGreen() {
        return 0;
    }

    /**
     * Get the blue value.
     *
     * @return Blue value.
     */
    public int getBlue() {
        return 0;
    }

    /**
     * Get the alpha value.
     *
     * @return Alpha value.
     */
    public int getAlpha() {
        return 0;
    }

    /**
     * Get the RGB value (32 bit value - each 8 bit part from the left represents Alpha, Red, Green, Blue
     * respectively).
     *
     * @return RGB value.
     */
    public int getRGB() {
        return 0;
    }

    /**
     * Create a bright color from this instance.
     *
     * @return Brighter color.
     */
    public PDFColor brighter() {
        return null;
    }

    /**
     * Create a darker color from this instance.
     *
     * @return Darker color.
     */
    public PDFColor darker() {
        return null;
    }
}
