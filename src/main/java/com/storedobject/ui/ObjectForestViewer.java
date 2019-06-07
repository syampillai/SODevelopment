package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectForestViewerSupplier;

public class ObjectForestViewer<T extends StoredObject> extends AbstractObjectForest<T> {

    public ObjectForestViewer(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, columns);
        dataProvider = new ObjectForestViewerSupplier<>(objectClass, null, null, any);
        setDataProvider(dataProvider);
    }
}