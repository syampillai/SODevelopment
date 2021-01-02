package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;

public final class ReceiveMaterialReturned extends AbstractSendAndReceiveMaterial {

    public ReceiveMaterialReturned() {
        super((String) null, true);
    }

    public ReceiveMaterialReturned(String to) {
        super(to, true);
    }

    public ReceiveMaterialReturned(InventoryLocation to) {
        super(to, true);
    }
}
