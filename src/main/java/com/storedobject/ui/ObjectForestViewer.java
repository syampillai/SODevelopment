package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;

import java.lang.reflect.Constructor;

import static com.storedobject.core.EditorAction.ALLOW_ANY;

public class ObjectForestViewer<T extends StoredObject> extends ObjectForestEditor<T> {

    public ObjectForestViewer(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any) {
        this(objectClass, null, any, true);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any, boolean splitView) {
        this(objectClass, null, any, splitView);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, any, null);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any, boolean splitView) {
        this(objectClass, columns, any, null, splitView);
    }

    public ObjectForestViewer(Class<T> objectClass, String caption) {
        this(objectClass, false, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, String caption, boolean splitView) {
        this(objectClass, false, caption, splitView);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, String caption) {
        this(objectClass, columns, false, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, String caption, boolean splitView) {
        this(objectClass, columns, false, caption, splitView);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any, String caption) {
        this(objectClass, null, any, caption);
    }

    public ObjectForestViewer(Class<T> objectClass, boolean any, String caption, boolean splitView) {
        this(objectClass, null, any, caption, splitView);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any, String caption) {
        this(objectClass, columns, any, caption, false);
    }

    public ObjectForestViewer(Class<T> objectClass, Iterable<String> columns, boolean any, String caption,
                              boolean splitView) {
        this(false, objectClass, columns, null, any, caption, splitView);
    }

    ObjectForestViewer(boolean large, Class<T> objectClass, Iterable<String> columns, Iterable<String> filterColumns,
                       boolean any, String caption, boolean splitView) {
        super(large, true, objectClass, columns, any ? ALLOW_ANY : 0, filterColumns, caption,
                null, splitView);
    }

    public ObjectForestViewer(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)));
    }

    public static <O extends StoredObject> ObjectForestViewer<O> create(Class<O> objectClass, boolean any,
                                                                        String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), any, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectForestViewer<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                        boolean any, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "ForestViewer"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, boolean.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance(columns, any, title);
            }
            try {
                c = logic.getConstructor(boolean.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance(any, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, boolean.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance(columns, any);
            }
            try {
                c = logic.getConstructor(boolean.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance(any);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestViewer<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectForestViewer<>(objectClass, columns, any, title);
    }
}