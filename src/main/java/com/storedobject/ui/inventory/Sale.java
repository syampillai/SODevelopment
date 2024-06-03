package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventorySale;
import com.storedobject.core.InventorySaleItem;

public class Sale extends AbstractSale<InventorySale, InventorySaleItem> {

    public Sale() {
        super(InventorySale.class, InventorySaleItem.class, "Sale");
    }

    public Sale(String from) {
        super(InventorySale.class, InventorySaleItem.class, from, "Sale");
    }

    public Sale(InventoryLocation from) {
        super(InventorySale.class, InventorySaleItem.class, from, "Sale");
    }

    @Override
    protected AbstractSale<InventorySale, InventorySaleItem> createInstance(InventoryLocation location) {
        return new Sale(location);
    }
}
