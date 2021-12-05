package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

public abstract class DataTreeGrid<T> extends com.storedobject.vaadin.DataTreeGrid<T> implements Transactional {

    public DataTreeGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataTreeGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, cols(objectClass, columns));
    }

    private static <O extends StoredObject> Iterable<String> cols(Class<?> objectClass, Iterable<String> columns) {
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
