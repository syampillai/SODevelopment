package com.storedobject.ui;

public abstract class DataTreeGrid<T> extends com.storedobject.vaadin.DataTreeGrid<T> implements Transactional {

    public DataTreeGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataTreeGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, DataGrid.columns(objectClass, columns));
    }
}
