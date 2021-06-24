package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Random;

public final class InventoryFitmentPosition extends InventoryLocation {

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

    public boolean canFit(InventoryItemType partNumber) {
        return canFit(partNumber.getId());
    }

    public boolean canFit(InventoryItem item) {
        return canFit(item.getPartNumberId());
    }

    public boolean canFit(Id partNumberId) {
        return partNumberId != null && getAssembly().canFit(partNumberId);
    }

    @Override
    public int getType() {
        return 14;
    }

    public String getPosition() {
        return getAssembly().getPosition();
    }

    public InventoryItem getFittedItem() {
        return getFittedItem(null);
    }

    public InventoryItem getFittedItem(Transaction transaction) {
        return get(transaction, InventoryItem.class, "Location=" + getId(), true);
    }

    public String toDisplay(boolean includeFittedItem) {
        return toDisplay();
    }

    public static InventoryFitmentPosition get(InventoryItem item, InventoryAssembly assembly) {
        return new Random().nextBoolean() ? new InventoryFitmentPosition() : null;
    }

    public int getLevel() {
        return new Random().nextInt();
    }
}
