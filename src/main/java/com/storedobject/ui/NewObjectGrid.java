package com.storedobject.ui;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;

import java.util.stream.Stream;

public class NewObjectGrid<T extends StoredObject> extends DataGrid<T> implements ObjectLoader<T> {

    private final ObjectCache<T> cache;

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
        cache = new ObjectCache<>(objectClass, any);
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
    public void load(ObjectIterator<T> objects) {
        cache.load(objects);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void reload() {
        cache.reload();
    }

    @Override
    public String getCondition() {
        return cache.getCondition();
    }

    public void setCondition(String condition) {
        cache.setCondition(condition);
    }

    @Override
    public String getOrderBy() {
        return cache.getOrderBy();
    }

    public void setOrderBy(String orderBy) {
        cache.setOrderBy(orderBy);
    }

    @Override
    public boolean isAllowAny() {
        return cache.isAllowAny();
    }

    public void setMaster(StoredObject master) {
        cache.setMaster(master);
    }

    public StoredObject getMaster() {
        return cache.getMaster();
    }

    public void setLinkType(int linkType) {
        cache.setLinkType(linkType);
    }

    public int getLinkType() {
        return cache.getLinkType();
    }

    @Override
    public void added(T item) {
        cache.added(item);
    }

    @Override
    public void edited(T item) {
        cache.edited(item);
    }

    @Override
    public void deleted(T item) {
        cache.deleted(item);
    }
}
