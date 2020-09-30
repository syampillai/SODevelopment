package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public class InventoryGRN extends StoredObject implements HasChildren {

    public InventoryGRN() {
    }

    public static void columns(Columns columns) {
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public void setReferenceNumber(String referenceNumber) {
    }

    public String getReferenceNumber() {
        return "";
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

    public void setSupplier(Id supplierId) {
    }

    public void setSupplier(BigDecimal idValue) {
    }

    public void setSupplier(Entity supplier) {
    }

    public Id getSupplierId() {
        return new Id();
    }

    public Entity getSupplier() {
        return new Entity();
    }

    public void setProcessed(boolean processed) {
    }

    public boolean getProcessed() {
        return false;
    }

    public void process(Transaction transaction) throws Exception {
    }
}
