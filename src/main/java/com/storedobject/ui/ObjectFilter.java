package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Form;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectFilter<T extends StoredObject> extends Form implements ObjectSearchBuilder<T> {

    public ObjectFilter(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ObjectFilter(Class<T> objectClass, StringList columns) {
    }

    public ObjectFilter(Class<T> objectClass, StringList columns, Consumer<ObjectSearchBuilder<T>> changeConsumer) {
    }

    public static <O extends StoredObject> ObjectFilter<O> create(Class<O> objectClass, StringList columns) {
        return null;
    }

    @Override
    public int getSearchFieldCount() {
        return 0;
    }

    @Override
    public boolean addSearchField(String fieldName) {
        return false;
    }

    @Override
    public boolean removeSearchField(String fieldName) {
        return false;
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public String getFilterText() {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }
}