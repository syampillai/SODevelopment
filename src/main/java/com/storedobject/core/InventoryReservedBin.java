package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryReservedBin extends InventoryBin {

    public InventoryReservedBin() {
    }

    public static void columns(Columns columns) {
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

    public void setReservedBy(Id reservedById) {
    }

    public void setReservedBy(BigDecimal idValue) {
    }

    public void setReservedBy(MaterialRequest reservedBy) {
    }

    public Id getReservedById() {
        return new Id();
    }

    public MaterialRequest getReservedBy() {
        return new MaterialRequest();
    }

    public static InventoryReservedBin createFor(Transaction transaction, InventoryBin bin,
                                                 MaterialRequest materialRequest) throws Exception {
        return new InventoryReservedBin();
    }
}
