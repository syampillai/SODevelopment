package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.StringList;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.ObjectsSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.vaadin.DataTreeGrid;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractObjectForest<T extends StoredObject> extends DataTreeGrid<Object> implements ObjectsSetter, Transactional {

    protected AbstractObjectForestSupplier<T> dataProvider;

    public AbstractObjectForest(Class<T> objectClass) {
        this(objectClass, null);
    }

    public AbstractObjectForest(Class<T> objectClass, Iterable<String> columns) {
        super(Object.class, new StringList(columns));
    }

    public void setKeepCache(boolean keepCache) {
    }

    public String get_Name(Object object) {
        return null;
    }

    @Override
    public String getColumnCaption(String columnName) {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return null;
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

    public void load() {
    }

    public void load(String filterClause) {
    }

    public void load(String filterClause, String orderBy) {
    }

    public void load(StoredObject master, String filterClause, String orderBy) {
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

    public void setRoot(T root) {
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

    public T getRoot() {
        return null;
    }

    public List<T> listRoots() {
        return null;
    }

    public T getItem(int index) {
        return null;
    }

    @Override
    public void setObject(StoredObject object) {
    }

    @Override
    public void setObjects(Iterable<? extends StoredObject> objects) {
    }

    public final boolean isFullyCached() {
        return false;
    }

    public int size() {
        return 0;
    }
}