package com.storedobject.core;

import com.storedobject.core.annotation.Column;

/**
 * Text Content
 */
public class TextContent extends StoredObject {

	/**
	 * Constructor
	 *
	 * @param name Name
	 */
	public TextContent(String name) {
	}

	/**
	 * Constructor
	 */
	public TextContent() {
	}

	public static void columns(Columns columns) {
	}

	public static TextContent create(String name) {
		return null;
	}

	public static TextContent get(String name) {
		return null;
	}
	
	public static ObjectIterator<? extends TextContent> list(String name) {
		return null;
	}

	/**
	 * Gets the name of the Text Content
	 *
	 * @return The name
	 */
	public String getName() {
		return null;
	}

	/**
	 * Sets the name of the Text Content.
	 *
	 * @param name The new name.
	 */
	public void setName(String name) {
	}
	
	public int getVersion() {
		return 0;
	}

	public void setVersion(int version) {
	}

    @Column(required = false, style = "(large)")
	public String getNotes() {
		return null;
	}

	public void setNotes(String notes) {
	}

    @Column(required = false, style = "(large)")
	public String getContent() {
    	return null;
	}

	public void setContent(String content) {
	}
}