package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectForestViewerSupplier<T extends StoredObject> implements AbstractObjectForestSupplier<T> {

    private final ObjectForestSupplier<T> supplier;

    public ObjectForestViewerSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        supplier = new ObjectForestSupplier<>(objectClass, condition, orderBy, any);
    }

    public ObjectForestViewerSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        supplier = new ObjectForestSupplier<>(linkType, master, objectClass, condition, orderBy, any);
    }

    @Override
    public void close() {
        supplier.close();
    }

    @Override
    public boolean isAllowAny() {
        return supplier.isAllowAny();
    }

    @Override
    public void load(String filterClause, String orderBy) {
        supplier.load(filterClause, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String filterClause, String orderBy) {
        supplier.load(linkType, master, filterClause, orderBy);
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        supplier.load(objects);
    }

    @Override
    public boolean isFullyLoaded() {
        return supplier.isFullyLoaded();
    }

    @Override
    public void setFilter(FilterProvider filterProvider) {
        supplier.setFilter(filterProvider);
    }

    @Override
    public void setFilter(String extraFilterClause) {
        supplier.setFilter(extraFilterClause);
    }

    @Override
    public void setFilter(Predicate<T> filter) {
        supplier.setFilter(filter);
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return supplier.getFilter();
    }

    @Override
    public int indexOf(T object) {
        return supplier.indexOf(object);
    }

    @Override
    public List<T> listRoots() {
        return supplier.listRoots();
    }

    @Override
    public T getItem(int index) {
        return supplier.getItem(index);
    }

    @Override
    public boolean isFullyCached() {
        return supplier.isFullyCached();
    }

    @Override
    public int getObjectCount() {
        return supplier.getObjectCount();
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, String> hierarchicalQuery) {
        return 0;
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> hierarchicalQuery) {
        return null;
    }

    @Override
    public boolean hasChildren(Object o) {
        return false;
    }

    @Override
    public boolean isInMemory() {
        return supplier.isInMemory();
    }

    @Override
    public void refreshItem(Object o) {
        supplier.refreshItem(o);
    }

    @Override
    public void refreshAll() {
        supplier.refreshAll();
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<Object> dataProviderListener) {
        return null;
    }
}
