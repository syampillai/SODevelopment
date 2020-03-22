package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryAssembly;
import com.storedobject.core.InventoryItemType;
import com.storedobject.vaadin.DataTreeGrid;

public class DefineAssembly<T extends InventoryItemType> extends DataTreeGrid<InventoryAssembly> {

    public DefineAssembly(Class<T> itemTypeClass) {
        super(InventoryAssembly.class);
    }

    public DefineAssembly(String itemTypeClass) {
        this((Class<T>)null);
    }
}