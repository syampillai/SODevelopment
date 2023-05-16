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

    public String getItemType() {
        InventoryItem item = getItem();
        return item.isConsumable() ? "Consumable" : (item.isRepairAllowed() ? "For repair" : "?");
    }

    public void setCostOfRepair(Object moneyValue) {
    }

    public Money getCostOfRepair() {
        return new Money();
    }
}
