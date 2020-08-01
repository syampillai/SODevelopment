package com.storedobject.core;

import java.math.BigDecimal;

/**
 * Represents a "fitment position" (a position where an item can be fitted) of an assembled item.
 *
 * @author Syam
 */
public final class InventoryFitmentPosition extends InventoryLocation {

    /**
     * Constructor.
     */
    public InventoryFitmentPosition() {
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

    public Id getAssemblyId() {
        return new Id();
    }

    public InventoryAssembly getAssembly() {
        return new InventoryAssembly();
    }

    public void setAssembly(Id assemblyId) {
    }

    public void setAssembly(BigDecimal idValue) {
    }

    public void setAssembly(InventoryAssembly assembly) {
    }

    @Override
    public final int getType() {
        return 14;
    }
}
