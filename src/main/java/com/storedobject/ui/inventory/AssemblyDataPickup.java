package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;

public class AssemblyDataPickup<T extends InventoryItem, C extends InventoryItem> extends Assembly<T, C> {

    public AssemblyDataPickup(Class<T> itemClass) {
        this(itemClass, (Class<C>)null);
    }

    public AssemblyDataPickup(Class<T> itemClass, Iterable<String> columns) {
        this(null, null, itemClass, null, columns);
    }

    public AssemblyDataPickup(T item) {
        this(item, (Class<C>)null);
    }

    public AssemblyDataPickup(T item, Iterable<String> columns) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), null, null);
    }

    public AssemblyDataPickup(InventoryStore store, Class<T> itemClass, Iterable<String> columns) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, null, columns);
    }

    public AssemblyDataPickup(InventoryLocation location, Class<T> itemClass, Iterable<String> columns) {
        this(location, null, itemClass, null, columns);
    }

    public AssemblyDataPickup(Class<T> itemClass, Class<C> componentClass) {
        this(itemClass, componentClass, null);
    }

    public AssemblyDataPickup(Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(null, null, itemClass, componentClass, columns);
    }

    public AssemblyDataPickup(T item, Class<C> componentClass) {
        this(item, componentClass, null);
    }

    public AssemblyDataPickup(T item, Class<C> componentClass, Iterable<String> columns) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), componentClass, null);
    }

    public AssemblyDataPickup(InventoryStore store, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, componentClass, columns);
    }

    public AssemblyDataPickup(InventoryLocation location, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(location, null, itemClass, componentClass, columns);
    }

    AssemblyDataPickup(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        super(location, item, itemClass, componentClass, columns);
        setCaption("Inventory Assembly - Data Pick-up");
    }
}
