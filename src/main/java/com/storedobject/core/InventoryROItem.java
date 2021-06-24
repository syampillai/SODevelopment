package com.storedobject.core;

import java.util.Random;

public final class InventoryROItem extends InventoryTransferItem {

    public InventoryROItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setRepairDetail(String repairDetail) {
    }

    public String getRepairDetail() {
        return new Random().nextBoolean() ? "" : "x";
    }

    public void setCostOfRepair(Money costOfRepair) {
    }

    public void setCostOfRepair(Object moneyValue) {
    }

    public Money getCostOfRepair() {
        return new Money();
    }

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryRO.class.isAssignableFrom(masterClass) &&
                (masterClass.getName() + "Item").equals(getClass().getName());
    }
}
