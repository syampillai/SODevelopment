package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectForestSupplier<T extends StoredObject> extends CallbackDataProvider<Object, String> implements AbstractObjectForestSupplier<T> {

    public ObjectForestSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any);
    }

    @SuppressWarnings("unchecked")
    public ObjectForestSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(new Supplier(linkType, master, objectClass, condition, orderBy, any));
    }

    @SuppressWarnings("unchecked")
    private ObjectForestSupplier(Supplier supplier) {
        super(supplier.new Fetcher(), supplier.new Counter(), null);
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public List<T> listRoots() {
        return null;
    }

    @Override
    public Stream<Object> fetch(Query<Object, String> query) {
        return null;
    }

    @Override
    public int size(Query<Object, String> query) {
        return 0;
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, String> query) {
        return 0;
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> query) {
        return null;
    }

    @Override
    public boolean hasChildren(Object item) {
        return false;
    }

    public static class LinkNode {

        private LinkNode(StoredObjectUtility.Link<?> link, StoredObject parent) {
        }

        public StoredObject getParent() {
            return null;
        }

        public StoredObjectUtility.Link<?> getLink() {
            return null;
        }

        public ArrayList<StoredObject> links() {
            return null;
        }

        public int size() {
            return 0;
        }
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
    public void setFilter(Predicate<T> filter) {
    }

    @Override
    public void setFilter(String filter) {
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public void setFilter(FilterProvider filter) {
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return null;
    }

    @Override
    public void refreshAll() {
    }

    @Override
    public void refreshItem(Object item) {
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public int indexOf(T object) {
        return 0;
    }

    private boolean orderChanged(String orderBy) {
        return false;
    }

    @Override
    public T getItem(int index) {
        return null;
    }

    @Override
    public int getObjectCount() {
        return 0;
    }

    private static class Supplier<T extends StoredObject> {

        private Supplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        }

        private class Fetcher implements CallbackDataProvider.FetchCallback<Object, String> {

            @Override
            public Stream<Object> fetch(Query<Object, String> query) {
                return null;
            }
        }

        private class Counter implements CallbackDataProvider.CountCallback<Object, String> {

            @Override
            public int count(Query<Object, String> query) {
                return 0;
            }
        }
    }
}
