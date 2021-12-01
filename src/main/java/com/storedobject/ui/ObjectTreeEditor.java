package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LogicParser;

import java.lang.reflect.Constructor;

import static com.storedobject.core.EditorAction.ALL;

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

    ObjectTreeEditor(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions) {
        super(objectClass, columns, actions, caption, allowedActions);
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
    public static <O extends StoredObject> ObjectTreeEditor<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                      int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "TreeEditor"));
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
