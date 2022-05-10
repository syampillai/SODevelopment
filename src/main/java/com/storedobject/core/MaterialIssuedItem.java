package com.storedobject.core;

import java.math.BigDecimal;

public final class MaterialIssuedItem extends StoredObject implements Detail, HasInventoryItem {

    public MaterialIssuedItem() {
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

    @Override
    public InventoryItem getItem() {
        return new InventoryItem();
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    @Override
    public Quantity getQuantity() {
        return Count.ONE;
    }

    public void setRequest(Id requestId) {
    }

    public void setRequest(BigDecimal idValue) {
    }

    public void setRequest(MaterialRequestItem request) {
    }

    public Id getRequestId() {
        return new Id();
    }

    public MaterialRequestItem getRequest() {
        return new MaterialRequestItem();
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MaterialIssued.class;
    }
}
