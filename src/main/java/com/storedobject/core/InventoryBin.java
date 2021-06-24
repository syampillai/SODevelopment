package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryBin extends InventoryLocation {

    public InventoryBin() {
    }

    public static void columns(Columns columns) {
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
    }

    public void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return new Id();
    }

    public InventoryStore getStore() {
        return new InventoryStore();
    }

    public void setPickingOrder(int pickingOrder) {
    }

    public int getPickingOrder() {
        return 0;
    }

    public static InventoryBin get(String name, InventoryStore store) {
        return new InventoryBin();
    }

    public static ObjectIterator<InventoryBin> list(String name, InventoryStore store) {
        return ObjectIterator.create();
    }

    @Override
    public final int getType() {
        return 0;
    }

    @Override
    public final Id getEntityId() {
        return Id.ZERO;
    }
}
