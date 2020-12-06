package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public final class InventoryGRN extends StoredObject implements HasChildren {

    private final static String[] statusValues = {
            "Initiated", "Processed", "Closed"
    };
    private final Date date = DateUtility.today();
    private String referenceNumber;
    private Id storeId;
    private Id supplierId;
    private int status = 0;

    public InventoryGRN() {
    }

    public static void columns(Columns columns) {
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
        setStore(new Id(idValue));
    }

    public void setStore(InventoryStore store) {
        setStore(store == null ? null : store.getId());
    }

    public Id getStoreId() {
        return storeId;
    }

    public InventoryStore getStore() {
        return get(InventoryStore.class, storeId, true);
    }

    public void setSupplier(Id supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplier(BigDecimal idValue) {
        setSupplier(new Id(idValue));
    }

    public void setSupplier(Entity supplier) {
        setSupplier(supplier == null ? null : supplier.getId());
    }

    public Id getSupplierId() {
        return supplierId;
    }

    public Entity getSupplier() {
        return get(Entity.class, supplierId);
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusValue() {
        return statusValues[status];
    }

    public boolean isProcessed() {
        return status == 1;
    }

    public boolean isClosed() {
        return status == 2;
    }

    public void process(Transaction transaction) throws Exception {
    }

    public void close(Transaction transaction) throws Exception {
    }
}
