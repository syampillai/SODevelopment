package com.storedobject.ui;

import com.storedobject.core.ApplicationServer;
import com.storedobject.core.EditorAction;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataProvider;

import java.lang.reflect.Constructor;

public class ObjectBrowserViewer<T extends StoredObject> extends ObjectBrowser<T> {

    public ObjectBrowserViewer(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectBrowserViewer(Class<T> objectClass, String caption) {
        this(objectClass, null, caption);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, (String)null);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, EditorAction.RELOAD, filterColumns);
        setSplitView();
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, String caption) {
        this(objectClass, browseColumns, null, caption);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns, String caption) {
        super(objectClass, browseColumns, EditorAction.RELOAD, filterColumns, caption);
        setSplitView();
    }

    public ObjectBrowserViewer(Class<T> objectClass, String caption, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, caption, dataProvider);
    }

    public ObjectBrowserViewer(Class<T> objectClass, Iterable<String> browseColumns, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, EditorAction.RELOAD, caption, dataProvider);
        setSplitView();
    }

    public ObjectBrowserViewer(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)),
                Application.get().getRunningLogic().getTitle());
    }

    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass) {
        return create(objectClass, null);
    }

    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass, String title) {
        return create(objectClass, null, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectBrowserViewer<O> create(Class<O> objectClass, Iterable<String> browseColumns, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "Browser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserViewer<O>) c.newInstance(browseColumns);
            }
            try {
                c = logic.getConstructor(String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserViewer<O>) c.newInstance(title);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserViewer<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectBrowserViewer<>(objectClass, browseColumns, title);
    }
}
