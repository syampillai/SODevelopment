package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;

public final class ReceiveMaterialTransferred extends AbstractReceiveMaterialTransferred {

    public ReceiveMaterialTransferred() {
        super();
    }

    public ReceiveMaterialTransferred(String to) {
        super(to);
    }

    public ReceiveMaterialTransferred(InventoryLocation to) {
        super(to);
    }
}
