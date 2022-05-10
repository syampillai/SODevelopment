package com.storedobject.core;

import java.math.BigDecimal;

public abstract class InventoryTransferItem extends StoredObject implements Detail, HasInventoryItem {

    public InventoryTransferItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setItem(Id itemId) {
    }

    public void setItem(BigDecimal idValue) {
    }

    public void setItem(InventoryItem item) {
    }

    public Id getItemId() {
        return new Id();
    }

    @Override
    public InventoryItem getItem() {
        return new InventoryItem();
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    @Override
    public Quantity getQuantity() {
        return Count.ONE;
    }
}
