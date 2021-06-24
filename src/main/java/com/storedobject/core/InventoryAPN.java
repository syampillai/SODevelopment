package com.storedobject.core;

import java.math.BigDecimal;

/**
 * APN (Alternate Part Number) of a "Part Number".
 *
 * @author Syam
 */
public final class InventoryAPN extends StoredObject implements Detail {

    private Id partNumberId;
    private InventoryItemType partNumber;

    public InventoryAPN() {
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
        return partNumberId;
    }

    public InventoryItemType getPartNumber() {
        return partNumber;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryItemType.class.isAssignableFrom(masterClass);
    }
}
