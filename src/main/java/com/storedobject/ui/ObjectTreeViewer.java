package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

public class ObjectTreeViewer<T extends StoredObject> extends ObjectTreeBrowser<T> {

    public ObjectTreeViewer(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectTreeViewer(Class<T> objectClass, String caption) {
        super(objectClass, 0, caption);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns, 0);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns, String caption) {
        super(objectClass, columns, 0, caption);
    }

    public ObjectTreeViewer(Class<T> objectClass, ObjectTreeBuilder treeBuilder) {
        super(objectClass, treeBuilder);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder) {
        super(objectClass, columns, treeBuilder);
    }

    public ObjectTreeViewer(Class<T> objectClass, ObjectTreeBuilder treeBuilder, String caption) {
        super(objectClass, treeBuilder, caption);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder, String caption) {
        super(objectClass, columns, treeBuilder, caption);
    }

    public ObjectTreeViewer(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className), Application.get().getRunningLogic().getTitle());
    }

    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass) {
        return create(objectClass, null);
    }

    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), title);
    }

    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass, Iterable<String> columns, String title) {
        return new ObjectTreeViewer<>(objectClass, columns, title);
    }
}