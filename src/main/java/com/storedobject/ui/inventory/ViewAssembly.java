package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;

public class ViewAssembly<T extends InventoryItem, C extends InventoryItem> extends Assembly<T, C> {

    public ViewAssembly(Class<T> itemClass) {
        super(itemClass);
        remButtons();
    }

    public ViewAssembly(T item) {
        super(item);
        remButtons();
    }

    public ViewAssembly(InventoryStore store, Class<T> itemClass) {
        super(store, itemClass);
        remButtons();
    }

    public ViewAssembly(InventoryLocation location, Class<T> itemClass) {
        super(location, itemClass);
        remButtons();
    }

    public ViewAssembly(Class<T> itemClass, Class<C> componentClass) {
        super(itemClass, componentClass);
        remButtons();
    }

    public ViewAssembly(T item, Class<C> componentClass) {
        super(item, componentClass);
        remButtons();
    }

    public ViewAssembly(InventoryStore store, Class<T> itemClass, Class<C> componentClass) {
        super(store, itemClass, componentClass);
        remButtons();
    }

    public ViewAssembly(InventoryLocation location, Class<T> itemClass, Class<C> componentClass) {
        super(location, itemClass, componentClass);
        remButtons();
    }

    ViewAssembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass) {
        super(location, item, itemClass, componentClass);
        remButtons();
    }

    public ViewAssembly(String itemClass) {
        super(itemClass);
        remButtons();
    }

    private void remButtons() {
        fit.setVisible(false);
        remove.setVisible(false);
    }
}
