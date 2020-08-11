package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataTreeGrid;
import com.storedobject.vaadin.HTMLGenerator;

public abstract class AbstractAssembly<T extends InventoryItem, C extends InventoryItem> extends DataTreeGrid<InventoryAssembly> implements Transactional {

    AbstractAssembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass, Iterable<String> columns) {
        super(InventoryAssembly.class, StringList.create(columns));
    }

    InventoryItem item(InventoryAssembly assembly) {
        return null;
    }

    InventoryFitmentPosition position(InventoryAssembly assembly) {
        return null;
    }

    public HTMLGenerator getName(InventoryAssembly assembly) {
        return null;
    }

    public String getPartNumber(InventoryAssembly assembly) {
        return null;
    }

    public String getSerialNumber(InventoryAssembly assembly) {
        return null;
    }

    public Quantity getQuantity(InventoryAssembly assembly) {
        return null;
    }

    public void setItem(T item) {
    }

    abstract FitItem createFitItem(Class<C> itemClass);

    abstract RemoveItem createRemoveItem();

    interface FitItem {
        void setAssembly(InventoryAssembly assembly, Quantity quantityAlreadyFitted);
        void execute();
    }

    interface RemoveItem {
        void setAssembly(InventoryAssembly assembly);
        void execute();
    }
}
