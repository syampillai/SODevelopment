package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
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
        super(objectClass, items, columns(objectClass, columns), caption(objectClass, caption));
    }

    private static String caption(Class<?> oClass, String caption) {
        if(caption != null && !caption.isEmpty()) {
            return caption;
        }
        return StringUtility.makeLabel(oClass);
    }

    static <O extends StoredObject> Iterable<String> columns(Class<?> objectClass, Iterable<String> columns) {
        if(columns != null) {
            return columns;
        }
        if(!StoredObject.class.isAssignableFrom(objectClass)) {
            return null;
        }
        @SuppressWarnings("unchecked") Class<O> oClass = (Class<O>) objectClass;
        return StoredObjectUtility.browseColumns(oClass);
    }
}
