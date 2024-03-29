package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.util.LogicParser;

import java.lang.reflect.Constructor;

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
        this(false, false, objectClass, columns, actions, null, caption,
                null, true);
    }

    ObjectForestEditor(boolean large, boolean forViewing, Class<T> objectClass, Iterable<String> columns, int actions,
                       Iterable<String> filterColumns, String caption, String allowedActions, boolean splitView) {
        super(large, forViewing, objectClass, columns, actions, filterColumns, caption, allowedActions);
        if(splitView) {
            setSplitView();
        }
    }

    @SuppressWarnings("unchecked")
    public ObjectForestEditor(String className) throws Exception {
        this(false, false, (Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)),
                null, ObjectBrowser.actions(className, Application.get().getServer().isDeveloper()),
                null, Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className),
                true);
    }

    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectForestEditor<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                        int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "ForestEditor"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(columns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(columns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestEditor<O>) c.newInstance(actions);
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
        return new ObjectForestEditor<>(objectClass, columns, actions, title);
    }
}