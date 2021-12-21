package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryCustodyLocation;
import com.storedobject.core.InventoryItem;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectInput;

public class ToolMonitoring<I extends InventoryItem> extends LocationMonitoring<I, InventoryCustodyLocation> {

    public ToolMonitoring() {
        this(null);
    }

    public ToolMonitoring(Class<I> itemClass) {
        super("Tool Monitoring", itemClass, InventoryCustodyLocation.class);
    }

    @Override
    protected ObjectInput<InventoryCustodyLocation> getLocationField() {
        return new ObjectComboField<>("Custodian", InventoryCustodyLocation.class);
    }
}
