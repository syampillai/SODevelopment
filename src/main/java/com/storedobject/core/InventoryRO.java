package com.storedobject.core;

public final class InventoryRO extends InventoryTransfer {

    public InventoryRO() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    public boolean getApprovalRequired() {
        return Math.random() > 0.5;
    }

    public Entity getRepairEntity() {
        return new Entity();
    }
}
