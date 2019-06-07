package com.storedobject.ui;

import com.storedobject.vaadin.HTMLGenerator;

import java.util.function.Function;

public class DataGrid<T> extends com.storedobject.vaadin.DataGrid<T> implements Transactional {

    public DataGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    @Override
    public void setRendererFunctions(String columnName, boolean html, Function<T, ?>... functions) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    class HTMLFunction implements Function<T, HTMLGenerator> {

        @Override
        public HTMLGenerator apply(T object) {
            return null;
        }
    }

    public void print() {
    }

    protected GridCellText getGridCellText() {
        return null;
    }
}