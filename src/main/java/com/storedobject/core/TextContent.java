package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

/**
 * Text Content of some sort.
 *
 * @author Syam
 */
public class TextContent extends StoredObject {

	private String name, content;
	private int version = 0;
	private String notes;

	/**
	 * Constructor
	 *
	 * @param name Name
	 */
	public TextContent(String name) {
		this.name = name;
	}

	/**
	 * Constructor
	 */
	public TextContent() {
	}

	public static void columns(Columns columns) {
		columns.add("Name", "text");
		columns.add("Version", "int");
		columns.add("Notes", "text");
		columns.add("Content", "text");
	}

	public static String[] displayColumns() {
		return new String[] { "Name" };
	}

	public static String[] browseColumns() {
		return new String[] { "Name" };
	}

	public static void indices(Indices indices) {
		indices.add("lower(Name)", true);
	}

	@Override
	public String getUniqueCondition() {
		return "lower(Name)='" + name.toLowerCase().replace("'", "''") + "'";
	}

	public static TextContent create(String name) {
		TextContent tc = get(name);
		if(tc != null) {
			if(tc.name.equalsIgnoreCase(name)) {
				tc.name = name;
			} else {
				tc = null;
			}
		}
		return tc == null ? new TextContent(name) : tc;
	}

	/**
	 * Retrieves a {@code TextContent} object based on the specified name.
	 *
	 * @param name The name of the {@code TextContent} to retrieve.
	 * @return The {@code TextContent} object corresponding to the specified name,
	 *         or {@code null} if no matching object is found.
	 */
	public static TextContent get(String name) {
		return StoredObjectUtility.get(TextContent.class, "Name", name);
	}

	/**
	 * Retrieves an iterator of {@link TextContent} objects that match the specified name.
	 *
	 * @param name The name to filter the {@link TextContent} objects by.
	 * @return An iterator over {@link TextContent} objects with the specified name.
	 */
	public static ObjectIterator<? extends TextContent> list(String name) {
		return StoredObjectUtility.list(TextContent.class, "Name", name);
	}

	/**
	 * Gets the name of the Text Content
	 *
	 * @return The name
	 */
	@Column(order = 1)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Text Content.
	 *
	 * @param name The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the version of the Text Content. Version is automatically incremented whenever the content is modified.
	 *
	 * @return The version number.
	 */
	@SetNotAllowed
	@Column(required = false, order = 2)
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version of the Text Content. This action is only allowed if the instance is currently being
	 * loaded from the database. If the instance is not loading, an exception will be thrown.
	 *
	 * @param version The version number to set.
	 * @throws Set_Not_Allowed If the version is modified while the instance is not loading.
	 */
	public void setVersion(int version) {
		if(!loading()) {
			throw new Set_Not_Allowed("Version");
		}
		this.version = version;
	}

	/**
	 * Retrieves the notes associated with the text content.
	 *
	 * @return The notes as a string, or null if no notes are set.
	 */
    @Column(required = false, order = 3, style = "(large)")
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the notes associated with the text content.
	 *
	 * @param notes The notes to set, represented as a string.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

    /**
	 * Retrieves the content associated with the TextContent instance.
	 *
	 * @return The content as a string, or null if no content is set.
	 */
    @Column(required = false, order = 4, style = "(large)")
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content for the TextContent instance.
	 *
	 * @param content The content to set.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(StringUtility.isWhite(name)) {
			throw new Invalid_Value("Name");
		}
		name = name.trim();
		super.validateData(tm);
	}

	@Override
	public void validate() throws Exception {
		super.validate();
		if(updated() || inserted()) {
			++version;
		}
	}
}