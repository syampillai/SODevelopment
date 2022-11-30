package com.storedobject.core;

import java.math.BigDecimal;

public final class MaterialRequestItem extends StoredObject implements Detail {

    public MaterialRequestItem() {
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

    public void setRequested(Quantity requested) {
    }

    public void setRequested(Object value) {
    }

    public Quantity getRequested() {
        return Count.ZERO;
    }

    public void setIssued(Quantity issued) {
    }

    public void setIssued(Object value) {
    }

    public Quantity getIssued() {
        return Count.ZERO;
    }

    public Quantity getBalance() {
        return Count.ZERO;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == MaterialRequest.class;
    }

    public void reduceRequestedQuantity(Transaction transaction, Quantity reduceBy) throws Exception {
        Quantity b = getBalance();
        if(reduceBy.isGreaterThan(b)) {
            throw new SOException("Balance to be issued is only " + b);
        }
    }
}
