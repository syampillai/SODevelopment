package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectListProvider<T extends StoredObject> extends CallbackDataProvider<T, String> implements ObjectDataProvider<T> {

    public ObjectListProvider(Class<T> objectClass) {
        this(new ArrayList<>());
    }

    public ObjectListProvider(List<T> list) {
        this(new Supplier<>(list));
    }

    private ObjectListProvider(Supplier<T> supplier) {
        super(supplier.new Fetcher(), supplier.new Counter(), StoredObject::getId);
    }

    @Override
    public Object getId(T item) {
        return null;
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    public void setAllowAny(boolean allowAny) {
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public void setFilter(Predicate<T> filter) {
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public final void load(String condition, String orderBy) {
    }

    @Override
    public final void load(int linkType, StoredObject master, String condition, String orderBy) {
    }

    @Override
    public void load(ObjectIterator<T> objects) {
    }

    public boolean contains(T item) {
        return false;
    }

    public final boolean add(T item) {
        return false;
    }

    public boolean add(int index, T item) {
        return true;
    }

    public boolean update(T item) {
        return true;
    }

    public boolean delete(T item) {
        return false;
    }

    public boolean delete(int index) {
        return false;
    }

    public void reloadAll() {
    }

    public void reload(T item) {
    }

    public void clear() {
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return null;
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public boolean isInMemory() {
        return true;
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

    public int size() {
        return 0;
    }

    public Stream<T> streamAll() {
        return null;
    }

    public Stream<T> streamFiltered() {
        return null;
    }

    private static class Supplier<T extends StoredObject> {

        private Supplier(List<T> list) {
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