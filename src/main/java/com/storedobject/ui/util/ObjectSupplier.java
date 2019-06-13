package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectSupplier<T extends StoredObject> extends CallbackDataProvider<T, String> implements ObjectDataProvider<T> {

    public ObjectSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any);
    }

    public ObjectSupplier(Class<T> objectClass, String condition, String orderBy, boolean any, boolean load) {
        this(0, null, objectClass, condition, orderBy, any, load);
    }

    public ObjectSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any, false);
    }

    @SuppressWarnings("unchecked")
    public ObjectSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any, boolean load) {
        this(new Supplier(linkType, master, objectClass, condition, orderBy, any), load);
    }

    @SuppressWarnings("unchecked")
    private ObjectSupplier(Supplier supplier, boolean load) {
        super(supplier.new Fetcher(), supplier.new Counter(), StoredObject::getId);
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
    }

    @Override
    public void load(String condition, String orderBy) {
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
    }

    @Override
    public void load(ObjectIterator<T> objects) {
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public void filter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return null;
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void filterChanged() {
    }

    @Override
    public void refreshAll() {
    }

    @Override
    public void refreshItem(T item) {
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public int indexOf(T object) {
        return 0;
    }

    @Override
    public T getItem(int index) {
        return null;
    }

    @Override
    public int getObjectCount() {
        return 0;
    }

    @Override
    public Stream<T> streamAll() {
        return null;
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return false;
    }

    private static class Supplier<T extends StoredObject> {

        private Supplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        }

        private class Fetcher implements CallbackDataProvider.FetchCallback<T, String> {

            @Override
            public Stream<T> fetch(Query<T, String> query) {
                return null;
            }
        }

        private class Counter implements CallbackDataProvider.CountCallback<T, String> {

            @Override
            public int count(Query<T, String> query) {
                return 0;
            }
        }
    }
}
