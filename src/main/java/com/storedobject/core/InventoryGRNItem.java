package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryGRNItem extends StoredObject implements Detail {

    public InventoryGRNItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setPartNumber(Id partNumberId) {
    }

    public void setPartNumber(BigDecimal idValue) {
    }

    public void setPartNumber(InventoryItemType partNumber) {
    }

    public Id getPartNumberId() {
        return new Id();
    }

    public InventoryItemType getPartNumber() {
        return new InventoryItemType();
    }

    public void setSerialNumber(String serialNumber) {
    }

    public String getSerialNumber() {
        return "";
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return Count.ZERO;
    }

    public void setUnitCost(Money unitCost) {
    }

    public void setUnitCost(Object moneyValue) {
    }

    public Money getUnitCost() {
        return new Money();
    }

    public void setAssembly(Id assemblyId) {
    }

    public void setAssembly(BigDecimal idValue) {
    }

    public void setAssembly(InventoryAssembly assembly) {
    }

    public Id getAssemblyId() {
        return new Id();
    }

    public InventoryAssembly getAssembly() {
        return new InventoryAssembly();
    }

    public void setBin(Id binId) {
    }

    public void setBin(BigDecimal idValue) {
    }

    public void setBin(InventoryBin bin) {
    }

    public Id getBinId() {
        return new Id();
    }

    public InventoryBin getBin() {
        return new InventoryBin();
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

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return true;
    }
}
