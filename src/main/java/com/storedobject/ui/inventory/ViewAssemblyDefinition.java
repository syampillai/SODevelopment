package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;

public class ViewAssemblyDefinition<T extends InventoryItemType> extends DefineAssembly<T, T> {

    public ViewAssemblyDefinition(Class<T> itemTypeClass) {
        super(itemTypeClass, null);
        remButtons();
    }

    public ViewAssemblyDefinition(T itemType) {
        super(itemType);
        remButtons();
    }

    public ViewAssemblyDefinition(String itemTypeClass) {
        super(itemTypeClass);
        remButtons();
    }

    private void remButtons() {
        add = null;
        edit = null;
        delete = null;
    }
}
