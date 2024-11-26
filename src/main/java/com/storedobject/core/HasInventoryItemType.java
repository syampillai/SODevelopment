package com.storedobject.core;

/**
 * The HasInventoryItemType interface defines a contract for classes that
 * have an associated inventory item type (P/N).
 * Classes implementing this interface should provide the logic to return
 * an InventoryItemType object, representing the type of the inventory item (P/N)
 * they are associated with.
 */
public interface HasInventoryItemType {

    /**
     * Returns the InventoryItemType (P/N) associated with this object.
     *
     * @return the InventoryItemType representing the type of the inventory item (P/N).
     */
    InventoryItemType getInventoryItemType();
}
