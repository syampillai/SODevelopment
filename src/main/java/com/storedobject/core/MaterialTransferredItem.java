package com.storedobject.core;

public final class MaterialTransferredItem extends InventoryTransferItem {

    public MaterialTransferredItem() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MaterialTransferred.class;
    }
}
