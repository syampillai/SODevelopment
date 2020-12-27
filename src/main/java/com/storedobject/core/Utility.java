package com.storedobject.core;

/**
 * General utility methods.
 *
 * @author Syam
 */
public class Utility {

    /**
     * Is the given value right-aligned? (Examples of right-aligned values are numeric values, quantity etc.) The
     * notion of right-alignment depends on the locale. Here, the meaning is as per left-to-right language convention.
     * <p>Note: A null value is considered as not right-aligned.</p>
     * @param value Value to check.
     * @return True if right-aligned, otherwise false.
     */
    public static boolean isRightAligned(Object value) {
        if(value == null) {
            return false;
        }
        Class<?> valueType = value.getClass();
        return valueType == int.class || valueType == long.class || valueType == double.class ||
                valueType == float.class || valueType == byte.class || valueType == short.class ||
                Number.class.isAssignableFrom(valueType) ||
                Rate.class.isAssignableFrom(valueType) ||
                Money.class.isAssignableFrom(valueType) ||
                Quantity.class.isAssignableFrom(valueType) ||
                DecimalNumber.class.isAssignableFrom(valueType);
    }
}
