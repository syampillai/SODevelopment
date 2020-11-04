package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.ui.util.ObjectForestSupplier;

public class ObjectForest<T extends StoredObject> extends BaseObjectForest<T> {

    public ObjectForest(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectForest(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectForest(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectForest(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(columns, new ObjectForestSupplier<>(objectClass, null, null, any));
    }

    ObjectForest(Iterable<String> columns, AbstractObjectForestSupplier<T, Void> dataProvider) {
        super(columns, dataProvider);
    }

    @Override
    public  <O extends StoredObject> O selected() {
        //noinspection unchecked
        return (O)getSelected();
    }
}