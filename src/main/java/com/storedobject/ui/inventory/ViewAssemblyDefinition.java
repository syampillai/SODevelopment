package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;

public class ViewAssemblyDefinition<T extends InventoryItemType> extends DefineAssembly<T, T> {

    public ViewAssemblyDefinition(Class<T> itemTypeClass) {
        super(itemTypeClass, null);
    }

    public ViewAssemblyDefinition(T itemType) {
        super(itemType);
    }

    public ViewAssemblyDefinition(String itemTypeClass) {
        super(itemTypeClass);
    }
}
