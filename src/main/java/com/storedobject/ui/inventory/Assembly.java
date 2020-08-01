package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataTreeGrid;

public class Assembly<T extends InventoryItem> extends DataTreeGrid<InventoryAssembly> implements Transactional {

    public Assembly(Class<T> itemClass) {
        this(itemClass, null);
    }

    public Assembly(Class<T> itemClass, Iterable<String> columns) {
        this(null, null, itemClass, columns);
    }

    public Assembly(T item) {
        this(item, null);
    }

    public Assembly(T item, Iterable<String> columns) {
        //noinspection unchecked
        this(null, item, (Class<T>) item.getClass(),null);
    }

    public Assembly(InventoryStore store, Class<T> itemClass, Iterable<String> columns) {
        this(store == null ? null : store.getStoreBin(), null, itemClass, columns);
    }

    public Assembly(InventoryLocation location, Class<T> itemClass, Iterable<String> columns) {
        this(location, null, itemClass, columns);
    }

    private Assembly(InventoryLocation location, T item, Class<T> itemClass, Iterable<String> columns) {
        super(InventoryAssembly.class, columns);
    }

    public Assembly(String itemTypeClass) {
        this(createClass(itemTypeClass));
    }

    private static <O extends InventoryItem> Class<O> createClass(String itemClass) {
        try {
            //noinspection unchecked
            return (Class<O>) JavaClassLoader.getLogic(itemClass);
        } catch (Throwable error) {
            throw new SORuntimeException("Invalid Item Class: " + itemClass);
        }
    }

    public void setItem(T item) {
    }
}
