package com.storedobject.pdf;

public class PDFColor {

    public static com.storedobject.pdf.PDFColor WHITE = null;
    public static com.storedobject.pdf.PDFColor LIGHT_GRAY = null;
    public static com.storedobject.pdf.PDFColor GRAY = null;
    public static com.storedobject.pdf.PDFColor DARK_GRAY = null;
    public static com.storedobject.pdf.PDFColor BLACK = null;
    public static com.storedobject.pdf.PDFColor RED = null;
    public static com.storedobject.pdf.PDFColor PINK = null;
    public static com.storedobject.pdf.PDFColor ORANGE = null;
    public static com.storedobject.pdf.PDFColor YELLOW = null;
    public static com.storedobject.pdf.PDFColor GREEN = null;
    public static com.storedobject.pdf.PDFColor MAGENTA = null;
    public static com.storedobject.pdf.PDFColor CYAN = null;
    public static com.storedobject.pdf.PDFColor BLUE = null;

    public PDFColor(int red, int green, int blue, int alpha) {
        this();
    }

    public PDFColor(int red, int grees, int blue) {
        this();
    }

    private PDFColor() {
    }

    @Override
	public int hashCode() {
        return 0;
    }

    public int getRed() {
        return 0;
    }

    public int getGreen() {
        return 0;
    }

    public int getBlue() {
        return 0;
    }

    public int getAlpha() {
        return 0;
    }

    public int getRGB() {
        return 0;
    }

    public com.storedobject.pdf.PDFColor brighter() {
        return null;
    }

    public com.storedobject.pdf.PDFColor darker() {
        return null;
    }
}
