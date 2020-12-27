package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;

public final class ReturnMaterial extends AbstractSendAndReceiveMaterial {

    public ReturnMaterial() {
        super((String) null, false);
    }

    public ReturnMaterial(String from) {
        super(from, false);
    }

    public ReturnMaterial(InventoryLocation from) {
        super(from, false);
    }
}
