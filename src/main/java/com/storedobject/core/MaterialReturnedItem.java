package com.storedobject.core;

import java.math.BigDecimal;

public final class MaterialReturnedItem extends StoredObject implements Detail {

    public MaterialReturnedItem() {
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

    public InventoryItem getItem() {
        return new InventoryItem();
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return Count.ONE;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MaterialReturned.class;
    }
}
