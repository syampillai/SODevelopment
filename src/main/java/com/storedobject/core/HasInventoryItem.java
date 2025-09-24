package com.storedobject.core;

/**
 * Represents an entity that has an associated inventory item and provides methods to access it.
 *
 * @author Syam
 */
public interface HasInventoryItem extends HasInventoryItemType {
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

    @Override
    default InventoryItemType getInventoryItemType() {
        InventoryItem item = getItem();
        return item == null ? null : item.getPartNumber();
    }

    /**
     * Retrieves the inventory item from the entity's history if applicable.
     * If the current object is a stored entity (e.g., an instance of StoredObject),
     * it attempts to find a historical version of the associated inventory item.
     * If found, it returns the historical inventory item; otherwise, it returns
     * the current inventory item.
     *
     * @return the historical InventoryItem if available and valid, or the current
     *         InventoryItem if no historical version exists.
     */
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
