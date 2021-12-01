package com.storedobject.ui;

import com.storedobject.vaadin.DataList;

import java.util.function.Predicate;

public class ListProvider<T> extends AbstractListProvider<T> {

    private Predicate<T> filter;

    public ListProvider(Class<T> dataClass, DataList<T> data) {
        super(dataClass, data);
    }

    @Override
    void saveFilter(Predicate<T> filter) {
        this.filter = filter;
    }

    @Override
    Predicate<T> retrieveFilter() {
        return filter;
    }
}
