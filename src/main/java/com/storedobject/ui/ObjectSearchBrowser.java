package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectDataProvider;
import java.util.function.Consumer;

public class ObjectSearchBrowser<T extends StoredObject> extends ObjectBrowser<T> {

    public ObjectSearchBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectSearchBrowser(Class<T> objectClass, String caption) {
        this(objectClass, null, caption);
    }

    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, (String)null);
    }

    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, filterColumns, null);
    }

    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, String caption) {
        this(objectClass, browseColumns, null, caption);
    }

    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, filterColumns, caption, null);
    }

    public ObjectSearchBrowser(Class<T> objectClass, String caption, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, caption, dataProvider);
    }

    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, EditorAction.SEARCH | EditorAction.RELOAD, caption, dataProvider);
    }

    ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns,
                        String caption, String allowedActions) {
        super(objectClass, browseColumns, EditorAction.SEARCH | EditorAction.RELOAD, filterColumns, caption, allowedActions);
    }

    public ObjectSearchBrowser(String className) throws Exception {
        this((Class<T>) null);
    }

    public static <O extends StoredObject> ObjectSearchBrowser<O> create(Class<O> objectClass, String title, Consumer<O> objectConsumer) {
        return null;
    }

    public static <O extends StoredObject> ObjectSearchBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns, String title, Consumer<O> objectConsumer) {
        return null;
    }
}
