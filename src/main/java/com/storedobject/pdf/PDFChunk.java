package com.storedobject.pdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of "PDF chunk".
 *
 * @author Syam
 */
public class PDFChunk implements PDFElement {

    /**
     * Constructor.
     */
    public PDFChunk() {
    }

    /**
     * Construct from another chunk.
     *
     * @param chunk Create from this chunk.
     */
    public PDFChunk(PDFChunk chunk) {
        this();
    }

    /**
     * Construct from a given text.
     *
     * @param text Text to include.
     * @param font Font to use.
     */
    public PDFChunk(String text, PDFFont font) {
        this();
    }

    /**
     * Construct from a given text.
     *
     * @param text Text to include.
     */
    public PDFChunk(String text) {
        this();
    }

    /**
     * Construct from a given character.
     *
     * @param c Character to include.
     * @param font Font to use.
     */
    public PDFChunk(char c, PDFFont font) {
        this();
    }

    /**
     * Construct from a given character.
     *
     * @param c Character to include.
     */
    public PDFChunk(char c) {
        this();
    }

    /**
     * Construct from a given image.
     *
     * @param image Image to include.
     * @param xOffset X offset.
     * @param yOffset Y offset.
     */
    public PDFChunk(PDFImage image, float xOffset, float yOffset) {
        this();
    }

    /**
     * Append to the text content.
     *
     * @param text Text to append.
     * @return String builder to chain.
     */
    public StringBuilder append(String text) {
        return null;
    }

    /**
     * Is this chunk empty?
     *
     * @return True/false.
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Does this contain only "white spaces"?
     *
     * @return True/false.
     */
    public boolean isWhitespace() {
        return false;
    }

    /**
     * Get the image in this chunk.
     *
     * @return Image or <code>null</code> if t=it doesn't exist.
     */
    public PDFImage getImage() {
        return null;
    }

    /**
     * Get the list of chunks within this.
     *
     * @return List of chunks.
     */
	public List<PDFChunk> getChunks() {
        return new ArrayList<>();
    }

    /**
     * Set the font.
     *
     * @param font Font to set.
     */
    public void setFont(PDFFont font) {
    }

    /**
     * Set the background color
     *
     * @param color Background to set.
     * @return Self.
     */
    public PDFChunk setBackground(PDFColor color) {
        return null;
    }

    /**
     * Set the background color
     *
     * @param color Background to set.
     * @param extraLeft Extra portion on the left.
     * @param extraBottom Extra portion at the bottom.
     * @param extraRight Extra portion on the right.
     * @param extraTop Extra portion at the top.
     */
    public void setBackground(PDFColor color, float extraLeft, float extraBottom, float extraRight, float extraTop) {
    }

    /**
     * Set the font.
     *
     * @return Font.
     */
    public PDFFont getFont() {
        return null;
    }

    /**
     * Get the character spacing.
     *
     * @return Character spacing.
     */
    public float getCharacterSpacing() {
        return 0;
    }

    /**
     * Get horizontal scaling.
     *
     * @return Horizontal scaling.
     */
    public float getHorizontalScaling() {
        return 0;
    }

    /**
     * Set the character spacing.
     *
     * @param spacing Character spacing to set.
     */
    public void setCharacterSpacing(float spacing) {
    }

    /**
     * Set the horizontal scaling.
     *
     * @param scaling Horizontal scaling to set.
     */
    public void setHorizontalScaling(float scaling) {
    }

    /**
     * Set the text-rise.
     *
     * @param rise Rise value for the text.
     */
    public void setTextRise(float rise) {
    }

    /**
     * Set to the new page.
     */
    public void setNewPage() {
    }

    /**
     * Set thickness for the underline.
     *
     * @param thickness Thickness.
     * @param yPosition Y position.
     */
    public void setUnderline(float thickness, float yPosition) {
    }

    /**
     * Get the text-rise value.
     *
     * @return Text-rise value.
     */
    public float getTextRise() {
        return 0;
    }

    /**
     * Set the skew angles.
     *
     * @param firstAngle First angle.
     * @param secondAngle Second angle.
     */
    public void setSkew(float firstAngle, float secondAngle) {
    }

    /**
     * Set the line height.
     *
     * @param height Line height.
     */
    public void setLineHeight(float height) {
    }
}
