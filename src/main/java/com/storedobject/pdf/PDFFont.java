package com.storedobject.pdf;

/**
 * Representation of "PDF font".
 *
 * @author Syam
 */
public class PDFFont {

    /**
     * Languages.
     */
	public enum Language {
		ENGLISH,
		HINDI,
		ARABIC,
	}

    /**
     * Font family.
     */
    public enum FontFamily {
        COURIER,
        HELVETICA,
        TIMES_ROMAN,
        SYMBOL,
        ZAPFDINGBATS,
        UNDEFINED,
    }

    /**
     * Font styles.
     */
    public enum FontStyle {
        NORMAL,
        BOLD,
        ITALIC,
        OBLIQUE,
        UNDERLINE,
        LINE_THROUGH,
    }

    /**
     * Constant used in font specification.
     */
    public static int NORMAL = 0;
    /**
     * Constant used in font specification.
     */
    public static int BOLD = 0;
    /**
     * Constant used in font specification.
     */
    public static int ITALIC = 0;
    /**
     * Constant used in font specification.
     */
    public static int UNDERLINE = 0;
    /**
     * Constant used in font specification.
     */
    public static int STRIKE_THROUGH = 0;
    /**
     * Constant used in font specification.
     */
    public static int BOLD_ITALIC = 0;
    /**
     * Constant used in font specification.
     */
    public static int UNDEFINED = 0;
    /**
     * Constant used in font specification.
     */
    public static int DEFAULTSIZE = 0;

    /**
     * Construct from another font.
     *
     * @param font Font to copy.
     */
    public PDFFont(PDFFont font) {
        this();
    }

    /**
     * Constructor.
     *
     * @param family Family.
     * @param size Size.
     * @param style Style.
     * @param color Color.
     */
    public PDFFont(PDFFont.FontFamily family, float size, int style, PDFColor color) {
        this();
    }

    /**
     * Constructor.
     *
     * @param family Family.
     * @param size Size.
     * @param style Style.
     */
    public PDFFont(PDFFont.FontFamily family, float size, int style) {
        this();
    }

    /**
     * Constructor.
     *
     * @param family Family.
     * @param size Size.
     */
    public PDFFont(PDFFont.FontFamily family, float size) {
        this();
    }

    /**
     * Constructor.
     *
     * @param family Family.
     */
    public PDFFont(PDFFont.FontFamily family) {
        this();
    }

    /**
     * Constructor.
     */
    public PDFFont() {
    }

    /**
     * Get the size.
     *
     * @return Size.
     */
    public float getSize() {
        return 0;
    }

    /**
     * Set the size.
     *
     * @param size Size to set.
     */
    public void setSize(float size) {
    }

    /**
     * Get the style.
     *
     * @return Style.
     */
    public int getStyle() {
        return 0;
    }

    /**
     * Set the color.
     *
     * @param color Color to set.
     */
    public void setColor(PDFColor color) {
    }

    /**
     * Set the color from RGB values.
     *
     * @param red Red value.
     * @param green Green value.
     * @param blue Blue value.
     */
    public void setColor(int red, int green, int blue) {
    }

    /**
     * Get the color.
     *
     * @return Color.
     */
    public PDFColor getColor() {
        return null;
    }

    /**
     * Get the font family.
     *
     * @return Family.
     */
    public FontFamily getFamily() {
        return null;
    }

    /**
     * Get the font family for the given name.
     *
     * @param name Name.
     * @return Font family.
     */
    public static FontFamily getFamily(String name) {
        return null;
    }

    /**
     * Get the font family name.
     *
     * @return Name of the font family.
     */
    public String getFamilyName() {
        return null;
    }

    /**
     * Set the font family from the name given.
     *
     * @param name Name of the font family to set.
     */
    public void setFamily(String name) {
    }

    /**
     * Is this bold?
     *
     * @return True/false.
     */
    public boolean isBold() {
        return false;
    }

    /**
     * Is this italic?
     *
     * @return True/false.
     */
    public boolean isItalic() {
        return false;
    }

    /**
     * Is this underlined?
     *
     * @return True/false.
     */
    public boolean isUnderlined() {
        return false;
    }

    /**
     * Is this strikethrough?
     *
     * @return True/false.
     */
    public boolean isStrikethrough() {
        return false;
    }

    /**
     * Set the style.
     *
     * @param style Style.
     */
    public void setStyle(int style) {
    }

    /**
     * Set the style via style name.
     *
     * @param style Name of the style to set.
     */
    public void setStyle(String style) {
    }
}
