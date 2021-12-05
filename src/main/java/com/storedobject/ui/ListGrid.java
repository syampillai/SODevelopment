package com.storedobject.ui;

import com.storedobject.core.MemoryCache;
import com.storedobject.core.Filtered;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.data.provider.ListDataProvider;

public class ListGrid<T> extends DataGrid<T> {

    public ListGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ListGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, new MemoryCache<>(), columns);
    }

    public ListGrid(Class<T> objectClass, Filtered<T> list, Iterable<String> columns) {
        super(objectClass, list, columns);
    }

    @Override
    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof ListProvider;
    }

    @Override
    protected ListProvider<T> createListDataProvider(DataList<T> data) {
        return new ListProvider<>(getDataClass(), data);
    }
}
