package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;

public class ObjectSupplier<T extends StoredObject, F> extends AbstractObjectSupplier<T, T, F> implements ObjectDataProvider<T, F> {

    public ObjectSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any);
    }

    public ObjectSupplier(Class<T> objectClass, String condition, String orderBy, boolean any, boolean load) {
        this(0, null, objectClass, condition, orderBy, any, load);
    }

    public ObjectSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(linkType, master, objectClass, condition, orderBy, any, false);
    }

    @SuppressWarnings("unchecked")
    public ObjectSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any, boolean load) {
        //noinspection rawtypes
        super(new ObjectsCached(linkType, master, objectClass, condition, orderBy, any, true), load);
    }

    @Override
    public void refreshItem(T item) {
        if(item == null) {
            return;
        }
        if(supplier.cache != null) {
            supplier.cache.refresh(item);
        }
        super.refreshItem(item);
    }
}