package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;

import static com.storedobject.core.EditorAction.ALL;

public class ObjectBrowserEditor<T extends StoredObject> extends ObjectBrowser<T> {

    public ObjectBrowserEditor(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectBrowserEditor(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    public ObjectBrowserEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectBrowserEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, (String)null);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    public ObjectBrowserEditor(Class<T> objectClass, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, actions, caption, dataProvider);
    }

    public ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, actions, caption, dataProvider);
        setSplitView();
    }

    ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                        String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns);
        setSplitView();
    }

    public ObjectBrowserEditor(String className) throws Exception {
        super(className);
    }

    public static <O extends StoredObject> ObjectBrowserEditor<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectBrowserEditor<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectBrowserEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, null, actions, title);
    }

    public static <O extends StoredObject> ObjectBrowserEditor<O> create(Class<O> objectClass, Iterable<String> browseColumns, int actions, String title) {
        return new ObjectBrowserEditor<>(objectClass, browseColumns, actions, title);
    }
}
