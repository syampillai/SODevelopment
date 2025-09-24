package com.storedobject.ui.inventory;

import com.storedobject.core.*;

public final class SendItemsForRepair extends SendItemsOut<InventoryRO, InventoryROItem> {

    public SendItemsForRepair() {
        this(SelectStore.get());
    }

    public SendItemsForRepair(String from) {
        super(InventoryRO.class, InventoryROItem.class, from);
    }

    public SendItemsForRepair(InventoryLocation from) {
        super(InventoryRO.class, InventoryROItem.class, from);
    }
}
