package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;

import static com.storedobject.core.EditorAction.ALL;
import static com.storedobject.core.EditorAction.ALLOW_ANY;

public class ObjectTreeBrowser<T extends StoredObject> extends ObjectTree<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, view, report, excel, audit, exit;

    public ObjectTreeBrowser(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectTreeBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        this(objectClass, columns, actions, caption, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, ObjectTreeBuilder treeBuilder) {
        this(objectClass, null, treeBuilder, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder) {
        this(objectClass, columns, treeBuilder, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, null, treeBuilder, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, columns, 0, caption, null, treeBuilder);
    }

    ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions) {
        this(objectClass, columns, actions, caption, allowedActions, ObjectTreeBuilder.create((actions & ALLOW_ANY) == ALLOW_ANY));
    }

    ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions, ObjectTreeBuilder treeBuilder) {
        super(objectClass, columns, treeBuilder);
    }

    @SuppressWarnings("unchecked")
    public ObjectTreeBrowser(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(className), null, 0, null);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return new ObjectTreeBrowser<>(objectClass);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        return new ObjectTreeBrowser<>(objectClass);
    }

    protected boolean isActionAllowed(String action) {
        return false;
    }

    protected void removeAllowedAction(String action) {
    }

    protected int filterActions(int actions) {
        return actions;
    }

    protected void createExtraButtons() {
    }

    protected void addExtraButtons() {
    }

    protected boolean canDelete(@SuppressWarnings("unused") T object) {
        return true;
    }

    protected boolean canEdit(@SuppressWarnings("unused") T object) {
        return true;
    }

    protected boolean canAdd(@SuppressWarnings("unused") T parentObject) {
        return true;
    }
}
