package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryPOItem;
import com.storedobject.ui.ObjectBrowser;

import static com.storedobject.core.EditorAction.ALL;

public class POItemBrowser<T extends InventoryPOItem> extends ObjectBrowser<T> {

    public POItemBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public POItemBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    public POItemBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public POItemBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, null);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    POItemBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
              String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    public POItemBrowser() {
        this((String)null);
    }

    public POItemBrowser(String caption) {
        this(ALL, caption);
    }

    public POItemBrowser(Iterable<String> browseColumns) {
        this(browseColumns, ALL);
    }

    public POItemBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(browseColumns, ALL, filterColumns);
    }

    public POItemBrowser(int actions) {
        this(actions, null);
    }

    public POItemBrowser(int actions, String caption) {
        this((Iterable<String>)null, actions, caption);
    }

    public POItemBrowser(Iterable<String> browseColumns, int actions) {
        this(browseColumns, actions, null, null);
    }

    public POItemBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(browseColumns, actions, filterColumns, null);
    }

    public POItemBrowser(Iterable<String> browseColumns, int actions, String caption) {
        this(browseColumns, actions, null, caption);
    }

    public POItemBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        //noinspection unchecked
        this((Class<T>)InventoryPOItem.class, browseColumns, actions, filterColumns, caption, null);
    }
}
