package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.AbstractObjectForest;
import com.storedobject.ui.TemplateText;
import com.storedobject.vaadin.HasColumns;
import com.storedobject.vaadin.ObjectColumnCreator;
import com.vaadin.flow.component.grid.ColumnTextAlign;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class SOColumnCreator<T> implements ObjectColumnCreator<T> {

    private HasColumns<T> grid;
    private ClassAttribute<?> ca;
    private Map<String, Class<?>> valueTypes;

    private Class<?> getObjectClass() {
        if(grid instanceof AbstractObjectForest) {
            return ((AbstractObjectForest<?>) grid).getObjectClass();
        }
        return grid.getDataClass();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectColumnCreator<T> create(HasColumns<T> grid) {
        SOColumnCreator<T> occ = new SOColumnCreator<>();
        occ.grid = grid;
        Class<? extends StoredObject> c = (Class<? extends StoredObject>) occ.getObjectClass();
        if(StoredObject.class.isAssignableFrom(c)) {
            occ.ca = StoredObjectUtility.classAttribute(c);
            occ.valueTypes = new HashMap<>();
        }
        return occ;
    }

    @Override
    public Stream<String> getColumnNames() {
        if(ca != null) {
            return StoredObjectUtility.browseColumns(ca.getObjectClass()).stream();
        }
        return ObjectColumnCreator.super.getColumnNames();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Function<T, ?> getColumnFunction(String columnName) {
        if(ca != null) {
            if(columnName.contains("${")) {
                valueTypes.put(columnName, String.class);
                return new TemplateText<>((Class<T>)ca.getObjectClass(), columnName);
            }
            StoredObjectUtility.MethodList m;
            try {
                m = StoredObjectUtility.createMethodList(getObjectClass(), columnName);
                m.stringifyTail();
                valueTypes.put(columnName, m.getReturnType());
                return (Function<T, ?>) m.function();
            } catch (Throwable ignored) {
            }
        }
        return ObjectColumnCreator.super.getColumnFunction(columnName);
    }

    @Override
    public ColumnTextAlign getColumnTextAlign(String columnName, Class<?> valueType) {
        if(Utility.isRightAligned(valueType)) {
            return ColumnTextAlign.END;
        }
        return ObjectColumnCreator.super.getColumnTextAlign(columnName, valueType);
    }

    @Override
    public Class<?> getColumnValueType(String columnName) {
        if(valueTypes == null) {
            return null;
        }
        return valueTypes.get(columnName);
    }

    @Override
    public void close() {
        ca = null;
        if(valueTypes != null) {
            valueTypes.clear();
            valueTypes = null;
        }
    }
}