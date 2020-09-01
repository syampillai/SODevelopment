package com.storedobject.ui;

import com.storedobject.core.ApplicationServer;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

import java.lang.reflect.Constructor;

public class ObjectForestEditor<T extends StoredObject> extends ObjectForestViewer<T> {

    public ObjectForestEditor(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectForestEditor(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, any, null);
    }

    public ObjectForestEditor(Class<T> objectClass, String caption) {
        this(objectClass, false, caption);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns, String caption) {
        this(objectClass, columns, false, caption);
    }

    public ObjectForestEditor(Class<T> objectClass, boolean any, String caption) {
        this(objectClass, null, any, caption);
    }

    public ObjectForestEditor(Class<T> objectClass, Iterable<String> columns, boolean any, String caption) {
        super(objectClass, columns);
    }

    public ObjectForestEditor(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className));
        load();
    }

    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, boolean any, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), any, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, Iterable<String> columns, boolean any, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "ForestViewer"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, boolean.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(columns, any, title);
            }
            try {
                c = logic.getConstructor(boolean.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(any, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, boolean.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(columns, any);
            }
            try {
                c = logic.getConstructor(boolean.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(any);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectForestEditor<>(objectClass, columns, any, title);
    }
}