package com.storedobject.core;

public final class MaterialReturnedItem extends InventoryTransferItem {

    public MaterialReturnedItem() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MaterialReturned.class;
    }
}
