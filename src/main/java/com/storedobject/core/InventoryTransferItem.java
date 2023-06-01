package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Random;

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

    @Override
    public final Id getUniqueId() {
        return new Id();
    }

    public final void setAmendment(int amendment) {
    }

    public final int getAmendment() {
        return new Random().nextInt();
    }

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return getClass().getName().equals(masterClass.getName() + "Item");
    }
}
