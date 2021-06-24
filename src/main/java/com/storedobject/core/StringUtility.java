package com.storedobject.core;

import com.storedobject.core.annotation.Table;

import java.math.BigInteger;

public class StringUtility extends com.storedobject.common.StringUtility {

    /**
     * Make a label for the class. Example: com.storedobject.core.SystemUser =&gt; "System User". In the case of data classes,
     * if a "Table" annotation exists, title will be picked up from the annotation.
     * @param c Class for which label to be created
     * @return Label
     */
    public static String makeLabel(Class<?> c) {
        return makeLabel(c, false);
    }

    /**
     * Make a label for the class. Example: com.storedobject.core.SystemUser =&gt; "System User". In the case of data classes,
     * if a "Table" annotation exists, title will be picked up from the annotation.
     * @param c Class for which label to be created
     * @param ignoreTableAnnotation Whether to ignore table annotation or not
     * @return Label
     */
    public static String makeLabel(Class<?> c, boolean ignoreTableAnnotation) {
        if(!ignoreTableAnnotation) {
            Table table = c.getAnnotation(Table.class);
            if(table != null && !table.title().isEmpty()) {
                return table.title();
            }
        }
        String name = c.getName();
        return StringUtility.makeLabel(name.substring(name.lastIndexOf('.') + 1));
    }

    /**
     * Converts a numeric value into words. Negative part will be ignored (If the {@link SystemEntity} is in India, it will
     * be done in Indian style).
     *
     * @param value Value
     * @return Value in words.
     */
    public static String words(BigInteger value) {
        return com.storedobject.common.StringUtility.words(value);
    }

    /**
     * Formats a string as a numeric string with thousands separation (If the {@link SystemEntity} is in India, it will
     * be done in Indian style).
     *
     * @param s String of digits (can contain a decimal point)
     * @param decimals Number of decimals required in the output string
     * @param separated True if thousands separation is needed in the output
     * @return Formatted value.
     */
    public static String format(String s, int decimals, boolean separated) {
        return com.storedobject.common.StringUtility.format(s, decimals, separated);
    }
}