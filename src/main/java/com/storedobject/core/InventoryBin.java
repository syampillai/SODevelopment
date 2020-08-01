package com.storedobject.core;

import java.math.BigDecimal;

/**
 * Represents a bin within a store where items can be stocked. (There can be a hierarchy of bins within
 * a store and thus, each bin can have a parent bin).
 *
 * @author Syam
 */
public class InventoryBin extends InventoryLocation {

    /**
     * Constructor.
     */
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

    public void setParentLocation(Id parentLocationId) {
    }

    public void setParentLocation(BigDecimal idValue) {
    }

    public void setParentLocation(InventoryBin parentLocation) {
    }

    public Id getParentLocationId() {
        return new Id();
    }

    public InventoryBin getParentLocation() {
        return new InventoryBin();
    }

    @Override
    public final int getType() {
        return 0;
    }

    public static InventoryBin get(String name, InventoryStore store) {
        return new InventoryBin();
    }

    public static ObjectIterator<InventoryBin> list(String name, InventoryStore store) {
        return ObjectIterator.create();
    }
}