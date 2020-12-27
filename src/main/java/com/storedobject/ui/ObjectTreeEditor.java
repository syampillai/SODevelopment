package com.storedobject.ui;

import com.storedobject.core.ApplicationServer;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;

import java.lang.reflect.Constructor;

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
        setSplitView();
    }

    @SuppressWarnings("unchecked")
    public ObjectTreeEditor(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                ObjectBrowser.actions(className, Application.get().getServer().isDeveloper()),
                Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className));
    }

    public static <O extends StoredObject> ObjectTreeEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectTreeEditor<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "TreeEditor"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance(columns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance(columns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance(actions);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeEditor<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectTreeEditor<>(objectClass, columns, actions, title);
    }
}
