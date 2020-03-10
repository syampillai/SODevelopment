package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;

import static com.storedobject.core.EditorAction.ALL;
import static com.storedobject.core.EditorAction.ALLOW_ANY;

public class ObjectForestBrowser<T extends StoredObject> extends ObjectForest<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, reload, view, report, excel, audit, exit;

    public ObjectForestBrowser(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectForestBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectForestBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectForestBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        this(objectClass, columns, actions, null, caption, null);
    }

    ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(objectClass, columns, (actions & ALLOW_ANY) == ALLOW_ANY);
    }

    public ObjectForestBrowser(String className) throws Exception {
        //noinspection unchecked
        this((Class<T>) JavaClassLoader.getLogic(className), null, 0, null, null, null);
    }

    public static <O extends StoredObject> ObjectForestBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return null;
    }

    public static <O extends StoredObject> ObjectForestBrowser<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        return null;
    }

    public ObjectSearchBuilder<T> createSearchBuilder(StringList searchColumns) {
        return null;
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

    protected boolean canDelete(@SuppressWarnings("unused") StoredObject object) {
        return true;
    }

    protected boolean canEdit(@SuppressWarnings("unused") StoredObject object) {
        return true;
    }

    protected boolean canAdd(@SuppressWarnings("unused") StoredObject parentObject) {
        return true;
    }
}
