package com.storedobject.pdf;

public class PDFFont {

	public enum Language {
		ENGLISH,
		HINDI,
		ARABIC,
	}
	
    public enum FontFamily {
        COURIER,
        HELVETICA,
        TIMES_ROMAN,
        SYMBOL,
        ZAPFDINGBATS,
        UNDEFINED,
    }
    public enum FontStyle {
        NORMAL,
        BOLD,
        ITALIC,
        OBLIQUE,
        UNDERLINE,
        LINETHROUGH,
    }
    public static int NORMAL = 0;
    public static int BOLD = 0;
    public static int ITALIC = 0;
    public static int UNDERLINE = 0;
    public static int STRIKETHRU = 0;
    public static int BOLDITALIC = 0;
    public static int UNDEFINED = 0;
    public static int DEFAULTSIZE = 0;

    public PDFFont(com.storedobject.pdf.PDFFont font) {
        this();
    }

    public PDFFont(com.storedobject.pdf.PDFFont.FontFamily family, float size, int style, com.storedobject.pdf.PDFColor color) {
        this();
    }

    public PDFFont(com.storedobject.pdf.PDFFont.FontFamily family, float size, int style) {
        this();
    }

    public PDFFont(com.storedobject.pdf.PDFFont.FontFamily family, float size) {
        this();
    }

    public PDFFont(com.storedobject.pdf.PDFFont.FontFamily family) {
        this();
    }

    public PDFFont() {
    }

    public float getSize() {
        return 0;
    }

    public void setSize(float size) {
    }

    public int getStyle() {
        return 0;
    }

    public void setColor(com.storedobject.pdf.PDFColor color) {
    }

    public void setColor(int red, int green, int blue) {
    }

    public com.storedobject.pdf.PDFColor getColor() {
        return null;
    }

    public FontFamily getFamily() {
        return null;
    }

    public static FontFamily getFamily(java.lang.String name) {
        return null;
    }

    public java.lang.String getFamilyname() {
        return null;
    }

    public void setFamily(java.lang.String name) {
    }

    public int getCalculatedStyle() {
        return 0;
    }

    public boolean isBold() {
        return false;
    }

    public boolean isItalic() {
        return false;
    }

    public boolean isUnderlined() {
        return false;
    }

    public boolean isStrikethru() {
        return false;
    }

    public void setStyle(int style) {
    }

    public void setStyle(java.lang.String style) {
    }
}
