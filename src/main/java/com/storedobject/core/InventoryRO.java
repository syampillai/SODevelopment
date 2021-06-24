package com.storedobject.core;

public final class InventoryRO extends InventoryTransfer {

    public InventoryRO() {
    }

    public static void columns(Columns columns) {
    }

    public Entity getRepairEntity() {
        return new Entity();
    }
}
