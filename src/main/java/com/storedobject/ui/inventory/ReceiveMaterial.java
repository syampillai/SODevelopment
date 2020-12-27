package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;

public final class ReceiveMaterial extends AbstractSendAndReceiveMaterial {

    public ReceiveMaterial() {
        super((String) null, true);
    }

    public ReceiveMaterial(String to) {
        super(to, true);
    }

    public ReceiveMaterial(InventoryLocation to) {
        super(to, true);
    }
}
