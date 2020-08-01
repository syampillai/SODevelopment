package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryAssembly;
import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.storedobject.vaadin.DataTreeGrid;

public class DefineAssembly<T extends InventoryItemType, C extends InventoryItemType> extends DataTreeGrid<InventoryAssembly> implements Transactional {

    protected Button add;
    protected Button edit;
    protected ConfirmButton delete;
    protected Button selectRoot;

    public DefineAssembly(Class<T> itemTypeClass) {
        this(itemTypeClass, null);
    }

    public DefineAssembly(Class<T> itemTypeClass, Class<C> componentTypeClass) {
        super(InventoryAssembly.class);
    }

    public DefineAssembly(T itemType) {
        //noinspection unchecked
        this((Class<T>) itemType.getClass(), null);
    }

    public DefineAssembly(String itemTypeClass) {
        this(null, null);
    }

    public void setTopLevelItem(T itemType) {
    }
}