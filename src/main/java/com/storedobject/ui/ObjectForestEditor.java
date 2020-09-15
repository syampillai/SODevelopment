package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

import static com.storedobject.core.EditorAction.ALL;

public class ObjectForestEditor<T extends StoredObject> extends ObjectForestBrowser<T> {

    public ObjectForestEditor(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectForestEditor(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectForestEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectForestEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        super(objectClass, columns, actions, null, caption, null);
    }

    public ObjectForestEditor(String className) throws Exception {
        super(className);
    }

    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        return new ObjectForestEditor<>(objectClass, columns, actions, title);
    }

    @Override
    public final void setSplitView() {
    }
}