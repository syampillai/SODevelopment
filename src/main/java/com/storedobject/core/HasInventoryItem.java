package com.storedobject.core;

public interface HasInventoryItem {
    InventoryItem getItem();
    default Quantity getQuantity() {
        return getItem().getQuantity();
    }
}
