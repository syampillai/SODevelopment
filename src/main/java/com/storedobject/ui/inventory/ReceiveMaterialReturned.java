package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;

public final class ReceiveMaterialReturned extends AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public ReceiveMaterialReturned() {
        super(MaterialReturned.class, MaterialReturnedItem.class, (String) null, true);
    }

    public ReceiveMaterialReturned(String to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }

    public ReceiveMaterialReturned(InventoryLocation to) {
        super(MaterialReturned.class, MaterialReturnedItem.class, to, true);
    }
}
