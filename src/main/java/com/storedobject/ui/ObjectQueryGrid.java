package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.ObjectsSetter;
import com.storedobject.core.StoredObject;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectQueryGrid<T extends StoredObject> extends DataGrid<T> implements ObjectsSetter, Transactional, ObjectChangedListener<T> {

    public ObjectQueryGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectQueryGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectQueryGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectQueryGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, columns);
    }

    public void setKeepCache(boolean keepCache) {
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getDataClass();
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    public void setOrderBy(String orderBy) {
    }

    public String getOrderBy() {
        return null;
    }

    public void setLoadCondition(String loadCondition) {
    }

    public String getLoadCondition() {
        return null;
    }

    public void load() {
    }

    public void load(String filterClause) {
    }

    public void load(String filterClause, String orderBy) {
    }

    public void load(StoredObject master) {
    }

    public void load(StoredObject master, String filterClause, String orderBy) {
    }

    public void load(int linkType, StoredObject master) {
    }

    public void load(int linkType, StoredObject master, String filterClause, String orderBy) {
    }

    public void load(Stream<T> objects) {
    }

    public void load(Iterator<T> objects) {
    }

    public void load(ObjectIterator<T> objects) {
    }

    public void load(Iterable<T> objects) {
    }

    boolean isFullyLoaded() {
        return false;
    }

    public void setFilter(String filterClause) {
    }

    public void setFilter(FilterProvider filterProvider) {
    }

    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    public void filter(Predicate<T> filter) {
    }

    public ObjectSearchFilter getFilter() {
        return null;
    }

    public void scrollTo(T object) {
    }

    public T getItem(int index) {
        return null;
    }

    @Override
    public final void updated(ObjectMasterData<T> object) {
    }

    @Override
    public final void inserted(ObjectMasterData<T> object) {
    }

    @Override
    public final void deleted(ObjectMasterData<T> object) {
    }

    @Override
    public void setObject(StoredObject object) {
    }

    @Override
    public void setObjects(Iterable<? extends StoredObject> objects) {
    }

    public void addObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public final boolean isFullyCached() {
        return false;
    }

    public int size() {
        return 0;
    }
}