package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Constructor;

public class ObjectTreeViewer<T extends StoredObject> extends ObjectTreeBrowser<T> {

    public ObjectTreeViewer(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectTreeViewer(Class<T> objectClass, String caption) {
        this(objectClass, null, caption);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, null);
    }

    public ObjectTreeViewer(Class<T> objectClass, Iterable<String> columns, String caption) {
        super(objectClass, columns, 0, caption, null);
        init();
    }

    public ObjectTreeViewer(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)),
                Application.get().getRunningLogic().getTitle());
    }

    private void init() {
        addConstructedListener(o -> con());
        setSplitView();
    }

    private void con() {
        protect();
        addItemDoubleClickListener(e -> getObjectEditor().viewObject(e.getItem(), getView(), true));
    }

    @Override
    public Component createHeader() {
        return null;
    }

    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass) {
        return create(objectClass, null);
    }

    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectTreeViewer<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                      String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "TreeViewer"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeViewer<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeViewer<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectTreeViewer<>(objectClass, columns, title);
    }
}
