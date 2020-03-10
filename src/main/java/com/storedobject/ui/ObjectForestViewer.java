package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

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
        this(objectClass, columns, any, null);
    }

    public ObjectForestViewer(Class<T> objectClass, String caption) {
        this(objectClass, false, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, String caption) {
        this(objectClass, columns, false, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any, String caption) {
        this(objectClass, null, any, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any, String caption) {
        super(objectClass, columns);
    }

    public ObjectForestViewer(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className));
    }

    public static <O extends StoredObject> ObjectForestViewer<O> create(Class<O> objectClass, boolean any, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), any, title);
    }

    public static <O extends StoredObject> ObjectForestViewer<O> create(Class<O> objectClass, Iterable<String> columns, boolean any, String title) {
        return new ObjectForestViewer<>(objectClass, columns, any, title);
    }
}