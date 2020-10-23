package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;
import com.vaadin.flow.component.Component;

public class ObjectBrowserViewer<T extends StoredObject> extends ObjectBrowser<T> {

    public ObjectBrowserViewer(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectBrowserViewer(Class<T> objectClass, String caption) {
        super(objectClass, 0, caption);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns) {
        super(objectClass, browseColumns, 0);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, 0, filterColumns);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, String caption) {
        super(objectClass, browseColumns, 0, caption);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns, String caption) {
        super(objectClass, browseColumns, 0, filterColumns, caption);
    }

    public ObjectBrowserViewer(Class<T> objectClass, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, 0, caption, dataProvider);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, 0, caption, dataProvider);
    }

    public ObjectBrowserViewer(String className) throws Exception {
        //noinspection unchecked
        super((Class<T>) JavaClassLoader.getLogic(className), Application.get().getRunningLogic().getTitle());
    }

    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass) {
        return create(objectClass, null);
    }

    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass, String title) {
        return create(objectClass, null, title);
    }

    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass, Iterable<String> browseColumns, String title) {
        return new ObjectBrowserViewer<>(objectClass, browseColumns, title);
    }
}
