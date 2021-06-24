package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;

public final class ReceiveMaterialReturned extends AbstractReceiveMaterialReturned {

    public ReceiveMaterialReturned(String to) {
        super(to);
    }

    public ReceiveMaterialReturned(InventoryLocation to) {
        super(to);
    }

    public ReceiveMaterialReturned(InventoryLocation to, InventoryLocation otherLocation) {
        super(to, otherLocation);
    }
}
