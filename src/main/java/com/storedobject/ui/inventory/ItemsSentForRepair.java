package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryVirtualLocation;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectInput;

public class ItemsSentForRepair<I extends InventoryItem> extends LocationMonitoring<I, InventoryVirtualLocation> {

    public ItemsSentForRepair() {
        this(null);
    }

    public ItemsSentForRepair(Class<I> itemClass) {
        super("Items Sent For repair", itemClass, InventoryVirtualLocation.class);
    }

    @Override
    protected ObjectInput<InventoryVirtualLocation> getLocationField() {
        return new ObjectComboField<>("Repair Agency", InventoryVirtualLocation.class, "Status=0");
    }

    @Override
    protected int getLocationType() {
        return 3;
    }
}
