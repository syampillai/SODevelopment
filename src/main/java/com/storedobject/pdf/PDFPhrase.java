package com.storedobject.pdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of "PDF phrase".
 *
 * @author Syam
 */
public class PDFPhrase extends ArrayList<PDFElement> {

    /**
     * Constructor.
     */
    public PDFPhrase() {
    }

    /**
     * Constructor.
     *
     * @param phrase Another phrase to include.
     */
    public PDFPhrase(PDFPhrase phrase) {
        this();
    }

    /**
     * Constructor.
     *
     * @param chunk Another chunk to include.
     */
    public PDFPhrase(PDFChunk chunk) {
        this();
    }

    /**
     * Constructor.
     *
     * @param text Text to include.
     */
    public PDFPhrase(String text) {
        this();
    }

    /**
     * Constructor.
     *
     * @param text Text to include.
     * @param font Font to use for the text.
     */
    public PDFPhrase(String text, PDFFont font) {
        this();
    }

    /**
     * Add text.
     *
     * @param text Text to add.
     * @return True if added.
     */
    public boolean add(String text) {
        return false;
    }

    /**
     * Create an instance for the given text.
     *
     * @param text Text to include.
     * @return An instance created.
     */
    public static PDFPhrase getInstance(String text) {
        return null;
    }

    /**
     * Get the list of chunks in this instance.
     *
     * @return List of chunks.
     */
    public List<PDFChunk> getChunks() {
        return new ArrayList<>();
    }

    /**
     * Set the font as the current font..
     * @param font Font to set.
     */
    public void setFont(PDFFont font) {
    }

    /**
     * Get the current font.
     *
     * @return Font.
     */
    public PDFFont getFont() {
        return null;
    }

    /**
     * Set the leading.
     *
     * @param leading Leading to set.
     */
    public void setLeading(final float leading) {
    }

    /**
     * Get the leading.
     *
     * @return The leading.
     */
    public float getLeading() {
    	return 0;
    }
}
