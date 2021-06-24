package com.storedobject.core;

import java.util.Date;

/**
 * General utility methods.
 *
 * @author Syam
 */
public class Utility {

    /**
     * Blank time: The local time Jan 1, 1800 0:00 is considered blank by the platform for internal purposes.
     */
    public static final long BLANK_TIME = -5364662400000L;

    /**
     * Is the given value right-aligned? (Examples of right-aligned values are numeric values, quantity etc.) The
     * notion of right-alignment depends on the locale. Here, the meaning is as per left-to-right language convention.
     * <p>Note: A null value is considered as not right-aligned.</p>
     * @param value Value to check.
     * @return True if right-aligned, otherwise false.
     */
    public static boolean isRightAligned(Object value) {
        return value != null;
    }

    /**
     * Check whether the given date/time is empty/blank or not.
     *
     * @param dateTime Date/timestamp to check.
     * @param <D> Type of date/timestamp.
     * @return True if empty/blank, otherwise false.
     */
    public static <D extends Date> boolean isEmpty(D dateTime) {
        return dateTime == null || dateTime.getTime() == BLANK_TIME;
    }
}
