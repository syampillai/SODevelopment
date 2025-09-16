package com.storedobject.core;

/**
 * Represents an entity that has an associated inventory item and provides methods to access it.
 *
 * @author Syam
 */
public interface HasInventoryItem {
    /**
     * Retrieves the inventory item associated with this entity.
     *
     * @return the associated InventoryItem instance
     */
    InventoryItem getItem();

    /**
     * Retrieves the quantity of the inventory item associated with this entity.
     *
     * @return the quantity of the associated inventory item
     */
    default Quantity getQuantity() {
        return getItem().getQuantity();
    }

    default InventoryItem getItemFromHistory() {
        InventoryItem item = getItem();
        if(item != null && this instanceof StoredObject so) {
            StoredObject history = item.contemporary(so);
            if(history instanceof InventoryItem) {
                item = (InventoryItem) history;
            }
        }
        return item;
    }
}
