package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.vaadin.DataTreeGrid;

public abstract class AbstractTreeGrid<T> extends DataTreeGrid<T> implements Transactional {

    public AbstractTreeGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public AbstractTreeGrid(Class<T> objectClass, Iterable<String> columns) {
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
