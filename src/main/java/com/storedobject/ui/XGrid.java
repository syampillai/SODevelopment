package com.storedobject.ui;

import com.storedobject.vaadin.DataGrid;

import java.util.function.Function;

public class XGrid<X extends XGrid.XData> extends DataGrid<X> {

    public XGrid(Class<X> objectClass) {
        this(objectClass,null);
    }

    public XGrid(Class<X> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    @Override
    public Function<X, ?> getColumnFunction(String columnName) {
        return x -> {
            Object value = x.getDataValue(columnName);
            return convertValue(value, x, columnName);
        };
    }

    @SuppressWarnings("unused")
    public Object convertValue(Object value, X item, String columnName) {
        return value == null ? "?" : value;
    }

    @FunctionalInterface
    public interface XData {
        Object getDataValue(String columnName);
    }
}