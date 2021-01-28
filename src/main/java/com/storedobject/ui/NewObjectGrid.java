package com.storedobject.ui;

import com.storedobject.core.StoredObject;

import java.util.stream.Stream;

public class NewObjectGrid<T extends StoredObject> extends DataGrid<T> implements ObjectLoader<T> {

    private final ObjectCache<T> cache;
    private String condition, orderBy;

    public NewObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public NewObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public NewObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public NewObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, columns);
        cache = new ObjectCache<>(objectClass);
        //noinspection deprecation
        setDataProvider(cache);
    }

    public T getItem(int index) {
        return cache.getItem(index);
    }

    public Stream<T> getItems() {
        return cache.getItems();
    }

    @Override
    public void load(String condition, String orderBy) {
        cache.load(condition, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        cache.load(linkType, master, condition, orderBy);
    }

    @Override
    public void load(Stream<T> objects) {
        cache.load(objects);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
