package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;

public class Assembly<T extends InventoryItem, C extends InventoryItem> extends AbstractAssembly<T, C> {

    public Assembly(Class<T> itemClass) {
        this(itemClass, (Class<C>)null);
    }

    public Assembly(Class<T> itemClass, Iterable<String> columns) {
        this(null, null, itemClass, null, columns);
    }

    public Assembly(T item) {
        this(item, (Class<C>)null);
    }

    public Assembly(T item, Iterable<String> columns) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), null, null);
    }

    public Assembly(InventoryStore store, Class<T> itemClass, Iterable<String> columns) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, null, columns);
    }

    public Assembly(InventoryLocation location, Class<T> itemClass, Iterable<String> columns) {
        this(location, null, itemClass, null, columns);
    }

    public Assembly(Class<T> itemClass, Class<C> componentClass) {
        this(itemClass, componentClass, null);
    }

    public Assembly(Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(null, null, itemClass, componentClass, columns);
    }

    public Assembly(T item, Class<C> componentClass) {
        this(item, componentClass, null);
    }

    public Assembly(T item, Class<C> componentClass, Iterable<String> columns) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(), componentClass, null);
    }

    public Assembly(InventoryStore store, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, componentClass, columns);
    }

    public Assembly(InventoryLocation location, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        this(location, null, itemClass, componentClass, columns);
    }

    Assembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        super(location, item, itemClass, componentClass, columns);
    }

    @Override
    FitItem createFitItem(Class<C> itemClass) {
        return null;
    }

    @Override
    RemoveItem createRemoveItem() {
        return null;
    }
}
