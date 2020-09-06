package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.StringList;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.ObjectsSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractObjectForest<T extends StoredObject> extends DataTreeGrid<Object> implements ObjectsSetter<T>, Transactional {

    private AbstractObjectForestSupplier<T, Void> dataProvider;

    public AbstractObjectForest(Class<T> objectClass) {
        this(objectClass, null);
    }

    public AbstractObjectForest(Class<T> objectClass, Iterable<String> columns) {
        super(Object.class, StringList.create(columns));
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
        //noinspection unchecked
        return (Class<T>) StoredObject.class;
    }
    @Override
    public void setDataProvider(DataProvider<Object, ?> dataProvider) {
    }

    public void setDataSupplier(AbstractObjectForestSupplier<T, Void> dataProvider) {
    }

    public AbstractObjectForestSupplier<T, Void> getDataSupplier() {
        return dataProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return Application.get();
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

    public void loaded() {
    }

    public void clear() {
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
        return new ObjectSearchFilter();
    }

    public void scrollTo(T object) {
    }

    public T getRoot() {
        return listRoots().get(0);
    }

    public List<T> listRoots() {
        return new ArrayList<>();
    }

    public T getItem(int index) {
        return getRoot();
    }

    @Override
    public void setObject(T object) {
    }

    @Override
    public void setObjects(Iterable<T> objects) {
    }

    public final boolean isFullyCached() {
        return false;
    }

    public int size() {
        return 0;
    }

    public Set<StoredObject> getSelectedObjects() {
        return new HashSet<>();
    }
}