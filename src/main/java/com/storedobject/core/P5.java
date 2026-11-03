package com.storedobject.core;

/**
 * Text content of P5.js (JS version of the Processing Language) type.
 *
 * @author Syam
 */
public class P5 extends TextContent {

	/**
	 * Constructs a new instance of the P5 class with the specified name.
	 *
	 * @param name The name to be associated with the P5 instance.
	 */
	public P5(String name) {
		super(name);
	}

	/**
	 * Default constructor for the P5 class, which represents the text content
	 * specific to the P5.js environment, a JavaScript version of the Processing language.
	 * Initializes an instance of P5 with default properties.
	 */
	public P5() {
	}
	
	public static void columns(@SuppressWarnings("unused") Columns columns) {
	}

	/**
	 * Creates an instance of the P5 class with the specified name. If an existing
	 * instance with the same name (case insensitive) is found, the existing instance
	 * is updated with the provided name. If no matching instance exists, a new one
	 * is created and returned.
	 *
	 * @param name The name to be associated with the P5 instance. Must not be null.
	 * @return An existing P5 instance with the updated name or a new instance of P5
	 *         if no matching object was found.
	 */
	public static P5 create(String name) {
		P5 tc = get(name);
		if(tc != null) {
			if(tc.getName().equalsIgnoreCase(name)) {
				tc.setName(name);
			} else {
				tc = null;
			}
		}
		return tc == null ? new P5(name) : tc;
	}

	/**
	 * Retrieves an instance of {@code P5} identified by the specified name.
	 *
	 * @param name the name identifier used to retrieve the {@code P5} object.
	 * @return the {@code P5} object corresponding to the specified name, or {@code null} if not found.
	 */
	public static P5 get(String name) {
		return StoredObjectUtility.get(P5.class, "Name", name);
	}

	/**
	 * Retrieves an iterator over P5 objects that match the specified name.
	 *
	 * @param name The name to filter the P5 objects by.
	 * @return An ObjectIterator of P5 objects that match the specified name.
	 */
	public static ObjectIterator<P5> list(String name) {
		return StoredObjectUtility.list(P5.class, "Name", name);
	}
	
	/**
	 * Retrieves the code content associated with the P5 instance.
	 *
	 * @return The code as a string, or null if no code is set.
	 */
	public String getCode() {
		return getContent();
	}
	
	/**
	 * Sets the code content for the P5 instance.
	 *
	 * @param code The code content to set.
	 */
	public void setCode(String code) {
		setContent(code);
	}
}
