package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryPOItem extends StoredObject implements Detail {

    public InventoryPOItem() {
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
        return Count.ONE;
    }

    public void setUnitPrice(Money unitPrice) {
    }

    public void setUnitPrice(Object moneyValue) {
    }

    public Money getUnitPrice() {
        return new Money();
    }

    public void setReceived(Quantity quantity) {
    }

    public void setReceived(Object value) {
    }

    public Quantity getReceived() {
        return Count.ONE;
    }

    public Quantity getBalance() {
        return Count.ONE;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryPO.class.isAssignableFrom(masterClass);
    }
}
