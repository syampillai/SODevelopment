package com.storedobject.pdf;

/**
 * Representation of "PDF element".
 *
 * @author Syam
 */
public interface PDFElement {

	/**
	 * Alignment - undefined.
	 */
	int ALIGN_UNDEFINED = -1;
	/**
	 * Alignment - left.
	 */
	int ALIGN_LEFT = 0;
	/**
	 * Alignment - center.
	 */
	int ALIGN_CENTER = 1;
	/**
	 * Alignment - right.
	 */
	int ALIGN_RIGHT = 2;
	/**
	 * Alignment - justified.
	 */
	int ALIGN_JUSTIFIED = 3;
	/**
	 * Alignment - top.
	 */
	int ALIGN_TOP = 4;
	/**
	 * Alignment - middle.
	 */
	int ALIGN_MIDDLE = 5;
	/**
	 * Alignment - bottom.
	 */
	int ALIGN_BOTTOM = 6;
	/**
	 * Alignment - base-line.
	 */
	int ALIGN_BASELINE = 7;
	/**
	 * Alignment - justified all.
	 */
	int ALIGN_JUSTIFIED_ALL = 8;
	/**
	 * Text direction - Default.
	 */
	int TEXT_DIRECTION_DEFAULT = 0;
	/**
	 * Text direction - No bidirectional reordering.
	 */
	int TEXT_DIRECTION_NO_BIDI = 1;
	/**
	 * Text direction - Bidirectional reordering from left to right.
	 */
	int TEXT_DIRECTION_LTR = 2;
	/**
	 * Text direction - Bidirectional reordering from right to left.
	 */
	int TEXT_DIRECTION_RTL = 3;
}
