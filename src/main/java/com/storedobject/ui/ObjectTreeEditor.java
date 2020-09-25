package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

import static com.storedobject.core.EditorAction.ALL;
import static com.storedobject.core.EditorAction.ALLOW_ANY;

public class ObjectTreeEditor<T extends StoredObject> extends ObjectTreeBrowser<T> {

    public ObjectTreeEditor(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectTreeEditor(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectTreeEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectTreeEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        this(objectClass, columns, actions, caption, null);
    }

    public ObjectTreeEditor(Class<T> objectClass, ObjectTreeBuilder treeBuilder) {
        this(objectClass, null, treeBuilder, null);
    }

    public ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder) {
        this(objectClass, columns, treeBuilder, null);
    }

    public ObjectTreeEditor(Class<T> objectClass, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, null, treeBuilder, caption);
    }

    public ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, columns, 0, caption, null, treeBuilder);
    }

    ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions) {
        this(objectClass, columns, actions, caption, allowedActions, ObjectTreeBuilder.create((actions & ALLOW_ANY) == ALLOW_ANY));
    }

    ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions, ObjectTreeBuilder treeBuilder) {
        super(objectClass, columns, actions, caption, allowedActions, treeBuilder);
    }

    public ObjectTreeEditor(String className) throws Exception {
        super(className);
    }

    public static <O extends StoredObject> ObjectTreeEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    public static <O extends StoredObject> ObjectTreeEditor<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        return new ObjectTreeEditor<>(objectClass, columns, actions, title);
    }
}
