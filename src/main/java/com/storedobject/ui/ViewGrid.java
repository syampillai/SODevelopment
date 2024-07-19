package com.storedobject.ui;

import com.storedobject.core.StringUtility;

import java.util.List;

public class ViewGrid<T> extends com.storedobject.vaadin.ViewGrid<T> {

    public ViewGrid(Class<T> objectClass, List<T> items) {
        this(objectClass, items, (String) null);
    }

    public ViewGrid(Class<T> objectClass, List<T> items, Iterable<String> columns) {
        this(objectClass, items, columns, null);
    }

    public ViewGrid(Class<T> objectClass, List<T> items, String caption) {
        this(objectClass, items, null, caption);
    }

    public ViewGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, String caption) {
        super(objectClass, items, DataGrid.columns(objectClass, columns), caption(objectClass, caption));
    }

    private static String caption(Class<?> oClass, String caption) {
        if(caption != null && !caption.isEmpty()) {
            return caption;
        }
        return StringUtility.makeLabel(oClass);
    }
}
