package com.storedobject.ui;

import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.ui.util.ObjectGridData;
import com.storedobject.ui.util.ObjectSupplier;
import com.storedobject.vaadin.ItemSelectedListener;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.List;
import java.util.function.Predicate;

public class ObjectGrid<T extends StoredObject> extends DataGrid<T> implements ObjectGridData<T> {

    ObjectSetter objectSetter;

    public ObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, new ObjectSupplier<>(objectClass, null, null, any));
    }

    public ObjectGrid(Class<T> objectClass, ObjectDataProvider<T> dataProvider) {
        this(objectClass, null, dataProvider);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns, ObjectDataProvider<T> dataProvider) {
        super(objectClass, columns);
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
    }

    @Override
    public ObjectDataProvider<T> getDataProvider() {
        return null;
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    @Override
    public void setKeepCache(boolean keepCache) {
    }

    @Override
    public void setOrderBy(String orderBy) {
    }

    @Override
    public String getOrderBy() {
        return null;
    }

    @Override
    public void setObjectSetter(ObjectSetter setter) {
    }

    @Override
    public List<ItemSelectedListener<T>> getItemSelectedListeners(boolean create) {
        return null;
    }
}