package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryAssembly extends StoredObject implements Detail {

    public InventoryAssembly() {
    }

    public static void columns(Columns columns) {
    }

    public void setPosition(String position) {
    }

    public String getPosition() {
        return null;
    }

    public void setItemType(Id itemTypeId) {
    }

    public void setItemType(BigDecimal idValue) {
    }

    public void setItemType(InventoryItemType itemType) {
    }

    public Id getItemTypeId() {
        return null;
    }

    public InventoryItemType getItemType() {
        return null;
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return null;
    }

    @Override
    public Id getUniqueId() {
        return getId();
    }

    @Override
    public void copyValuesFrom(Detail detail) {
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return true;
    }
}
