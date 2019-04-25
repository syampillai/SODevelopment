package com.storedobject.core;

import com.storedobject.core.annotation.Table;

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
}