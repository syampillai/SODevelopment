package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.LogicParser;

import java.lang.reflect.Constructor;

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
        this(objectClass, browseColumns, actions, null, null);
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

    ObjectBrowserEditor(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns);
        setSplitView();
    }

    @SuppressWarnings("unchecked")
    public ObjectBrowserEditor(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                actions(className, Application.get().getServer().isDeveloper()), null,
                Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className));
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

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectBrowserEditor<O> create(Class<O> objectClass,
                                                                         Iterable<String> browseColumns, int actions,
                                                                         String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "BrowserEditor"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance(browseColumns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance(browseColumns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance(actions);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance(browseColumns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowserEditor<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectBrowserEditor<>(objectClass, browseColumns, actions, title);
    }
}
