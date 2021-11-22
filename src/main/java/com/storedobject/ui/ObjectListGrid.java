package com.storedobject.ui;

import com.storedobject.core.ObjectCacheList;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.DataList;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.provider.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ObjectListGrid<T extends StoredObject> extends ListGrid<T> implements ObjectLoader<T> {

    private GridListDataView<T> dataView;

    public ObjectListGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectListGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectListGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectListGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, new ObjectCacheList<>(objectClass, any), columns);
    }

    @Override
    public final GridListDataView<T> setItems(ListDataProvider<T> dataProvider) {
        if(dataProvider instanceof ObjectListDataProvider) {
            dataView = super.setItems(dataProvider);
        }
        return dataView;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback) {
        return null;
    }

    @Override
    public final GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        return null;
    }

    @Override
    public final GridDataView<T> setItems(InMemoryDataProvider<T> inMemoryDataProvider) {
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                        CallbackDataProvider.CountCallback<T, Void> countCallback) {
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(BackEndDataProvider<T, Void> dataProvider) {
        return null;
    }

    @SafeVarargs
    @Override
    public final GridListDataView<T> setItems(T... items) {
        load(items);
        return dataView;
    }

    @Override
    public final GridListDataView<T> setItems(Collection<T> items) {
        load(items);
        return super.setItems(items);
    }

    @Override
    protected ListDataProvider<T> createListDataProvider(DataList<T> data) {
        return new ObjectListDataProvider<>(data);
    }

    @Override
    public ObjectListDataProvider<T> getDataProvider() {
        return (ObjectListDataProvider<T>) super.getDataProvider();
    }

    @Override
    public final boolean isAllowAny() {
        return getDataProvider().isAllowAny();
    }

    @Override
    public void load(String condition, String orderBy) {
        getDataProvider().load(condition, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        getDataProvider().load(linkType, master, condition, orderBy);
    }

    @Override
    public final void load(ObjectIterator<T> objects) {
        getDataProvider().load(objects);
    }

    @Override
    public void added(T item) {
        getDataProvider().added(item);
    }

    @Override
    public void edited(T item) {
        getDataProvider().edited(item);
    }

    @Override
    public void deleted(T item) {
        getDataProvider().deleted(item);
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        getDataProvider().getData().sort(comparator);
        refresh();
    }

    @Override
    public void sort(List<GridSortOrder<T>> order) {
        super.sort(order);
    }
}
